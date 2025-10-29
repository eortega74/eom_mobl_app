#!/usr/bin/env powershell
# =================================================
#     SCRIPT REAL DE PRUEBAS QA - EOM AUTOMATION
# =================================================
# Este script ejecuta pruebas QA REALES contra la aplicacion desplegada

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "     PRUEBAS QA REALES - EOM AUTOMATION" -ForegroundColor Cyan  
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$APP_URL = "https://localhost:8080"
$TEST_RESULTS = @()

# Configurar para ignorar certificados SSL en testing
if (-not ([System.Management.Automation.PSTypeName]'ServerCertificateValidationCallback').Type) {
    $certCallback = @"
        using System;
        using System.Net;
        using System.Net.Security;
        using System.Security.Cryptography.X509Certificates;
        public class ServerCertificateValidationCallback
        {
            public static void Ignore()
            {
                if(ServicePointManager.ServerCertificateValidationCallback ==null)
                {
                    ServicePointManager.ServerCertificateValidationCallback += 
                        delegate
                        (
                            Object obj, 
                            X509Certificate certificate, 
                            X509Chain chain, 
                            SslPolicyErrors errors
                        )
                        {
                            return true;
                        };
                }
            }
        }
"@
    Add-Type $certCallback
}
[ServerCertificateValidationCallback]::Ignore()

Write-Host "[STAGE 1] Verificando que la aplicacion este corriendo..." -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri $APP_URL -Method GET -TimeoutSec 10 -SkipCertificateCheck
    Write-Host "✅ Aplicacion EOM QA respondiendo en puerto 8080 (HTTPS)" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Gray
} catch {
    Write-Host "❌ ERROR: Aplicacion no esta corriendo en $APP_URL" -ForegroundColor Red
    Write-Host "   Detalle: $($_.Exception.Message)" -ForegroundColor Gray
    exit 1
}

Write-Host ""
Write-Host "[STAGE 2] Ejecutando pruebas de endpoints REST..." -ForegroundColor Yellow

# Test 1: Endpoint principal (redirige a login por seguridad)
Write-Host "  Test 1: Probando endpoint principal (/) - Esperando redireccion a login" -ForegroundColor White
try {
    $response = Invoke-WebRequest -Uri "$APP_URL/" -Method GET -SkipCertificateCheck
    if ($response.StatusCode -eq 200 -and $response.Content -like "*login*") {
        Write-Host "    ✅ PASS - Aplicacion redirige correctamente a login" -ForegroundColor Green
        $TEST_RESULTS += "✅ Test 1 PASS: Endpoint principal (/) - Redireccion a login OK"
    } else {
        Write-Host "    ⚠️  WARN - Respuesta inesperada: $($response.StatusCode)" -ForegroundColor Yellow
        $TEST_RESULTS += "⚠️ Test 1 WARN: Endpoint principal - Status $($response.StatusCode)"
    }
} catch {
    if ($_.Exception.Message -like "*302*" -or $_.Exception.Message -like "*redirect*") {
        Write-Host "    ✅ PASS - Aplicacion redirige correctamente (302)" -ForegroundColor Green
        $TEST_RESULTS += "✅ Test 1 PASS: Endpoint principal - Redireccion de seguridad OK"
    } else {
        Write-Host "    ❌ FAIL - Error: $($_.Exception.Message)" -ForegroundColor Red
        $TEST_RESULTS += "❌ Test 1 FAIL: Endpoint principal - Error de conexion"
    }
}

# Test 2: Probar con credenciales basicas (user/password generado)
Write-Host "  Test 2: Probando autenticacion basica con usuario 'user'" -ForegroundColor White
$username = "user"
$password = "90d325e6-4c11-4fea-bc9a-35986ae6046b"  # Password del log
$credentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("${username}:${password}"))

try {
    $headers = @{
        "Authorization" = "Basic $credentials"
    }
    $response = Invoke-WebRequest -Uri "$APP_URL/" -Method GET -Headers $headers -SkipCertificateCheck
    if ($response.StatusCode -eq 200) {
        Write-Host "    ✅ PASS - Autenticacion basica funciona" -ForegroundColor Green
        Write-Host "    Contenido: $($response.Content.Substring(0, [Math]::Min(100, $response.Content.Length)))..." -ForegroundColor Gray
        $TEST_RESULTS += "✅ Test 2 PASS: Autenticacion basica - Login exitoso"
    } else {
        Write-Host "    ❌ FAIL - Status inesperado: $($response.StatusCode)" -ForegroundColor Red
        $TEST_RESULTS += "❌ Test 2 FAIL: Autenticacion basica - Status $($response.StatusCode)"
    }
} catch {
    Write-Host "    ❌ FAIL - Error en autenticacion: $($_.Exception.Message)" -ForegroundColor Red
    $TEST_RESULTS += "❌ Test 2 FAIL: Autenticacion basica - Error de credenciales"
}

