# Test simple de conectividad HTTPS

Write-Host "Probando conectividad HTTPS..." -ForegroundColor Green

# Configurar para aceptar certificados self-signed
[System.Net.ServicePointManager]::ServerCertificateValidationCallback = {$true}

try {
    Write-Host "Intentando conectar a https://localhost:8080/health..." -ForegroundColor Yellow
    $response = Invoke-WebRequest -Uri "https://localhost:8080/health" -UseBasicParsing -TimeoutSec 15
    
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Content: $($response.Content)" -ForegroundColor Cyan
    
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Detalles: $($_.Exception)" -ForegroundColor Yellow
}

# Probar tambien el endpoint principal
try {
    Write-Host "Intentando conectar a https://localhost:8080/..." -ForegroundColor Yellow
    $response2 = Invoke-WebRequest -Uri "https://localhost:8080/" -UseBasicParsing -TimeoutSec 15
    
    Write-Host "Status Code: $($response2.StatusCode)" -ForegroundColor Green
    Write-Host "Content Length: $($response2.Content.Length)" -ForegroundColor Cyan
    
} catch {
    Write-Host "Error en pagina principal: $($_.Exception.Message)" -ForegroundColor Red
}