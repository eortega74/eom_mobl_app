param(
    [Parameter(Mandatory = $false)]
    [string]$BaseUrl = "",

    [Parameter(Mandatory = $false)]
    [string]$Project = "",

    [Parameter(Mandatory = $false)]
    [string]$Jql = "",

    [Parameter(Mandatory = $false)]
    [string]$ApiVersion = "latest",

    [Parameter(Mandatory = $false)]
    [int]$MaxResults = 50,

    [Parameter(Mandatory = $false)]
    [int]$TimeoutSeconds = 60,

    [Parameter(Mandatory = $false)]
    [string]$OutputFile = "jira_issues.json",

    [Parameter(Mandatory = $false)]
    [switch]$SkipSslVerify
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($BaseUrl)) {
    $BaseUrl = Read-Host "Jira Base URL (ej: https://jira.tuempresa.com)"
}
if ([string]::IsNullOrWhiteSpace($Project)) {
    $Project = Read-Host "Project key o name (ej: NTD)"
}

$BaseUrl = $BaseUrl.TrimEnd("/")
$securePat = Read-Host "Pega tu PAT (input oculto)" -AsSecureString
$credential = [System.Management.Automation.PSCredential]::new("pat", $securePat)
$pat = $credential.GetNetworkCredential().Password

if ([string]::IsNullOrWhiteSpace($pat)) {
    throw "No se recibio un PAT valido."
}

$headers = @{
    Authorization = "Bearer $pat"
    Accept = "application/json"
}

$invokeParams = @{
    Headers = $headers
    TimeoutSec = $TimeoutSeconds
    ErrorAction = "Stop"
}

$cmd = Get-Command Invoke-WebRequest
if ($cmd.Parameters.ContainsKey("UseBasicParsing")) {
    $invokeParams.UseBasicParsing = $true
}

$supportsSkipCertificateCheck = $cmd.Parameters.ContainsKey("SkipCertificateCheck")
$oldCertPolicy = $null
$oldCertCallback = [System.Net.ServicePointManager]::ServerCertificateValidationCallback

if ($null -ne $oldCertCallback) {
    [System.Net.ServicePointManager]::ServerCertificateValidationCallback = $null
}

if ($SkipSslVerify) {
    if ($supportsSkipCertificateCheck) {
        $invokeParams.SkipCertificateCheck = $true
    }
    else {
        $oldCertPolicy = [System.Net.ServicePointManager]::CertificatePolicy
        if (-not ("TrustAllCertsPolicy" -as [type])) {
            Add-Type @"
using System.Net;
using System.Security.Cryptography.X509Certificates;

public class TrustAllCertsPolicy : ICertificatePolicy
{
    public bool CheckValidationResult(ServicePoint srvPoint, X509Certificate certificate, WebRequest request, int certificateProblem)
    {
        return true;
    }
}
"@
        }
        [System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy
        try {
            [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12
        }
        catch {
        }
    }
}

function Invoke-JiraGet {
    param(
        [string]$Uri
    )

    $localParams = @{}
    foreach ($k in $invokeParams.Keys) {
        $localParams[$k] = $invokeParams[$k]
    }
    $localParams["Uri"] = $Uri

    $resp = Invoke-WebRequest @localParams
    return ($resp.Content | ConvertFrom-Json)
}

try {
    $projectsUri = "$BaseUrl/rest/api/$ApiVersion/project"
    Write-Host "Consultando proyectos visibles..." -ForegroundColor Cyan
    $projects = Invoke-JiraGet -Uri $projectsUri

    if (-not $projects) {
        throw "La API no devolvio proyectos."
    }

    $projectObj = $projects | Where-Object {
        ($_.key -ieq $Project) -or ($_.name -ieq $Project)
    } | Select-Object -First 1

    if (-not $projectObj) {
        Write-Host "No tienes acceso al proyecto '$Project' o el nombre/key no existe." -ForegroundColor Yellow
        Write-Host "Proyectos visibles (primeros 20):" -ForegroundColor Yellow
        $projects | Select-Object -First 20 | ForEach-Object {
            Write-Host ("- {0} ({1})" -f $_.key, $_.name)
        }
        exit 2
    }

    Write-Host ("Proyecto accesible: {0} ({1})" -f $projectObj.key, $projectObj.name) -ForegroundColor Green

    if ([string]::IsNullOrWhiteSpace($Jql)) {
        $Jql = "project = $($projectObj.key) ORDER BY created DESC"
    }

    $fields = "key,summary,status,assignee,created,updated"
    $q = [uri]::EscapeDataString($Jql)
    $searchUri = "$BaseUrl/rest/api/$ApiVersion/search?jql=$q&maxResults=$MaxResults&fields=$fields"

    Write-Host "Ejecutando JQL..." -ForegroundColor Cyan
    $searchPayload = Invoke-JiraGet -Uri $searchUri

    $issues = @($searchPayload.issues)
    Write-Host ("Issues obtenidos: {0}" -f $issues.Count) -ForegroundColor Green

    $searchPayload | ConvertTo-Json -Depth 100 | Set-Content -Path $OutputFile -Encoding UTF8
    Write-Host ("Archivo generado: {0}" -f (Resolve-Path $OutputFile)) -ForegroundColor Green

    $issues | Select-Object -First 10 | ForEach-Object {
        Write-Host ("{0}: {1}" -f $_.key, $_.fields.summary)
    }
}
catch {
    Write-Host ("Error: {0}" -f $_.Exception.Message) -ForegroundColor Red
    if ($_.Exception.Response) {
        try {
            $stream = $_.Exception.Response.GetResponseStream()
            if ($stream) {
                $reader = New-Object System.IO.StreamReader($stream)
                $body = $reader.ReadToEnd()
                $reader.Close()
                if ($body) {
                    $preview = if ($body.Length -gt 500) { $body.Substring(0, 500) } else { $body }
                    Write-Host "Body preview:" -ForegroundColor Yellow
                    Write-Host $preview
                }
            }
        }
        catch {
        }
    }
    exit 1
}
finally {
    if ($null -ne $oldCertCallback) {
        [System.Net.ServicePointManager]::ServerCertificateValidationCallback = $oldCertCallback
    }
    if ($SkipSslVerify -and (-not $supportsSkipCertificateCheck) -and ($null -ne $oldCertPolicy)) {
        [System.Net.ServicePointManager]::CertificatePolicy = $oldCertPolicy
    }
}
