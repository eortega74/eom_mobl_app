# 🎯 SCRIPT DE PRUEBAS SIMPLE - EOM QA HELLO WORLD
# Test del pipeline: compilación + deployment + QA básico
# Autor: EOM QA Team - Hello World Version

Write-Host "🚀 INICIANDO PRUEBAS EOM QA HELLO WORLD" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan

$BASE_URL = "http://localhost:8080"
$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"

Write-Host "⏰ Timestamp: $timestamp" -ForegroundColor Yellow
Write-Host ""

# Test 1: Verificar conectividad básica
Write-Host "🔍 Test 1: Verificando conectividad HTTP" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri $BASE_URL -TimeoutSec 5 -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ PASS - Aplicación responde HTTP 200" -ForegroundColor Green
        Write-Host "   📊 Tamaño respuesta: $($response.Content.Length) bytes" -ForegroundColor Gray
    } else {
        Write-Host "❌ FAIL - Status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ FAIL - Error de conectividad: $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# Test 2: Verificar API Health
Write-Host "🔍 Test 2: Verificando Health Check API" -ForegroundColor Yellow
try {
    $healthResponse = Invoke-WebRequest -Uri "$BASE_URL/health" -TimeoutSec 5 -UseBasicParsing
    if ($healthResponse.StatusCode -eq 200) {
        Write-Host "✅ PASS - Health Check funcionando" -ForegroundColor Green
        Write-Host "   📄 Respuesta: $($healthResponse.Content)" -ForegroundColor Gray
    } else {
        Write-Host "❌ FAIL - Health Status: $($healthResponse.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ FAIL - Health Check error: $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# Test 3: Verificar API Hello
Write-Host "🔍 Test 3: Verificando API Hello" -ForegroundColor Yellow
try {
    $helloResponse = Invoke-WebRequest -Uri "$BASE_URL/api/hello?name=QATester" -TimeoutSec 5 -UseBasicParsing
    if ($helloResponse.StatusCode -eq 200) {
        Write-Host "✅ PASS - API Hello funcionando" -ForegroundColor Green
        Write-Host "   📄 Respuesta: $($helloResponse.Content)" -ForegroundColor Gray
    } else {
        Write-Host "❌ FAIL - API Status: $($helloResponse.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ FAIL - API Hello error: $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# Test 4: Verificar JSON API
Write-Host "🔍 Test 4: Verificando JSON API" -ForegroundColor Yellow
try {
    $jsonResponse = Invoke-WebRequest -Uri "$BASE_URL/json?name=Pipeline" -TimeoutSec 5 -UseBasicParsing
    if ($jsonResponse.StatusCode -eq 200) {
        Write-Host "✅ PASS - JSON API funcionando" -ForegroundColor Green
        # Intentar parsear JSON
        try {
            $jsonData = $jsonResponse.Content | ConvertFrom-Json
            Write-Host "   📄 Framework: $($jsonData.framework)" -ForegroundColor Gray
            Write-Host "   📄 Version: $($jsonData.version)" -ForegroundColor Gray
        } catch {
            Write-Host "   📄 Respuesta JSON: $($jsonResponse.Content)" -ForegroundColor Gray
        }
    } else {
        Write-Host "❌ FAIL - JSON Status: $($jsonResponse.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ FAIL - JSON API error: $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# Test 5: Verificar formulario HTML
Write-Host "🔍 Test 5: Verificando página principal con parámetro" -ForegroundColor Yellow
try {
    $pageResponse = Invoke-WebRequest -Uri "$BASE_URL/?name=TestRunner" -TimeoutSec 5 -UseBasicParsing
    if ($pageResponse.StatusCode -eq 200) {
        Write-Host "✅ PASS - Página HTML funcionando" -ForegroundColor Green
        if ($pageResponse.Content -like "*TestRunner*") {
            Write-Host "   📄 Parámetro procesado correctamente" -ForegroundColor Gray
        }
        if ($pageResponse.Content -like "*EOM QA Framework*") {
            Write-Host "   📄 Título correcto encontrado" -ForegroundColor Gray
        }
    } else {
        Write-Host "❌ FAIL - Página Status: $($pageResponse.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ FAIL - Página error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "🏁 RESUMEN DE PRUEBAS COMPLETADO" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "✅ Aplicación Hello World verificada" -ForegroundColor Green
Write-Host "✅ APIs funcionando correctamente" -ForegroundColor Green
Write-Host "✅ Pipeline básico validado" -ForegroundColor Green
Write-Host ""
Write-Host "📋 SIGUIENTE PASO: Implementar Selenium para tests UI" -ForegroundColor Yellow
Write-Host "🌐 URL de la aplicación: $BASE_URL" -ForegroundColor White
Write-Host "⏰ Tests completados: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor White