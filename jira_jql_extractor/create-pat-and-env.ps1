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

function Get-HttpErrorDetails {
    param(
        [Parameter(Mandatory = $true)]
        $Exception
    )

    $statusCode = "unknown"
    $responseBody = ""

    if ($Exception.Response -and $Exception.Response.StatusCode) {
        $statusCode = [int]$Exception.Response.StatusCode
    }

    try {
        if ($Exception.Response -and $Exception.Response.GetResponseStream) {
            $stream = $Exception.Response.GetResponseStream()
            if ($stream) {
                $reader = New-Object System.IO.StreamReader($stream)
                $responseBody = $reader.ReadToEnd()
                $reader.Close()
            }
        }
    } catch {
    }

    return [PSCustomObject]@{
        StatusCode = $statusCode
        Body = $responseBody
    }
}

function New-PatWithBasicAuth {
    param(
        [string]$Uri,
        [hashtable]$Headers,
        [string]$Body
    )

    return Invoke-RestMethod -Method Post -Uri $Uri -Headers $Headers -Body $Body
}

function New-PatWithSessionAuth {
    param(
        [string]$BaseUrl,
        [string]$Uri,
        [string]$ServiceAccountUser,
        [string]$PlainPassword,
        [string]$Body
    )

    $loginEndpoints = @(
        "$BaseUrl/rest/auth/1/session",
        "$BaseUrl/rest/auth/latest/session"
    )

    $authPayload = @{ username = $ServiceAccountUser; password = $PlainPassword } | ConvertTo-Json
    $lastAuthError = $null

    foreach ($loginUri in $loginEndpoints) {
        try {
            Write-Host "Intentando login de sesion en: $loginUri" -ForegroundColor Yellow
            $null = Invoke-RestMethod `
                -Method Post `
                -Uri $loginUri `
                -ContentType "application/json" `
                -Body $authPayload `
                -SessionVariable jiraSession

            $sessionHeaders = @{
                "Content-Type" = "application/json"
                "Accept" = "application/json"
                "X-Atlassian-Token" = "no-check"
            }

            return Invoke-RestMethod -Method Post -Uri $Uri -Headers $sessionHeaders -Body $Body -WebSession $jiraSession
        } catch {
            $lastAuthError = $_
        }
    }

    if ($null -ne $lastAuthError) {
        throw $lastAuthError
    }

    throw "No fue posible autenticarse por sesion para crear el PAT."
}

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
    "Accept" = "application/json"
}

$bodyObject = @{
    name = $TokenName
    expirationDuration = $ExpirationDays
}
$body = $bodyObject | ConvertTo-Json -Depth 5

$uri = "$BaseUrl/rest/pat/latest/tokens"
Write-Host "Creando PAT en: $uri" -ForegroundColor Cyan

$response = $null
try {
    $response = New-PatWithBasicAuth -Uri $uri -Headers $headers -Body $body
} catch {
    $details = Get-HttpErrorDetails -Exception $_.Exception
    $statusCode = $details.StatusCode

    if ($statusCode -in @(401, 403)) {
        Write-Host "Basic auth devolvio $statusCode. Intentando fallback por sesion..." -ForegroundColor Yellow
        try {
            $response = New-PatWithSessionAuth `
                -BaseUrl $BaseUrl `
                -Uri $uri `
                -ServiceAccountUser $ServiceAccountUser `
                -PlainPassword $plainPassword `
                -Body $body
        } catch {
            $sessionDetails = Get-HttpErrorDetails -Exception $_.Exception
            $msg = @(
                "No se pudo crear el PAT (fallback de sesion tambien fallo).",
                "Status basic: $statusCode",
                "Status session: $($sessionDetails.StatusCode)",
                "Sugerencias:",
                "- Verifica formato de usuario: DOMAIN\\usuario o usuario@dominio.",
                "- Confirma que el usuario puede crear PAT en Jira.",
                "- Confirma que el endpoint PAT este habilitado en la instancia.",
                "- Revisa si SSO/politicas bloquean autenticacion por password.",
                "Body session: $($sessionDetails.Body)"
            ) -join [Environment]::NewLine
            throw $msg
        }
    } else {
        $msg = @(
            "No se pudo crear el PAT.",
            "Status: $statusCode",
            "Body: $($details.Body)"
        ) -join [Environment]::NewLine
        throw $msg
    }
}

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
