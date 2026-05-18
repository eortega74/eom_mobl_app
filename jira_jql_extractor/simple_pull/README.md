# Simple Pull Jira

Esta carpeta contiene un ejemplo minimo para extraer issues desde Jira usando PAT.

## Archivos

- `jira_pull_simple.py`: script Python que consulta Jira Search API
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
pip install -r requirements.txt
$env:JIRA_BASE_URL = "https://jira.tuempresa.com"
$env:JIRA_PAT_TOKEN = "TU_PAT"
$env:JIRA_JQL = "project = ABC ORDER BY created DESC"
python .\jira_pull_simple.py
```

## Notas

- Si tu Jira usa API v3, puedes cambiar el endpoint en `jira_pull_simple.py` de `/rest/api/2/search` a `/rest/api/3/search`.
- Si recibes 401/403, revisa permisos del PAT y politica SSO de la instancia.
