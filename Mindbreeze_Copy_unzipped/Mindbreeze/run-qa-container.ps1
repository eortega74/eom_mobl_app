param(
  [string]$BaseUrl = "http://localhost:3000"
)

$ErrorActionPreference = "Stop"

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
  throw "Docker no esta disponible. Instala Docker Desktop o ejecuta en GitHub Actions."
}

$imageName = "mindbreeze-qa-runner:local"

Write-Host "[Mindbreeze QA] Building image: $imageName"
docker build -t $imageName .

Write-Host "[Mindbreeze QA] Running tests against: $BaseUrl"
docker run --rm -e MINDBREEZE_BASE_URL=$BaseUrl $imageName
