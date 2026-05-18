import argparse
import csv
from datetime import datetime, timezone
import json
import os
import sys
import time
from pathlib import Path

import requests
import yaml


DEFAULT_CSV_COLUMNS = [
    "key",
    "summary",
    "status",
    "assignee",
    "reporter",
    "priority",
    "issue_type",
    "labels",
    "sprint",
    "created",
    "updated",
]

DEFAULT_FIELD_API_PATH = "/rest/api/3/field"


def uniq_keep_order(items: list) -> list:
    seen = set()
    result = []
    for item in items:
        if item not in seen:
            seen.add(item)
            result.append(item)
    return result


def resolve_auth_type(jira: dict) -> str:
    raw_auth_type = str(jira.get("auth_type", "")).strip().lower()
    if not raw_auth_type:
        raw_auth_type = "pat" if jira.get("pat_token") or jira.get("pat_token_env") else "basic"

    if raw_auth_type in {"pat", "bearer", "bearer_pat"}:
        return "pat"
    if raw_auth_type == "basic":
        return "basic"
    raise ValueError("jira.auth_type debe ser 'pat' o 'basic'")


def resolve_pat_token(jira: dict) -> str:
    token = str(jira.get("pat_token", "")).strip()
    if token:
        return token

    env_name = str(jira.get("pat_token_env", "")).strip()
    if env_name:
        return str(os.getenv(env_name, "")).strip()

    return ""


def load_config(config_path: Path, require_jql: bool = True) -> dict:
    if not config_path.exists():
        raise FileNotFoundError(
            f"No existe el archivo de configuracion: {config_path}. "
            "Copia config.example.yaml como config.yaml y completa tus datos."
        )

    with config_path.open("r", encoding="utf-8") as stream:
        config = yaml.safe_load(stream) or {}

    jira = config.get("jira", {})
    query = config.get("query", {})

    auth_type = resolve_auth_type(jira)

    required = {
        "jira.base_url": jira.get("base_url"),
    }

    if auth_type == "pat":
        required["jira.pat_token|jira.pat_token_env"] = resolve_pat_token(jira)
    else:
        required["jira.email"] = jira.get("email")
        required["jira.api_token"] = jira.get("api_token")

    if require_jql:
        required["query.jql"] = query.get("jql")

    missing = [name for name, value in required.items() if not value]
    if missing:
        raise ValueError(f"Faltan variables requeridas en config: {', '.join(missing)}")

    return config


def get_jira_connection(config: dict) -> tuple[str, dict, int, bool]:
    jira = config["jira"]
    base_url = jira["base_url"].rstrip("/")

    auth_type = resolve_auth_type(jira)

    if auth_type == "pat":
        pat_token = resolve_pat_token(jira)
        if not pat_token:
            raise ValueError("No se encontro PAT. Configura jira.pat_token o jira.pat_token_env")
        auth = {
            "type": "pat",
            "pat_token": pat_token,
        }
    elif auth_type == "basic":
        auth = {
            "type": "basic",
            "email": jira["email"],
            "api_token": jira["api_token"],
        }
    else:
        raise ValueError("jira.auth_type debe ser 'pat' o 'basic'")

    timeout_seconds = int(jira.get("timeout_seconds", 30))
    verify_ssl = bool(jira.get("verify_ssl", True))
    return base_url, auth, timeout_seconds, verify_ssl


def get_request_auth(auth: dict) -> tuple[dict, tuple | None]:
    headers = {"Accept": "application/json"}
    if auth["type"] == "pat":
        headers["Authorization"] = f"Bearer {auth['pat_token']}"
        return headers, None
    return headers, (auth["email"], auth["api_token"])


def get_retry_options(config: dict) -> tuple[int, float]:
    jira = config.get("jira", {})
    retry_cfg = jira.get("retry", {})

    max_attempts = int(retry_cfg.get("max_attempts", 3))
    backoff_seconds = float(retry_cfg.get("backoff_seconds", 1.0))

    if max_attempts < 1:
        max_attempts = 1
    if backoff_seconds < 0:
        backoff_seconds = 0.0

    return max_attempts, backoff_seconds


