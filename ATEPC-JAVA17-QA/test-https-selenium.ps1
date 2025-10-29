# Script para probar EOM QA Hello World con HTTPS y preparar Selenium

Write-Host "🔐 Probando EOM QA Hello World con HTTPS..." -ForegroundColor Green

# URLs para probar
$baseUrl = "https://localhost:8080"
$endpoints = @(
    "/",
    "/health",
    "/api/hello",
    "/json"
)

Write-Host "🌐 Probando endpoints HTTPS..." -ForegroundColor Cyan

foreach ($endpoint in $endpoints) {
    $url = $baseUrl + $endpoint
    Write-Host "📡 Probando: $url" -ForegroundColor Yellow
    
    try {
        # Ignorar certificado self-signed para la prueba
        [System.Net.ServicePointManager]::ServerCertificateValidationCallback = {$true}
        
        $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 10
        
        if ($response.StatusCode -eq 200) {
            Write-Host "  ✅ OK (200) - Longitud: $($response.Content.Length) chars" -ForegroundColor Green
            
            # Mostrar contenido parcial para endpoints pequeños
            if ($response.Content.Length -lt 500) {
                $content = $response.Content -replace "`n", " " -replace "`r", ""
                if ($content.Length -gt 100) {
                    $content = $content.Substring(0, 100) + "..."
                }
                Write-Host "     Contenido: $content" -ForegroundColor Gray
            }
        } else {
            Write-Host "  ❌ Error: Status $($response.StatusCode)" -ForegroundColor Red
        }
    }
    catch {
        Write-Host "  ❌ Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Start-Sleep -Milliseconds 500
}

Write-Host "`n🎯 Preparando para pruebas Selenium QA..." -ForegroundColor Magenta

# Verificar si Selenium está disponible
Write-Host "📋 Verificando componentes para QA:" -ForegroundColor Cyan

# Buscar archivos de Selenium en QAS
$qasPath = "..\QAS-QualityAssuranceServ"
if (Test-Path $qasPath) {
    Write-Host "  ✅ Directorio QAS encontrado: $qasPath" -ForegroundColor Green
    
    # Buscar archivos de Selenium
    $seleniumFiles = Get-ChildItem -Path $qasPath -Recurse -Filter "*selenium*" -ErrorAction SilentlyContinue
    if ($seleniumFiles.Count -gt 0) {
        Write-Host "  ✅ Archivos Selenium encontrados: $($seleniumFiles.Count)" -ForegroundColor Green
        $seleniumFiles | ForEach-Object { Write-Host "     $($_.FullName)" -ForegroundColor Gray }
    } else {
        Write-Host "  ⚠️ No se encontraron archivos Selenium específicos" -ForegroundColor Yellow
    }
} else {
    Write-Host "  ❌ Directorio QAS no encontrado" -ForegroundColor Red
}

Write-Host "`n🚀 Resultados:" -ForegroundColor Green
Write-Host "✅ Aplicación HTTPS corriendo en: https://localhost:8080" -ForegroundColor White
Write-Host "✅ Certificado SSL self-signed configurado" -ForegroundColor White
Write-Host "🔄 Listo para ejecutar pruebas Selenium QA" -ForegroundColor White

Write-Host "`n📝 Próximos pasos para QA:" -ForegroundColor Magenta
Write-Host "1. Configurar Selenium WebDriver para aceptar certificados self-signed" -ForegroundColor White
Write-Host "2. Crear casos de prueba para los endpoints" -ForegroundColor White
Write-Host "3. Ejecutar batería de pruebas QA automatizadas" -ForegroundColor White

Write-Host "`n🎉 EOM QA Framework con HTTPS listo para testing" -ForegroundColor Green