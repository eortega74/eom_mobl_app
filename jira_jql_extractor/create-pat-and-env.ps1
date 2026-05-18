param(
    [Parameter(Mandatory = $false)]
    [string]$BaseUrl = "",

    [Parameter(Mandatory = $false)]
    [string]$ServiceAccountUser = "",

    [Parameter(Mandatory = $false)]
    [string]$TokenName = "jira-exporter-pat",

    [Parameter(Mandatory = $false)]
    [int]$ExpirationDays = 90,

    [Parameter(Mandatory = $false)]
    [string]$EnvPath = ".env",

    [Parameter(Mandatory = $false)]
    [switch]$Force
)

$ErrorActionPreference = "Stop"

function Read-RequiredValue {
    param(
        [string]$Prompt
    )

    while ($true) {
        $value = Read-Host $Prompt
        if (-not [string]::IsNullOrWhiteSpace($value)) {
            return $value.Trim()
        }
        Write-Host "Valor requerido. Intenta nuevamente." -ForegroundColor Yellow
    }
}

if ([string]::IsNullOrWhiteSpace($BaseUrl)) {
    $BaseUrl = Read-RequiredValue -Prompt "Jira Base URL (ej: https://jira.tuempresa.com)"
}

if ([string]::IsNullOrWhiteSpace($ServiceAccountUser)) {
    $ServiceAccountUser = Read-RequiredValue -Prompt "Service account user"
}

$BaseUrl = $BaseUrl.TrimEnd("/")

if ($ExpirationDays -lt 1) {
    throw "ExpirationDays debe ser mayor o igual a 1"
}

if ((Test-Path $EnvPath) -and (-not $Force)) {
    throw "El archivo $EnvPath ya existe. Usa -Force para reemplazarlo."
}

$securePassword = Read-Host "Password del service account" -AsSecureString
$credential = [System.Management.Automation.PSCredential]::new($ServiceAccountUser, $securePassword)
$plainPassword = $credential.GetNetworkCredential().Password

$pair = "{0}:{1}" -f $ServiceAccountUser, $plainPassword
$bytes = [System.Text.Encoding]::UTF8.GetBytes($pair)
$basic = [Convert]::ToBase64String($bytes)

$headers = @{
    Authorization = "Basic $basic"
    "Content-Type" = "application/json"
}

$bodyObject = @{
    name = $TokenName
    expirationDuration = $ExpirationDays
}
$body = $bodyObject | ConvertTo-Json -Depth 5

$uri = "$BaseUrl/rest/pat/latest/tokens"
Write-Host "Creando PAT en: $uri" -ForegroundColor Cyan

$response = Invoke-RestMethod -Method Post -Uri $uri -Headers $headers -Body $body

$pat = ""
if ($response -is [string]) {
    $pat = $response.Trim()
} elseif ($null -ne $response) {
    foreach ($candidate in @("token", "rawToken", "pat", "value")) {
        if ($response.PSObject.Properties.Name -contains $candidate) {
            $candidateValue = [string]$response.$candidate
            if (-not [string]::IsNullOrWhiteSpace($candidateValue)) {
                $pat = $candidateValue.Trim()
                break
            }
        }
    }
}

if ([string]::IsNullOrWhiteSpace($pat)) {
    throw "No se pudo extraer el PAT de la respuesta. Revisa la salida del endpoint /rest/pat/latest/tokens."
}

$envContent = "JIRA_PAT_TOKEN=$pat`n"
Set-Content -Path $EnvPath -Value $envContent -Encoding ASCII

$masked = if ($pat.Length -ge 4) { "****" + $pat.Substring($pat.Length - 4) } else { "****" }
Write-Host "PAT generado y guardado en $EnvPath" -ForegroundColor Green
Write-Host "Token (mascarado): $masked" -ForegroundColor Green
Write-Host "Listo. Ahora puedes ejecutar: docker compose up --build" -ForegroundColor Cyan