def fetch_issues(config: dict, verbose: bool = False, log_stream=None) -> tuple[list, dict]:
    query = config["query"]

    base_url, auth_cfg, timeout_seconds, verify_ssl = get_jira_connection(config)
    api_path = config["jira"].get("api_path", "/rest/api/3/search")
    endpoint = f"{base_url}{api_path}"

    jql = query["jql"]
    fields = build_requested_fields(config)
    page_size = int(query.get("page_size", 100))

    headers, auth = get_request_auth(auth_cfg)
    issues = []
    start_at = 0
    total = None
    pages_queried = 0
    requests_sent = 0
    retry_attempts = 0
    request_failures = 0
    max_attempts, backoff_seconds = get_retry_options(config)

    while True:
        params = {
            "jql": jql,
            "startAt": start_at,
            "maxResults": page_size,
        }
        if fields:
            params["fields"] = ",".join(fields)

        response = None
        last_exc = None
        for attempt in range(max_attempts):
            try:
                requests_sent += 1
                response = requests.get(
                    endpoint,
                    headers=headers,
                    params=params,
                    auth=auth,
                    timeout=timeout_seconds,
                    verify=verify_ssl,
                )
                response.raise_for_status()
                last_exc = None
                if attempt > 0:
                    vlog(verbose, log_stream, f"[retry-ok] page_start={start_at} attempts={attempt + 1}")
                break
            except requests.RequestException as exc:
                request_failures += 1
                last_exc = exc
                if attempt < (max_attempts - 1):
                    retry_attempts += 1
                    wait_seconds = backoff_seconds * (2**attempt)
                    vlog(
                        verbose,
                        log_stream,
                        (
                            f"[retry] page_start={start_at} attempt={attempt + 1}/{max_attempts} "
                            f"wait_s={round(wait_seconds, 3)} error={exc}"
                        ),
                    )
                    if wait_seconds > 0:
                        time.sleep(wait_seconds)

        if last_exc is not None:
            raise last_exc

        payload = response.json()
        pages_queried += 1

        batch = payload.get("issues", [])
        issues.extend(batch)

        if total is None:
            total = int(payload.get("total", 0))

        vlog(
            verbose,
            log_stream,
            (
                f"[page] #{pages_queried} fetched={len(batch)} "
                f"accumulated={len(issues)} total={total} startAt={start_at}"
            ),
        )

        if not batch or len(issues) >= total:
            break

        start_at += len(batch)

    stats = {
        "pages_queried": pages_queried,
        "requests_sent": requests_sent,
        "retry_attempts": retry_attempts,
        "request_failures": request_failures,
        "avg_issues_per_page": round((len(issues) / pages_queried), 3) if pages_queried > 0 else 0.0,
    }

    return issues, stats


def fetch_fields_metadata(config: dict) -> list:
    base_url, auth_cfg, timeout_seconds, verify_ssl = get_jira_connection(config)
    api_path = config.get("jira", {}).get("field_api_path", DEFAULT_FIELD_API_PATH)
    endpoint = f"{base_url}{api_path}"

    headers, auth = get_request_auth(auth_cfg)

    response = requests.get(
        endpoint,
        headers=headers,
        auth=auth,
        timeout=timeout_seconds,
        verify=verify_ssl,
    )
    response.raise_for_status()
    payload = response.json()
    if isinstance(payload, list):
        return payload
    raise ValueError("Respuesta inesperada al consultar campos de Jira")


def find_field_id_by_name(fields_meta: list, contains_text: str) -> str:
    needle = contains_text.lower()
    for field in fields_meta:
        name = str(field.get("name", "")).lower()
        field_id = str(field.get("id", "")).strip()
        if needle in name and field_id:
            return field_id
    return ""


def build_csv_template_payload(fields_meta: list) -> dict:
    sprint_id = find_field_id_by_name(fields_meta, "sprint") or "customfield_10020"

    epic_id = ""
    for candidate in ["epic link", "epic"]:
        epic_id = find_field_id_by_name(fields_meta, candidate)
        if epic_id:
            break
    if not epic_id:
        epic_id = "customfield_10014"

    return {
        "output": {
            "csv": {
                "sprint_field": sprint_id,
                "columns": [
                    "key",
                    "summary",
                    "status",
                    "assignee",
                    "reporter",
                    "priority",
                    "issue_type",
                    "labels",
                    "sprint",
                    "epic_link",
                    "created",
                    "updated",
                ],
                "custom_fields": {
                    "epic_link": epic_id,
                },
            }
        }
    }


def print_fields_table(fields_meta: list) -> None:
    fields_sorted = sorted(fields_meta, key=lambda x: str(x.get("name", "")).lower())
    print("id,name,custom")
    for field in fields_sorted:
        field_id = str(field.get("id", "")).replace(",", " ")
        name = str(field.get("name", "")).replace(",", " ")
        is_custom = str(field.get("custom", False)).lower()
        print(f"{field_id},{name},{is_custom}")


def ensure_parent_dir(file_path: Path) -> None:
    file_path.parent.mkdir(parents=True, exist_ok=True)


def get_csv_options(config: dict) -> tuple[list, dict, str]:
    output_cfg = config.get("output", {})
    csv_cfg = output_cfg.get("csv", {})

    columns = csv_cfg.get("columns") or DEFAULT_CSV_COLUMNS
    custom_fields = csv_cfg.get("custom_fields") or {}
    sprint_field = str(csv_cfg.get("sprint_field", "sprint")).strip() or "sprint"

    return columns, custom_fields, sprint_field


