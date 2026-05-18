param(
    [Parameter(Mandatory = $false)]
    [string]$BaseUrl = "",

    [Parameter(Mandatory = $false)]
    [string]$ApiPath = "/rest/api/latest/myself",

    [Parameter(Mandatory = $false)]
    [int]$TimeoutSeconds = 30,

    [Parameter(Mandatory = $false)]
    [switch]$SkipSslVerify
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($BaseUrl)) {
    $BaseUrl = Read-Host "Jira Base URL (ej: https://jira.tuempresa.com)"
}

$BaseUrl = $BaseUrl.TrimEnd("/")
if (-not $ApiPath.StartsWith("/")) {
    $ApiPath = "/$ApiPath"
}

$securePat = Read-Host "Pega tu PAT (input oculto)" -AsSecureString
$credential = [System.Management.Automation.PSCredential]::new("pat", $securePat)
$pat = $credential.GetNetworkCredential().Password

if ([string]::IsNullOrWhiteSpace($pat)) {
    throw "No se recibio un PAT valido."
}

$uri = "$BaseUrl$ApiPath"
$headers = @{
    Authorization = "Bearer $pat"
    Accept = "application/json"
}

Write-Host "Testing PAT against: $uri" -ForegroundColor Cyan

$invokeParams = @{
    Uri = $uri
    Headers = $headers
    MaximumRedirection = 0
    TimeoutSec = $TimeoutSeconds
    ErrorAction = "Stop"
}

$supportsSkipCertificateCheck = $false
if (Get-Command Invoke-WebRequest -ErrorAction SilentlyContinue) {
    $cmd = Get-Command Invoke-WebRequest
    $supportsSkipCertificateCheck = $cmd.Parameters.ContainsKey("SkipCertificateCheck")
}

$oldCertPolicy = $null
if ($SkipSslVerify) {
    if ($supportsSkipCertificateCheck) {
        $invokeParams.SkipCertificateCheck = $true
    }
    else {
        # Windows PowerShell 5.1 fallback without ScriptBlock callback
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

function Show-Result {
    param(
        [int]$StatusCode,
        [string]$ContentType,
        [string]$Location,
        [string]$Body
    )

    $preview = ""
    if ($null -ne $Body) {
        $preview = $Body
        if ($preview.Length -gt 500) {
            $preview = $preview.Substring(0, 500)
        }
    }

    Write-Host "Status: $StatusCode"
    Write-Host "Content-Type: $ContentType"
    Write-Host "Location: $Location"
    Write-Host "Body preview:"
    Write-Host $preview

    if ($StatusCode -eq 200 -and $ContentType -match "application/json") {
        Write-Host "PAT accepted (JSON API response)." -ForegroundColor Green
        return
    }

    if ($StatusCode -in @(301, 302, 303, 307, 308)) {
        Write-Host "Possible SSO/login redirect detected." -ForegroundColor Yellow
        return
    }

    if ($StatusCode -in @(401, 403)) {
        Write-Host "PAT rejected or blocked by policy/permissions." -ForegroundColor Yellow
        return
    }

    Write-Host "Unexpected response. Review body preview and headers." -ForegroundColor Yellow
}

try {
    try {
        $response = Invoke-WebRequest @invokeParams
        $statusCode = [int]$response.StatusCode
        $contentType = [string]$response.Headers["Content-Type"]
        $location = [string]$response.Headers["Location"]
        $body = [string]$response.Content

        Show-Result -StatusCode $statusCode -ContentType $contentType -Location $location -Body $body
    }
    catch {
        $exception = $_.Exception
        if ($exception.Response) {
            $response = $exception.Response
            $statusCode = [int]$response.StatusCode
            $contentType = [string]$response.Headers["Content-Type"]
            $location = [string]$response.Headers["Location"]

            $body = ""
            try {
                if ($response.GetResponseStream) {
                    $stream = $response.GetResponseStream()
                    if ($stream) {
                        $reader = New-Object System.IO.StreamReader($stream)
                        $body = $reader.ReadToEnd()
                        $reader.Close()
                    }
                }
            }
            catch {
                $body = ""
            }

            Show-Result -StatusCode $statusCode -ContentType $contentType -Location $location -Body $body
            exit 1
        }

        Write-Host "Request failed: $exception" -ForegroundColor Red
        exit 1
    }
}
finally {
    if ($SkipSslVerify -and (-not $supportsSkipCertificateCheck) -and ($null -ne $oldCertPolicy)) {
        [System.Net.ServicePointManager]::CertificatePolicy = $oldCertPolicy
    }
}
