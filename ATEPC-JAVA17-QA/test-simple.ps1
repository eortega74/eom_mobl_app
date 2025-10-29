# Test simple de endpoints HTTPS

Write-Host "Probando EOM QA HTTPS endpoints..." -ForegroundColor Green

# Configurar SSL
[System.Net.ServicePointManager]::ServerCertificateValidationCallback = {$true}

# Test pagina principal
Write-Host "Probando pagina principal..." -ForegroundColor Yellow
try {
    $home = Invoke-WebRequest -Uri "https://localhost:8080/" -UseBasicParsing -TimeoutSec 5
    Write-Host "OK - Pagina principal: $($home.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "ERROR - Pagina principal: $($_.Exception.Message)" -ForegroundColor Red
}

# Test health
Write-Host "Probando health endpoint..." -ForegroundColor Yellow  
try {
    $health = Invoke-WebRequest -Uri "https://localhost:8080/health" -UseBasicParsing -TimeoutSec 5
    Write-Host "OK - Health: $($health.StatusCode)" -ForegroundColor Green
    Write-Host "Contenido: $($health.Content)" -ForegroundColor Gray
} catch {
    Write-Host "ERROR - Health: $($_.Exception.Message)" -ForegroundColor Red
}

# Test API hello
Write-Host "Probando API hello..." -ForegroundColor Yellow
try {
    $hello = Invoke-WebRequest -Uri "https://localhost:8080/api/hello" -UseBasicParsing -TimeoutSec 5  
    Write-Host "OK - API Hello: $($hello.StatusCode)" -ForegroundColor Green
    Write-Host "Contenido: $($hello.Content)" -ForegroundColor Gray
} catch {
    Write-Host "ERROR - API Hello: $($_.Exception.Message)" -ForegroundColor Red  
}

# Test JSON API
Write-Host "Probando JSON API..." -ForegroundColor Yellow
try {
    $json = Invoke-WebRequest -Uri "https://localhost:8080/json" -UseBasicParsing -TimeoutSec 5
    Write-Host "OK - JSON API: $($json.StatusCode)" -ForegroundColor Green  
    Write-Host "Contenido: $($json.Content)" -ForegroundColor Gray
} catch {
    Write-Host "ERROR - JSON API: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "HTTPS Tests completados" -ForegroundColor Green
Write-Host "URL: https://localhost:8080" -ForegroundColor Cyan