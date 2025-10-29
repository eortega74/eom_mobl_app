#!/usr/bin/env powershell
# =================================================
#     SCRIPT REAL DE PRUEBAS QA - EOM AUTOMATION
# =================================================
# Este script ejecuta pruebas QA REALES contra la aplicación desplegada

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "     PRUEBAS QA REALES - EOM AUTOMATION" -ForegroundColor Cyan  
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$APP_URL = "http://localhost:8080"
$TEST_RESULTS = @()

Write-Host "[STAGE 1] Verificando que la aplicación esté corriendo..." -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri $APP_URL -Method GET -TimeoutSec 10
    Write-Host "✅ Aplicación EOM QA respondiendo en puerto 8080" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Gray
} catch {
    Write-Host "❌ ERROR: Aplicación no está corriendo en $APP_URL" -ForegroundColor Red
    Write-Host "   Detalle: $($_.Exception.Message)" -ForegroundColor Gray
    exit 1
}

Write-Host ""
Write-Host "[STAGE 2] Ejecutando pruebas de endpoints REST..." -ForegroundColor Yellow

# Test 1: Endpoint principal
Write-Host "  Test 1: Probando endpoint principal (/)" -ForegroundColor White
try {
    $response = Invoke-WebRequest -Uri "$APP_URL/" -Method GET
    if ($response.StatusCode -eq 200) {
        Write-Host "    ✅ PASS - Endpoint principal responde OK" -ForegroundColor Green
        $TEST_RESULTS += "✅ Test 1 PASS: Endpoint principal (/) - Status 200"
    } else {
        Write-Host "    ❌ FAIL - Status inesperado: $($response.StatusCode)" -ForegroundColor Red
        $TEST_RESULTS += "❌ Test 1 FAIL: Endpoint principal - Status $($response.StatusCode)"
    }
} catch {
    Write-Host "    ❌ FAIL - Error: $($_.Exception.Message)" -ForegroundColor Red
    $TEST_RESULTS += "❌ Test 1 FAIL: Endpoint principal - Error de conexión"
}

# Test 2: Endpoint EOM
Write-Host "  Test 2: Probando endpoint EOM (/eom)" -ForegroundColor White
try {
    $response = Invoke-WebRequest -Uri "$APP_URL/eom" -Method GET
    if ($response.StatusCode -eq 200) {
        Write-Host "    ✅ PASS - Endpoint EOM responde OK" -ForegroundColor Green
        Write-Host "    Respuesta: $($response.Content.Substring(0, [Math]::Min(50, $response.Content.Length)))..." -ForegroundColor Gray
        $TEST_RESULTS += "✅ Test 2 PASS: Endpoint EOM (/eom) - Status 200"
    } else {
        Write-Host "    ❌ FAIL - Status inesperado: $($response.StatusCode)" -ForegroundColor Red
        $TEST_RESULTS += "❌ Test 2 FAIL: Endpoint EOM - Status $($response.StatusCode)"
    }
} catch {
    Write-Host "    ❌ FAIL - Error: $($_.Exception.Message)" -ForegroundColor Red
    $TEST_RESULTS += "❌ Test 2 FAIL: Endpoint EOM - Error de conexión"
}

# Test 3: Endpoint QA
Write-Host "  Test 3: Probando endpoint QA (/qa)" -ForegroundColor White
try {
    $response = Invoke-WebRequest -Uri "$APP_URL/qa" -Method GET
    if ($response.StatusCode -eq 200) {
        Write-Host "    ✅ PASS - Endpoint QA responde OK" -ForegroundColor Green
        Write-Host "    Respuesta: $($response.Content.Substring(0, [Math]::Min(50, $response.Content.Length)))..." -ForegroundColor Gray
        $TEST_RESULTS += "✅ Test 3 PASS: Endpoint QA (/qa) - Status 200"
    } else {
        Write-Host "    ❌ FAIL - Status inesperado: $($response.StatusCode)" -ForegroundColor Red
        $TEST_RESULTS += "❌ Test 3 FAIL: Endpoint QA - Status $($response.StatusCode)"
    }
} catch {
    Write-Host "    ❌ FAIL - Error: $($_.Exception.Message)" -ForegroundColor Red
    $TEST_RESULTS += "❌ Test 3 FAIL: Endpoint QA - Error de conexión"
}

