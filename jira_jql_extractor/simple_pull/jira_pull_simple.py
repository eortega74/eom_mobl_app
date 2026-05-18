import json
import os
import sys

import requests


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

        url = f"{base_url}/rest/api/2/search"
        headers = {
            "Authorization": f"Bearer {token}",
            "Accept": "application/json",
        }
        params = {
            "jql": jql,
            "maxResults": 50,
            "fields": "key,summary,status,assignee,created,updated",
        }

        response = requests.get(url, headers=headers, params=params, timeout=30)
        response.raise_for_status()
        data = response.json()

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
    except Exception as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