# Test 3: Endpoint EOM con autenticacion
Write-Host "  Test 3: Probando endpoint EOM (/eom) con autenticacion" -ForegroundColor White
try {
    $headers = @{
        "Authorization" = "Basic $credentials"
    }
    $response = Invoke-WebRequest -Uri "$APP_URL/eom" -Method GET -Headers $headers -SkipCertificateCheck
    if ($response.StatusCode -eq 200) {
        Write-Host "    ✅ PASS - Endpoint EOM responde OK" -ForegroundColor Green
        Write-Host "    Respuesta: $($response.Content.Substring(0, [Math]::Min(80, $response.Content.Length)))..." -ForegroundColor Gray
        $TEST_RESULTS += "✅ Test 3 PASS: Endpoint EOM (/eom) - Status 200"
    } else {
        Write-Host "    ❌ FAIL - Status inesperado: $($response.StatusCode)" -ForegroundColor Red
        $TEST_RESULTS += "❌ Test 3 FAIL: Endpoint EOM - Status $($response.StatusCode)"
    }
} catch {
    Write-Host "    ❌ FAIL - Error: $($_.Exception.Message)" -ForegroundColor Red
    $TEST_RESULTS += "❌ Test 3 FAIL: Endpoint EOM - Error de conexion"
}

# Test 4: Endpoint QA con autenticacion
Write-Host "  Test 4: Probando endpoint QA (/qa) con autenticacion" -ForegroundColor White
try {
    $headers = @{
        "Authorization" = "Basic $credentials"
    }
    $response = Invoke-WebRequest -Uri "$APP_URL/qa" -Method GET -Headers $headers -SkipCertificateCheck
    if ($response.StatusCode -eq 200) {
        Write-Host "    ✅ PASS - Endpoint QA responde OK" -ForegroundColor Green
        Write-Host "    Respuesta: $($response.Content.Substring(0, [Math]::Min(80, $response.Content.Length)))..." -ForegroundColor Gray
        $TEST_RESULTS += "✅ Test 4 PASS: Endpoint QA (/qa) - Status 200"
    } else {
        Write-Host "    ❌ FAIL - Status inesperado: $($response.StatusCode)" -ForegroundColor Red
        $TEST_RESULTS += "❌ Test 4 FAIL: Endpoint QA - Status $($response.StatusCode)"
    }
} catch {
    Write-Host "    ❌ FAIL - Error: $($_.Exception.Message)" -ForegroundColor Red
    $TEST_RESULTS += "❌ Test 4 FAIL: Endpoint QA - Error de conexion"
}

Write-Host ""
Write-Host "[STAGE 3] Pruebas de carga basica..." -ForegroundColor Yellow

Write-Host "  Test 5: Prueba de carga basica (5 requests autenticados)" -ForegroundColor White
$successful_requests = 0
$total_requests = 5

for ($i = 1; $i -le $total_requests; $i++) {
    try {
        $headers = @{
            "Authorization" = "Basic $credentials"
        }
        $response = Invoke-WebRequest -Uri "$APP_URL/eom" -Method GET -Headers $headers -TimeoutSec 5 -SkipCertificateCheck
        if ($response.StatusCode -eq 200) {
            $successful_requests++
        }
    } catch {
        Write-Host "    Request $i fallo" -ForegroundColor Red
    }
}

$success_rate = ($successful_requests / $total_requests) * 100
if ($success_rate -ge 80) {
    Write-Host "    ✅ PASS - Carga basica OK ($successful_requests/$total_requests - $success_rate%)" -ForegroundColor Green
    $TEST_RESULTS += "✅ Test 5 PASS: Carga basica - $success_rate% exito"
} else {
    Write-Host "    ❌ FAIL - Carga basica fallo ($successful_requests/$total_requests - $success_rate%)" -ForegroundColor Red
    $TEST_RESULTS += "❌ Test 5 FAIL: Carga basica - $success_rate% exito"
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
# REPORTE DE PRUEBAS QA REALES - EOM AUTOMATION
**Fecha:** $report_date  
**Aplicacion:** EOM QA Automation Framework  
**URL Probada:** $APP_URL (HTTPS)  

## RESUMEN DE RESULTADOS
- **Total de pruebas:** $total_tests
- **Exitosas:** $passed_tests ✅
- **Fallidas:** $failed_tests ❌  
- **Advertencias:** $warning_tests ⚠️
- **Tasa de exito:** $([Math]::Round(($passed_tests / $total_tests) * 100, 2))%

## RESULTADOS DETALLADOS
$($TEST_RESULTS | ForEach-Object { "- $_" } | Out-String)

## CONFIGURACION DE SEGURIDAD DETECTADA
- **Protocolo:** HTTPS con certificado SSL self-signed
- **Autenticacion:** Spring Security habilitado
- **Usuario por defecto:** user
- **Password temporal:** 90d325e6-4c11-4fea-bc9a-35986ae6046b

## ENDPOINTS PROBADOS
- **/** - Pagina principal (con redireccion de seguridad)
- **/eom** - Endpoint EOM personalizado 
- **/qa** - Endpoint QA personalizado

## CONCLUSION
$( if ($failed_tests -eq 0) { "✅ **TODAS LAS PRUEBAS PRINCIPALES PASARON** - La aplicacion EOM QA esta funcionando correctamente con seguridad habilitada." } else { "❌ **SE ENCONTRARON FALLAS** - Revisar los tests fallidos antes del deployment a produccion." } )

---
*Reporte generado automaticamente por EOM QA Test Suite*
*Aplicacion desplegada y probada en tiempo real*
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
    Write-Host "🎉 ¡EXITO! Todas las pruebas principales pasaron." -ForegroundColor Green
    Write-Host "   La aplicacion EOM QA esta lista y funcionando." -ForegroundColor Green
    Write-Host "   Aplicacion corriendo en: $APP_URL" -ForegroundColor Green
    exit 0
} else {
    Write-Host ""
    Write-Host "⚠️  ATENCION: Se encontraron $failed_tests fallas." -ForegroundColor Yellow
    Write-Host "   Revisar antes de deployment a produccion." -ForegroundColor Yellow
    exit 1
}