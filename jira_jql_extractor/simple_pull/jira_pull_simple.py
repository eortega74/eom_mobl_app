import json
import os
import sys

import requests
import urllib3


def required_env(name: str) -> str:
    value = os.environ.get(name, "").strip()
    if not value:
        raise ValueError(f"Missing required environment variable: {name}")
    return value


def main() -> int:
    try:
        base_url = required_env("JIRA_BASE_URL").rstrip("/")
        token = required_env("JIRA_PAT_TOKEN")
        jql = os.environ.get("JIRA_JQL", "project = ABC ORDER BY created DESC").strip()
        api_path = os.environ.get("JIRA_API_PATH", "/rest/api/latest/search").strip() or "/rest/api/latest/search"
        timeout_seconds = int(os.environ.get("JIRA_TIMEOUT_SECONDS", "30"))
        verify_ssl_env = os.environ.get("JIRA_VERIFY_SSL", "true").strip().lower()
        verify_ssl = verify_ssl_env not in {"0", "false", "no", "off"}

        if not verify_ssl:
            urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

        if not api_path.startswith("/"):
            api_path = f"/{api_path}"

        url = f"{base_url}{api_path}"
        headers = {
            "Authorization": f"Bearer {token}",
            "Accept": "application/json",
        }
        params = {
            "jql": jql,
            "maxResults": 50,
            "fields": "key,summary,status,assignee,created,updated",
        }

        response = requests.get(
            url,
            headers=headers,
            params=params,
            timeout=timeout_seconds,
            verify=verify_ssl,
        )
        response.raise_for_status()

        content_type = (response.headers.get("Content-Type") or "").lower()
        if "application/json" not in content_type:
            preview = (response.text or "")[:500]
            print("Unexpected response format from Jira (expected JSON).", file=sys.stderr)
            print(f"Status: {response.status_code}", file=sys.stderr)
            print(f"Content-Type: {response.headers.get('Content-Type', '')}", file=sys.stderr)
            print(f"Body preview: {preview}", file=sys.stderr)
            print(
                "Hint: this is commonly an SSO/login HTML page. Verify PAT validity and endpoint access.",
                file=sys.stderr,
            )
            return 1

        try:
            data = response.json()
        except ValueError as exc:
            preview = (response.text or "")[:500]
            print(f"JSON parse error: {exc}", file=sys.stderr)
            print(f"Status: {response.status_code}", file=sys.stderr)
            print(f"Content-Type: {response.headers.get('Content-Type', '')}", file=sys.stderr)
            print(f"Body preview: {preview}", file=sys.stderr)
            return 1

        issues = data.get("issues", [])
        print(f"Issues fetched: {len(issues)}")
        for issue in issues:
            key = issue.get("key", "")
            summary = issue.get("fields", {}).get("summary", "")
            print(f"{key}: {summary}")

        out_file = os.environ.get("JIRA_OUTPUT_FILE", "jira_issues.json").strip() or "jira_issues.json"
        with open(out_file, "w", encoding="utf-8") as stream:
            json.dump(data, stream, ensure_ascii=False, indent=2)

        print(f"Saved output to: {out_file}")
        return 0

    except requests.HTTPError as exc:
        print(f"HTTP error: {exc}", file=sys.stderr)
        if exc.response is not None:
            print(f"Status: {exc.response.status_code}", file=sys.stderr)
            print(f"Body: {exc.response.text}", file=sys.stderr)
        return 1
    except requests.RequestException as exc:
        print(f"Request error type: {exc.__class__.__name__}", file=sys.stderr)
        print(f"Request error: {exc}", file=sys.stderr)
        print(f"Request error repr: {repr(exc)}", file=sys.stderr)
        if getattr(exc, "response", None) is not None:
            print(f"Status: {exc.response.status_code}", file=sys.stderr)
            print(f"Body: {exc.response.text}", file=sys.stderr)
        print(
            "Hint: try API v3 (-ApiPath /rest/api/3/search) or disable SSL verify temporarily (-SkipSslVerify) in corporate networks.",
            file=sys.stderr,
        )
        return 1
    except Exception as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
