param(
    [Parameter(Mandatory = $false)]
    [string]$BaseUrl = "",

    [Parameter(Mandatory = $false)]
    [string]$Jql = "project = ABC ORDER BY created DESC",

    [Parameter(Mandatory = $false)]
    [string]$OutputFile = "jira_issues.json",

    [Parameter(Mandatory = $false)]
    [string]$ApiPath = "/rest/api/2/search",

    [Parameter(Mandatory = $false)]
    [int]$TimeoutSeconds = 30,

    [Parameter(Mandatory = $false)]
    [switch]$SkipSslVerify
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($BaseUrl)) {
    $BaseUrl = Read-Host "Jira Base URL (ej: https://jira.tuempresa.com)"
}

$securePat = Read-Host "Pega tu PAT (input oculto)" -AsSecureString
$credential = [System.Management.Automation.PSCredential]::new("pat", $securePat)
$pat = $credential.GetNetworkCredential().Password

if ([string]::IsNullOrWhiteSpace($pat)) {
    throw "No se recibio un PAT valido."
}

$env:JIRA_BASE_URL = $BaseUrl.TrimEnd("/")
$env:JIRA_PAT_TOKEN = $pat
$env:JIRA_JQL = $Jql
$env:JIRA_OUTPUT_FILE = $OutputFile
$env:JIRA_API_PATH = $ApiPath
$env:JIRA_TIMEOUT_SECONDS = [string]$TimeoutSeconds
$env:JIRA_VERIFY_SSL = if ($SkipSslVerify) { "false" } else { "true" }

Write-Host "Installing dependencies..." -ForegroundColor Cyan
py -m pip install -r requirements.txt

Write-Host "Running Jira simple pull..." -ForegroundColor Cyan
py .\jira_pull_simple.py
