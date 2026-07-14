# Mindbreeze QA Runner

ES: Runner ejecutable de pruebas UI con Playwright para Mindbreeze.
EN: Executable UI test runner for Mindbreeze using Playwright.

---

## Espanol

### Que es esto

Este modulo contiene un runner de QA automatizado para interfaz web con Playwright.
Incluye:
- Pruebas en TypeScript
- Ejecucion local
- Ejecucion en contenedor Docker
- Integracion lista para GitHub Actions

### Integracion en GitHub Actions

1. Verifica que exista el workflow en:
	- `.github/workflows/mindbreeze-qa.yml`
2. Sube al repositorio esta carpeta del runner:
	- `Mindbreeze_Copy_unzipped/Mindbreeze`
3. (Recomendado) Crea el secret del repo:
	- `MINDBREEZE_BASE_URL`
4. Haz push al branch principal o ejecuta manualmente el workflow desde Actions.

El workflow usa la imagen:
- `mcr.microsoft.com/playwright:v1.55.0-jammy`

Eso evita depender de Node, npm o navegadores preinstalados en el runner host.

### Como corre el workflow

El job hace lo siguiente:
1. Checkout del repositorio
2. `npm ci` (o fallback `npm install`)
3. `npm test`
4. Publica el reporte de Playwright como artifact

### Variables y secretos

- `MINDBREEZE_BASE_URL` (secret recomendado)

Si no se define, el runner usa por defecto:
- `http://localhost:3000`

### Ejecucion local con Docker (sin depender del host)

Requisito:
- Docker

Comando:

```powershell
./run-qa-container.ps1 -BaseUrl "https://tu-app"
```

### Ejecucion local con Node

Requisitos:
- Node.js 20+
- npm

Comando:

```powershell
./run-qa.ps1 -BaseUrl "https://tu-app"
```

---

## English

### What this is

This module provides an automated UI QA runner for Mindbreeze using Playwright.
It includes:
- TypeScript tests
- Local execution
- Docker-based execution
- Ready-to-use GitHub Actions integration

### GitHub Actions integration

1. Ensure the workflow exists at:
	- `.github/workflows/mindbreeze-qa.yml`
2. Commit and push the runner folder:
	- `Mindbreeze_Copy_unzipped/Mindbreeze`
3. (Recommended) Create this repository secret:
	- `MINDBREEZE_BASE_URL`
4. Trigger the workflow via push or manually from the Actions tab.

The workflow runs inside:
- `mcr.microsoft.com/playwright:v1.55.0-jammy`

This removes dependency on preinstalled Node, npm, or browsers on the host runner.

### Workflow execution flow

The job performs:
1. Repository checkout
2. `npm ci` (or fallback `npm install`)
3. `npm test`
4. Uploads Playwright HTML report as an artifact

### Variables and secrets

- `MINDBREEZE_BASE_URL` (recommended secret)

If not set, the runner defaults to:
- `http://localhost:3000`

### Local execution with Docker (host-independent)

Requirement:
- Docker

Command:

```powershell
./run-qa-container.ps1 -BaseUrl "https://your-app"
```

### Local execution with Node

Requirements:
- Node.js 20+
- npm

Command:

```powershell
./run-qa.ps1 -BaseUrl "https://your-app"
```
