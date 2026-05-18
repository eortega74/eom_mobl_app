# Jira JQL Extractor (Python + Docker)

This project exports Jira issues using the Jira REST API and a configurable JQL query.

It supports:
- JSON or CSV output
- Custom CSV columns
- Jira custom fields (for example `customfield_10014`)
- Automatic Jira field discovery (list all fields and generate a CSV mapping template)
- Optional metadata export (run timestamp, issue count, and JQL used)
- Metadata includes execution time and issue date ranges
- Metadata includes pagination and retry/network stats

## How It Works

1. The script reads `config.yaml`.
2. It authenticates against Jira using PAT (Bearer) or email + API token.
3. It executes your JQL in pages (`startAt`, `maxResults`) until all issues are fetched.
4. It writes output as JSON or CSV.
5. For CSV, it maps built-in and custom columns based on `output.csv` settings.
6. If enabled, it also writes a metadata JSON file for auditing/reporting.

## Configuration

Edit `config.yaml`:

- `jira.base_url`: Jira base URL (Cloud or Server)
- `jira.api_path`: search endpoint (default `/rest/api/3/search`)
- `jira.field_api_path`: field metadata endpoint (default `/rest/api/3/field`)
- `jira.auth_type`: `pat` (recommended for Jira Server/DC) or `basic` (Jira Cloud)
- `jira.pat_token_env`: environment variable name that stores the PAT (recommended)
- `jira.pat_token`: personal access token used with `Authorization: Bearer <token>`
- `jira.email`: Jira account email (used only when `auth_type: basic`)
- `jira.api_token`: Jira API token (used only when `auth_type: basic`)
- `jira.retry.max_attempts`: max attempts per Jira request (default `3`)
- `jira.retry.backoff_seconds`: exponential backoff base in seconds (default `1.0`)
- `query.jql`: JQL query
- `query.fields`: fields to request (the script also adds needed fields for CSV)
- `query.page_size`: page size for Jira search
- `output.format`: `csv` or `json`
- `output.file_path`: output file path
- `output.verbose_log_file`: optional file path to store verbose run logs
- `output.metadata.enabled`: enable/disable metadata output
- `output.metadata.file_path`: metadata JSON file path

CSV-specific settings:

- `output.csv.columns`: final CSV columns and order
- `output.csv.sprint_field`: Jira field id used for sprint (often custom)
- `output.csv.custom_fields`: map of CSV column name to Jira field id

## Run Locally (Python)

Set your PAT in an environment variable (recommended):

```powershell
$env:JIRA_PAT_TOKEN = "tu_pat_real"
```

```powershell
cd jira_jql_extractor
pip install -r requirements.txt
python app.py --config config.yaml
```

Verbose run (page-by-page progress and retries):

```powershell
python app.py --config config.yaml --verbose
```

Verbose log file (writes logs even without console verbose):

```powershell
python app.py --config config.yaml --verbose-log-file
```

Custom verbose log file path:

```powershell
python app.py --config config.yaml --verbose --verbose-log-file output/my_run.log
```

## Run with Docker Compose

Create `.env` from the example and set your PAT:

```powershell
cd jira_jql_extractor
Copy-Item .env.example .env
# Edita .env y coloca JIRA_PAT_TOKEN real
```

```powershell
cd jira_jql_extractor
docker compose up --build
```

Quick PAT creation guide (service account user/password):
- See `README-PAT-SERVICE-ACCOUNT.md`

## New: Discover Jira Field IDs Automatically

List all Jira fields:

```powershell
python app.py --config config.yaml --list-fields
```

Output format:

```text
id,name,custom
summary,Summary,false
customfield_10020,Sprint,true
...
```

Generate a CSV template with suggested field mappings:

```powershell
python app.py --config config.yaml --generate-csv-template
```

Custom output path for template:

```powershell
python app.py --config config.yaml --generate-csv-template output/my_template.yaml
```

