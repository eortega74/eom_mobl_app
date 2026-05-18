# Crear PAT con Service Account (Simple)

Esta guia usa solo usuario y password del service account para crear un PAT via API.

## Opcion recomendada (automatica)

Usa el script `create-pat-and-env.ps1`. Este script:
- Pide el password de forma segura
- Crea el PAT en Jira
- Escribe `.env` con `JIRA_PAT_TOKEN=...`

Ejemplo rapido:

```powershell
cd jira_jql_extractor
.\create-pat-and-env.ps1 -BaseUrl "https://jira.tuempresa.com" -ServiceAccountUser "svc_jira"
```

Si quieres reemplazar un `.env` existente:

```powershell
.\create-pat-and-env.ps1 -BaseUrl "https://jira.tuempresa.com" -ServiceAccountUser "svc_jira" -Force
```

Opcionales:
- `-TokenName "jira-exporter-pat"`
- `-ExpirationDays 90`
- `-EnvPath ".env"`

## Requisitos

- URL base de Jira (Server/Data Center), por ejemplo: `https://jira.tuempresa.com`
- Usuario del service account
- Password del service account

## Paso 1: Crear el PAT

Si prefieres manual, usa este ejemplo:

PowerShell:

```powershell
$BASE_URL = "https://jira.tuempresa.com"
$USER = "svc_jira"
$PASS = "TU_PASSWORD"

$pair = "$USER`:$PASS"
$bytes = [System.Text.Encoding]::UTF8.GetBytes($pair)
$basic = [Convert]::ToBase64String($bytes)

$headers = @{
  Authorization = "Basic $basic"
  "Content-Type" = "application/json"
}

$body = '{"name":"jira-exporter-pat","expirationDuration":90}'

$response = Invoke-RestMethod -Method Post -Uri "$BASE_URL/rest/pat/latest/tokens" -Headers $headers -Body $body
$response
```

La respuesta devuelve el token (PAT). Guardalo en un lugar seguro.

## Paso 2: Usar el PAT en este proyecto

1. Crear archivo `.env`:

```powershell
Copy-Item .env.example .env
```

2. Editar `.env` y colocar:

```text
JIRA_PAT_TOKEN=TU_PAT_GENERADO
```

3. Ejecutar el extractor:

```powershell
python app.py --config config.yaml
```

## Prueba rapida del PAT

```powershell
$BASE_URL = "https://jira.tuempresa.com"
$PAT = "TU_PAT_GENERADO"

Invoke-RestMethod -Method Get -Uri "$BASE_URL/rest/api/2/myself" -Headers @{ Authorization = "Bearer $PAT" }
```

Si responde datos del usuario, el PAT funciona.
