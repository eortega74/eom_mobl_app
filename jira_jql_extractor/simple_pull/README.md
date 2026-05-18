# Simple Pull Jira

Esta carpeta contiene un ejemplo minimo para extraer issues desde Jira usando PAT.

## Archivos

- `jira_pull_simple.py`: script .py que consulta Jira Search API
- `requirements.txt`: dependencia minima (`requests`)
- `run-simple-pull.ps1`: instala pip deps y ejecuta en un paso

## Opcion 1: usar script PowerShell (recomendada)

```powershell
cd ..\simple_pull
.\run-simple-pull.ps1 -BaseUrl "https://jira.tuempresa.com" -Jql "project = ABC ORDER BY created DESC"
```

El script pedira el PAT de forma oculta y generara `jira_issues.json`.

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

- Si tu Jira usa API v3, puedes cambiar el endpoint en `jira_pull_simple.py` de `/rest/api/2/search` a `/rest/api/3/search`.
- Si recibes 401/403, revisa permisos del PAT y politica SSO de la instancia.

## Troubleshooting rapido

Si ves `Max retries exceeded`, prueba primero con API v3:

```powershell
.\run-simple-pull.ps1 -BaseUrl "https://jira.tuempresa.com" -ApiPath "/rest/api/3/search" -Jql "project = ABC ORDER BY created DESC"
```

Si estas en red corporativa con SSL inspeccionado, prueba temporalmente sin validacion SSL:

```powershell
.\run-simple-pull.ps1 -BaseUrl "https://jira.tuempresa.com" -ApiPath "/rest/api/3/search" -SkipSslVerify -Jql "project = ABC ORDER BY created DESC"
```

Nota: `-SkipSslVerify` es solo para diagnostico/local; no recomendado como configuracion permanente.