The generated template includes recommended values for:
- `output.csv.sprint_field`
- `output.csv.custom_fields.epic_link`
- common CSV columns

## Supported Built-in CSV Columns

- `key`
- `summary`
- `status`
- `assignee`
- `reporter`
- `priority`
- `issue_type`
- `labels`
- `sprint`
- `created`
- `updated`

Any other column name can be mapped through `output.csv.custom_fields`.

## Metadata Output

When `output.metadata.enabled: true`, the script creates a metadata JSON file.

Example:

```json
{
	"generated_at_utc": "2026-05-08T10:41:22.110345+00:00",
	"execution_seconds": 1.237,
	"total_issues": 128,
	"jql": "project = DEMO ORDER BY created DESC",
	"output_format": "csv",
	"output_file": "C:/.../jira_jql_extractor/output/issues.csv",
	"issues_created_min": "2026-01-02T09:10:00.000+00:00",
	"issues_created_max": "2026-05-08T08:55:13.210+00:00",
	"issues_updated_min": "2026-01-02T09:10:00.000+00:00",
	"issues_updated_max": "2026-05-08T10:40:57.401+00:00",
	"pages_queried": 4,
	"avg_issues_per_page": 32.0,
	"requests_sent": 5,
	"retry_attempts": 1,
	"request_failures": 1
}
```

Metadata fields:
- `generated_at_utc`: UTC timestamp when export finished
- `execution_seconds`: total runtime of the export operation
- `total_issues`: number of issues written to the output file
- `jql`: JQL used for the run
- `output_format`: output format (`csv` or `json`)
- `output_file`: absolute path to the generated output file
- `issues_created_min` / `issues_created_max`: min/max `created` values found in exported issues
- `issues_updated_min` / `issues_updated_max`: min/max `updated` values found in exported issues
- `pages_queried`: number of Jira search pages fetched
- `avg_issues_per_page`: average issues per page in this run
- `requests_sent`: total HTTP requests sent to Jira search endpoint
- `retry_attempts`: retries performed after failed attempts
- `request_failures`: failed request attempts (including ones that were later retried)
- `verbose_log_file`: absolute path to the verbose log file (only when enabled)

## Verbose Console Output

When `--verbose` is enabled, the script prints live progress lines such as:

```text
[page] #1 fetched=100 accumulated=100 total=248 startAt=0
[retry] page_start=100 attempt=1/3 wait_s=1.0 error=...
[retry-ok] page_start=100 attempts=2
[page] #2 fetched=100 accumulated=200 total=248 startAt=100
```

This helps during long exports or intermittent network issues.

## Example CSV Section

```yaml
jira:
  auth_type: "pat"
  pat_token_env: "JIRA_PAT_TOKEN"
  # pat_token: "TU_PAT"
  # Para Jira Cloud tambien puedes usar:
  # auth_type: "basic"
  # email: "tu-correo@empresa.com"
  # api_token: "TU_API_TOKEN"
  retry:
    max_attempts: 3
    backoff_seconds: 1.0

output:
  format: "csv"
  file_path: "output/issues.csv"
  verbose_log_file: "output/run.log"
  metadata:
    enabled: true
    file_path: "output/metadata.json"
  csv:
    sprint_field: "customfield_10020"
    columns:
      - key
      - summary
      - status
      - priority
      - issue_type
      - labels
      - sprint
      - epic_link
      - created
      - updated
    custom_fields:
      epic_link: "customfield_10014"
```

## Notes

- Jira Cloud usually works with `/rest/api/3/...` endpoints.
- Jira Server/Data Center may require `/rest/api/2/...` endpoints.
- For Jira Server/Data Center with PAT, this project sends `Authorization: Bearer <pat_token>`.
- If `jira.pat_token_env` is configured, the token is read from your environment at runtime.
- If your Jira uses different custom field ids, use `--list-fields` first.
