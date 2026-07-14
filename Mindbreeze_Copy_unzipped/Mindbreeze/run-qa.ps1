param(
  [string]$BaseUrl = "http://localhost:3000"
)

$ErrorActionPreference = "Stop"
$env:MINDBREEZE_BASE_URL = $BaseUrl

Write-Host "[Mindbreeze QA] Base URL: $BaseUrl"

if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
  throw "npm no esta disponible. Instala Node.js 20+ para ejecutar este runner localmente."
}

npm ci
npx playwright install chromium
npm test