def get_metadata_options(config: dict) -> tuple[bool, Path]:
    output_cfg = config.get("output", {})
    metadata_cfg = output_cfg.get("metadata", {})

    enabled = bool(metadata_cfg.get("enabled", False))
    metadata_file = metadata_cfg.get("file_path", "output/metadata.json")
    metadata_path = Path(metadata_file)

    if not metadata_path.is_absolute():
        metadata_path = (Path.cwd() / metadata_path).resolve()

    return enabled, metadata_path


def get_verbose_log_file_option(config: dict) -> str:
    output_cfg = config.get("output", {})
    return str(output_cfg.get("verbose_log_file", "")).strip()


def vlog(verbose: bool, log_stream, message: str) -> None:
    if verbose:
        print(message)
    if log_stream is not None:
        log_stream.write(message + "\n")
        log_stream.flush()


def build_requested_fields(config: dict) -> list:
    query = config.get("query", {})
    query_fields = query.get("fields") or []

    output_cfg = config.get("output", {})
    output_format = str(output_cfg.get("format", "json")).lower().strip()

    if output_format != "csv":
        return uniq_keep_order(query_fields)

    columns, custom_fields, sprint_field = get_csv_options(config)

    csv_required = []
    for col in columns:
        if col == "key":
            continue
        if col in {"summary", "status", "assignee", "reporter", "created", "updated", "priority", "labels"}:
            csv_required.append(col)
        elif col == "issue_type":
            csv_required.append("issuetype")
        elif col == "sprint":
            csv_required.append(sprint_field)
        elif col in custom_fields:
            csv_required.append(str(custom_fields[col]))

    return uniq_keep_order(query_fields + csv_required)


def normalize_field_value(value) -> str:
    if value is None:
        return ""
    if isinstance(value, (str, int, float, bool)):
        return str(value)
    if isinstance(value, list):
        return "; ".join([normalize_field_value(item) for item in value if item is not None])
    if isinstance(value, dict):
        for key in ["displayName", "name", "value", "key", "id"]:
            if key in value and value[key] is not None:
                return str(value[key])
        return json.dumps(value, ensure_ascii=False)
    return str(value)


def issue_to_csv_row(issue: dict, columns: list, custom_fields: dict, sprint_field: str) -> dict:
    fields = issue.get("fields", {})
    row = {}

    for col in columns:
        if col == "key":
            row[col] = issue.get("key", "")
        elif col == "summary":
            row[col] = normalize_field_value(fields.get("summary"))
        elif col == "status":
            row[col] = normalize_field_value(fields.get("status"))
        elif col == "assignee":
            row[col] = normalize_field_value(fields.get("assignee"))
        elif col == "reporter":
            row[col] = normalize_field_value(fields.get("reporter"))
        elif col == "created":
            row[col] = normalize_field_value(fields.get("created"))
        elif col == "updated":
            row[col] = normalize_field_value(fields.get("updated"))
        elif col == "priority":
            row[col] = normalize_field_value(fields.get("priority"))
        elif col == "issue_type":
            row[col] = normalize_field_value(fields.get("issuetype"))
        elif col == "labels":
            labels = fields.get("labels") or []
            row[col] = "; ".join([str(label) for label in labels])
        elif col == "sprint":
            row[col] = normalize_field_value(fields.get(sprint_field))
        elif col in custom_fields:
            jira_field = str(custom_fields[col])
            row[col] = normalize_field_value(fields.get(jira_field))
        else:
            row[col] = normalize_field_value(fields.get(col))

    return row


def save_json(issues: list, out_path: Path) -> None:
    ensure_parent_dir(out_path)
    with out_path.open("w", encoding="utf-8") as stream:
        json.dump(issues, stream, indent=2, ensure_ascii=False)


def save_csv(issues: list, out_path: Path, columns: list, custom_fields: dict, sprint_field: str) -> None:
    ensure_parent_dir(out_path)
    rows = [issue_to_csv_row(issue, columns, custom_fields, sprint_field) for issue in issues]

    with out_path.open("w", encoding="utf-8", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=columns)
        writer.writeheader()
        writer.writerows(rows)


def save_metadata(metadata: dict, metadata_path: Path) -> None:
    ensure_parent_dir(metadata_path)
    with metadata_path.open("w", encoding="utf-8") as stream:
        json.dump(metadata, stream, indent=2, ensure_ascii=False)


def parse_jira_datetime(value: str):
    if not value:
        return None
    normalized = value.replace("Z", "+00:00")
    try:
        return datetime.fromisoformat(normalized)
    except ValueError:
        return None


