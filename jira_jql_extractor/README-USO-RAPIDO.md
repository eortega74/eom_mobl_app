# Uso rapido: Jira JQL Extractor con PAT automatico

Este documento explica el flujo mas simple para usar el proyecto cuando ya tienes:
- Usuario de service account
- Password de service account
- URL base de Jira

## 1) Ir a la carpeta del proyecto

```powershell
cd c:\Users\eomar\OneDrive\eom_dock_img\repos\BPPR\eom_mobl_app\jira_jql_extractor
```

## 2) Generar PAT y archivo .env automaticamente

Ejecuta:

```powershell
.\create-pat-and-env.ps1 -BaseUrl "<JIRA_URL>" -ServiceAccountUser "<SERVICE_ACCOUNT_USER>"
```

El script:
- Te pide el password de forma segura
- Crea el PAT en Jira
- Crea/actualiza `.env` con `JIRA_PAT_TOKEN=...`

Si ya existe `.env` y deseas reemplazarlo:

```powershell
.\create-pat-and-env.ps1 -BaseUrl "<JIRA_URL>" -ServiceAccountUser "<SERVICE_ACCOUNT_USER>" -Force
```

Alternativa de usuario (UPN), si tu dominio lo requiere:

```powershell
.\create-pat-and-env.ps1 -BaseUrl "<JIRA_URL>" -ServiceAccountUser "<SERVICE_ACCOUNT_USER_UPN>" -Force
```

## 2.1) Si solo deseas usar un PAT ya creado

Sin generar PAT por API, carga tu token existente en `.env`:

```powershell
.\set-env-from-existing-pat.ps1 -Force
```

Luego ejecuta el extractor:

```powershell
docker compose up --build
```

## 3) Validar configuracion

Asegurate de que `config.yaml` tenga:

```yaml
jira:
  auth_type: "pat"
  pat_token_env: "JIRA_PAT_TOKEN"
```

## 4) Ejecutar extractor

### Opcion A: Docker Compose (recomendada)

```powershell
docker compose up --build
```

### Opcion B: Python local

```powershell
pip install -r requirements.txt
python app.py --config config.yaml
```

## 5) Salidas

Por defecto se generan archivos en la carpeta `output/` (segun `config.yaml`):
- issues exportados (CSV o JSON)
- metadata (si esta habilitado)

## Problemas comunes

- Error de autenticacion 401/403:
  - Verifica user/password del service account
  - Verifica que el usuario tenga permisos en Jira
  - Regenera PAT con `-Force`
  - Si el error muestra `WWW-Authenticate: OAuth realm=...`, tu instancia probablemente bloquea auth por password para API.
  - En ese caso usa PAT manual + script de carga en `.env`:

```powershell
.\set-env-from-existing-pat.ps1 -Force
```

  - Luego ejecuta:

```powershell
docker compose up --build
```

- Error indicando PAT no encontrado:
  - Verifica que exista `.env`
  - Verifica que tenga `JIRA_PAT_TOKEN=...`
  - Verifica `pat_token_env: "JIRA_PAT_TOKEN"` en `config.yaml`

## Comando completo de referencia

```powershell
.\create-pat-and-env.ps1 -BaseUrl "<JIRA_URL>" -ServiceAccountUser "<SERVICE_ACCOUNT_USER>" -TokenName "jira-exporter-pat" -ExpirationDays 90 -EnvPath ".env" -Force
```
