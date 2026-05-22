# Simple Pull Jira

Esta carpeta contiene un ejemplo minimo para extraer issues desde Jira usando PAT.

## Archivos

- `jira_pull_simple.py`: script .py que consulta Jira Search API
- `requirements.txt`: dependencia minima (`requests`)
- `run-simple-pull.ps1`: instala pip deps y ejecuta en un paso
- `check-project-and-pull.ps1`: valida acceso al proyecto y exporta a `json` o `csv`

## Opcion 1: usar script PowerShell (recomendada)

```powershell
cd ..\simple_pull
.\run-simple-pull.ps1 -BaseUrl "https://jira.tuempresa.com" -Jql "project = ABC ORDER BY created DESC"
```

El script pedira el PAT de forma oculta y generara `jira_issues.json`.

## Opcion 1.1: validar proyecto + exportar JSON/CSV

Exportar en JSON (default):

```powershell
.\check-project-and-pull.ps1 -BaseUrl "https://jira.tuempresa.com" -Project "NAFM" -SkipSslVerify
```

Exportar en CSV:

```powershell
.\check-project-and-pull.ps1 -BaseUrl "https://jira.tuempresa.com" -Project "NAFM" -OutputFormat csv -OutputFile "nafm_issues.csv" -SkipSslVerify
```

Ver ayuda del script:

```powershell
.\check-project-and-pull.ps1 -Help
```

## Opcion 2: correr manual

```powershell
cd ..\simple_pull
py -m pip install -r requirements.txt
$env:JIRA_BASE_URL = "https://jira.tuempresa.com"
$env:JIRA_PAT_TOKEN = "TU_PAT"
$env:JIRA_JQL = "project = ABC ORDER BY created DESC"
py .\jira_pull_simple.py
```

## Notas

- Para Jira Server/Data Center, usa `/rest/api/latest/search` (default) o `/rest/api/2/search`.
- Si tu entorno expone API v3, puedes usar `/rest/api/3/search`.
- Si recibes 401/403, revisa permisos del PAT y politica SSO de la instancia.

## Troubleshooting rapido

Si ves `Max retries exceeded`, prueba primero con latest:

```powershell
.\run-simple-pull.ps1 -BaseUrl "https://jira.tuempresa.com" -ApiPath "/rest/api/latest/search" -Jql "project = ABC ORDER BY created DESC"
```

Si no funciona, prueba API v2 o v3 segun tu instancia:

```powershell
.\run-simple-pull.ps1 -BaseUrl "https://jira.tuempresa.com" -ApiPath "/rest/api/2/search" -Jql "project = ABC ORDER BY created DESC"
```

Si estas en red corporativa con SSL inspeccionado, prueba temporalmente sin validacion SSL:

```powershell
.\run-simple-pull.ps1 -BaseUrl "https://jira.tuempresa.com" -ApiPath "/rest/api/3/search" -SkipSslVerify -Jql "project = ABC ORDER BY created DESC"
```

Nota: `-SkipSslVerify` es solo para diagnostico/local; no recomendado como configuracion permanente.