def compute_issue_date_ranges(issues: list) -> dict:
    created_values = []
    updated_values = []

    for issue in issues:
        fields = issue.get("fields", {})
        created_dt = parse_jira_datetime(str(fields.get("created", "")))
        updated_dt = parse_jira_datetime(str(fields.get("updated", "")))

        if created_dt is not None:
            created_values.append(created_dt)
        if updated_dt is not None:
            updated_values.append(updated_dt)

    return {
        "issues_created_min": min(created_values).isoformat() if created_values else None,
        "issues_created_max": max(created_values).isoformat() if created_values else None,
        "issues_updated_min": min(updated_values).isoformat() if updated_values else None,
        "issues_updated_max": max(updated_values).isoformat() if updated_values else None,
    }


def main() -> int:
    parser = argparse.ArgumentParser(description="Extrae issues de Jira usando JQL desde config.yaml")
    parser.add_argument("--config", default="config.yaml", help="Ruta al archivo de configuracion")
    parser.add_argument(
        "--list-fields",
        action="store_true",
        help="Lista todos los campos disponibles en Jira (id,name,custom)",
    )
    parser.add_argument(
        "--generate-csv-template",
        nargs="?",
        const="output/csv_template.yaml",
        metavar="RUTA",
        help="Genera una plantilla YAML con mapeo sugerido de campos CSV",
    )
    parser.add_argument(
        "--verbose",
        action="store_true",
        help="Muestra progreso por pagina y reintentos en consola",
    )
    parser.add_argument(
        "--verbose-log-file",
        nargs="?",
        const="output/run.log",
        metavar="RUTA",
        help="Guarda logs de progreso en archivo (por defecto output/run.log)",
    )
    args = parser.parse_args()

    log_stream = None
    log_path = None

    try:
        config_path = Path(args.config).resolve()

        if args.list_fields or args.generate_csv_template is not None:
            config = load_config(config_path, require_jql=False)
            fields_meta = fetch_fields_metadata(config)

            if args.list_fields:
                print_fields_table(fields_meta)

            if args.generate_csv_template is not None:
                template_path = Path(args.generate_csv_template)
                if not template_path.is_absolute():
                    template_path = (Path.cwd() / template_path).resolve()

                ensure_parent_dir(template_path)
                template_payload = build_csv_template_payload(fields_meta)
                with template_path.open("w", encoding="utf-8") as stream:
                    yaml.safe_dump(template_payload, stream, sort_keys=False, allow_unicode=False)

                print(f"Plantilla CSV generada: {template_path}")

            return 0

        config = load_config(config_path, require_jql=True)

        cli_log_file = args.verbose_log_file if args.verbose_log_file is not None else ""
        config_log_file = get_verbose_log_file_option(config)
        resolved_log_file = cli_log_file or config_log_file

        if resolved_log_file:
            log_path = Path(resolved_log_file)
            if not log_path.is_absolute():
                log_path = (Path.cwd() / log_path).resolve()
            ensure_parent_dir(log_path)
            log_stream = log_path.open("w", encoding="utf-8")
            vlog(args.verbose, log_stream, f"[log] writing verbose output to {log_path}")

        run_started = datetime.now(timezone.utc)
        issues, fetch_stats = fetch_issues(config, verbose=args.verbose, log_stream=log_stream)

        output_cfg = config.get("output", {})
        output_format = str(output_cfg.get("format", "json")).lower().strip()
        output_file = output_cfg.get("file_path", "output/issues.json")
        out_path = Path(output_file)
        columns, custom_fields, sprint_field = get_csv_options(config)
        metadata_enabled, metadata_path = get_metadata_options(config)

        if not out_path.is_absolute():
            out_path = (Path.cwd() / out_path).resolve()

        if output_format == "csv":
            save_csv(issues, out_path, columns, custom_fields, sprint_field)
        elif output_format == "json":
            save_json(issues, out_path)
        else:
            raise ValueError("output.format debe ser 'json' o 'csv'")

        if metadata_enabled:
            run_finished = datetime.now(timezone.utc)
            issue_ranges = compute_issue_date_ranges(issues)
            metadata_payload = {
                "generated_at_utc": run_finished.isoformat(),
                "execution_seconds": round((run_finished - run_started).total_seconds(), 3),
                "total_issues": len(issues),
                "jql": config.get("query", {}).get("jql", ""),
                "output_format": output_format,
                "output_file": str(out_path),
            }
            if log_path is not None:
                metadata_payload["verbose_log_file"] = str(log_path)
            metadata_payload.update(issue_ranges)
            metadata_payload.update(fetch_stats)
            save_metadata(metadata_payload, metadata_path)
            print(f"Metadata generado: {metadata_path}")

        print(f"Issues extraidos: {len(issues)}")
        print(f"Archivo generado: {out_path}")
        return 0

    except Exception as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1
    finally:
        if log_stream is not None:
            log_stream.close()


if __name__ == "__main__":
    raise SystemExit(main())
