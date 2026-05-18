param(
    [Parameter(Mandatory = $false)]
    [string]$EnvPath = ".env",

    [Parameter(Mandatory = $false)]
    [switch]$Force
)

$ErrorActionPreference = "Stop"

if ((Test-Path $EnvPath) -and (-not $Force)) {
    throw "El archivo $EnvPath ya existe. Usa -Force para reemplazarlo."
}

$securePat = Read-Host "Pega el PAT (input oculto)" -AsSecureString
$credential = [System.Management.Automation.PSCredential]::new("pat", $securePat)
$pat = $credential.GetNetworkCredential().Password

if ([string]::IsNullOrWhiteSpace($pat)) {
    throw "No se recibio un PAT valido."
}

$envContent = "JIRA_PAT_TOKEN=$pat`n"
Set-Content -Path $EnvPath -Value $envContent -Encoding ASCII

$masked = if ($pat.Length -ge 4) { "****" + $pat.Substring($pat.Length - 4) } else { "****" }
Write-Host "Archivo $EnvPath actualizado." -ForegroundColor Green
Write-Host "Token (mascarado): $masked" -ForegroundColor Green
Write-Host "Listo. Ahora puedes ejecutar: docker compose up --build" -ForegroundColor Cyan