# Test 4: Endpoint health actuator (si existe)
Write-Host "  Test 4: Probando endpoint health (/actuator/health)" -ForegroundColor White
try {
    $response = Invoke-WebRequest -Uri "$APP_URL/actuator/health" -Method GET
    if ($response.StatusCode -eq 200) {
        Write-Host "    ✅ PASS - Endpoint health responde OK" -ForegroundColor Green
        $TEST_RESULTS += "✅ Test 4 PASS: Endpoint health (/actuator/health) - Status 200"
    } else {
        Write-Host "    ⚠️  WARN - Health endpoint no disponible o configurado" -ForegroundColor Yellow
        $TEST_RESULTS += "⚠️ Test 4 WARN: Endpoint health - No disponible"
    }
} catch {
    Write-Host "    ⚠️  WARN - Health endpoint no configurado" -ForegroundColor Yellow
    $TEST_RESULTS += "⚠️ Test 4 WARN: Endpoint health - No configurado"
}

Write-Host ""
Write-Host "[STAGE 3] Pruebas de carga básica..." -ForegroundColor Yellow

Write-Host "  Test 5: Prueba de carga básica (10 requests)" -ForegroundColor White
$successful_requests = 0
$total_requests = 10

for ($i = 1; $i -le $total_requests; $i++) {
    try {
        $response = Invoke-WebRequest -Uri "$APP_URL/eom" -Method GET -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            $successful_requests++
        }
    } catch {
        Write-Host "    Request $i falló" -ForegroundColor Red
    }
}

$success_rate = ($successful_requests / $total_requests) * 100
if ($success_rate -ge 90) {
    Write-Host "    ✅ PASS - Carga básica OK ($successful_requests/$total_requests - $success_rate%)" -ForegroundColor Green
    $TEST_RESULTS += "✅ Test 5 PASS: Carga básica - $success_rate% éxito"
} else {
    Write-Host "    ❌ FAIL - Carga básica falló ($successful_requests/$total_requests - $success_rate%)" -ForegroundColor Red
    $TEST_RESULTS += "❌ Test 5 FAIL: Carga básica - $success_rate% éxito"
}

Write-Host ""
Write-Host "[STAGE 4] Generando reporte de resultados..." -ForegroundColor Yellow

# Contar resultados
$passed_tests = ($TEST_RESULTS | Where-Object { $_ -like "*PASS*" }).Count
$failed_tests = ($TEST_RESULTS | Where-Object { $_ -like "*FAIL*" }).Count
$warning_tests = ($TEST_RESULTS | Where-Object { $_ -like "*WARN*" }).Count
$total_tests = $TEST_RESULTS.Count

# Generar reporte
$report_date = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$report_content = @"
# REPORTE DE PRUEBAS QA - EOM AUTOMATION
**Fecha:** $report_date  
**Aplicación:** EOM QA Automation Framework  
**URL Probada:** $APP_URL  

## RESUMEN DE RESULTADOS
- **Total de pruebas:** $total_tests
- **Exitosas:** $passed_tests ✅
- **Fallidas:** $failed_tests ❌  
- **Advertencias:** $warning_tests ⚠️
- **Tasa de éxito:** $([Math]::Round(($passed_tests / $total_tests) * 100, 2))%

## RESULTADOS DETALLADOS
$($TEST_RESULTS | ForEach-Object { "- $_" } | Out-String)

## CONCLUSIÓN
$( if ($failed_tests -eq 0) { "✅ **TODAS LAS PRUEBAS PRINCIPALES PASARON** - La aplicación EOM QA está funcionando correctamente." } else { "❌ **SE ENCONTRARON FALLAS** - Revisar los tests fallidos antes del deployment a producción." } )

---
*Reporte generado automáticamente por EOM QA Test Suite*
"@

# Guardar reporte
$report_file = "REPORTE-QA-REAL-$(Get-Date -Format 'yyyyMMdd-HHmmss').md"
$report_content | Out-File -FilePath $report_file -Encoding UTF8

Write-Host "📄 Reporte guardado en: $report_file" -ForegroundColor Cyan

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "     RESUMEN FINAL DE PRUEBAS QA" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Total: $total_tests | Exitosas: $passed_tests | Fallidas: $failed_tests | Advertencias: $warning_tests" -ForegroundColor White

if ($failed_tests -eq 0) {
    Write-Host ""
    Write-Host "🎉 ¡ÉXITO! Todas las pruebas principales pasaron." -ForegroundColor Green
    Write-Host "   La aplicación EOM QA está lista para uso." -ForegroundColor Green
    exit 0
} else {
    Write-Host ""
    Write-Host "⚠️  ATENCIÓN: Se encontraron $failed_tests fallas." -ForegroundColor Yellow
    Write-Host "   Revisar antes de deployment a producción." -ForegroundColor Yellow
    exit 1
}