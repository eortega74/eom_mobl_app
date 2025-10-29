# Test simple para HTTPS con EOM QA Hello World

Write-Host "=== TEST HTTPS EOM QA HELLO WORLD ===" -ForegroundColor Green
Write-Host ""

# URL de la aplicacion
$baseUrl = "https://localhost:8080"

Write-Host "Probando aplicacion en: $baseUrl" -ForegroundColor Cyan
Write-Host ""

# Test 1: Verificar que la aplicacion responde
Write-Host "1. Test de conexion HTTPS..." -ForegroundColor Yellow

try {
    # Ignorar errores de certificado self-signed
    [System.Net.ServicePointManager]::ServerCertificateValidationCallback = {$true}
    
    $response = Invoke-WebRequest -Uri $baseUrl -UseBasicParsing -TimeoutSec 10
    
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✓ Aplicacion responde correctamente" -ForegroundColor Green
        Write-Host "   Status Code: $($response.StatusCode)" -ForegroundColor White
    } else {
        Write-Host "   ✗ Error: Status Code $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "   ✗ Error conectando: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 2: Verificar endpoint de salud
Write-Host "2. Test endpoint /health..." -ForegroundColor Yellow

try {
    $healthResponse = Invoke-WebRequest -Uri "$baseUrl/health" -UseBasicParsing -TimeoutSec 10
    
    if ($healthResponse.StatusCode -eq 200) {
        Write-Host "   ✓ Endpoint /health OK" -ForegroundColor Green
        Write-Host "   Response: $($healthResponse.Content)" -ForegroundColor White
    } else {
        Write-Host "   ✗ Error en /health: Status Code $($healthResponse.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "   ✗ Error en /health: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 3: Verificar endpoint JSON API
Write-Host "3. Test endpoint /api/hello..." -ForegroundColor Yellow

try {
    $apiResponse = Invoke-WebRequest -Uri "$baseUrl/api/hello" -UseBasicParsing -TimeoutSec 10
    
    if ($apiResponse.StatusCode -eq 200) {
        Write-Host "   ✓ Endpoint /api/hello OK" -ForegroundColor Green
        Write-Host "   Response: $($apiResponse.Content)" -ForegroundColor White
    } else {
        Write-Host "   ✗ Error en /api/hello: Status Code $($apiResponse.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "   ✗ Error en /api/hello: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== RESUMEN ===" -ForegroundColor Magenta
Write-Host "Aplicacion HTTPS: $baseUrl" -ForegroundColor White
Write-Host "Certificado: Self-signed (desarrollo)" -ForegroundColor White
Write-Host "Estado: Verificar resultados anteriores" -ForegroundColor White
Write-Host ""
Write-Host "Para pruebas Selenium, usar ChromeOptions para ignorar certificados:" -ForegroundColor Cyan
Write-Host "  --ignore-certificate-errors" -ForegroundColor White
Write-Host "  --ignore-ssl-errors" -ForegroundColor White
Write-Host "  --allow-running-insecure-content" -ForegroundColor White
Write-Host ""
Write-Host "Test HTTPS completado!" -ForegroundColor Green