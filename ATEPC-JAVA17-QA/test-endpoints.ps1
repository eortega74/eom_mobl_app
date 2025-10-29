# Script de prueba QA para certificados self-signed

Write-Host "Probando endpoints EOM QA con HTTPS..." -ForegroundColor Green

# Configurar PowerShell para aceptar certificados self-signed
add-type @"
    using System.Net;
    using System.Security.Cryptography.X509Certificates;
    public class TrustAllCertsPolicy : ICertificatePolicy {
        public bool CheckValidationResult(
            ServicePoint srvPoint, X509Certificate certificate,
            WebRequest request, int certificateProblem) {
            return true;
        }
    }
"@
[System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12

# Endpoints a probar
$endpoints = @{
    "Pagina Principal" = "/"
    "Health Check" = "/health" 
    "API Hello" = "/api/hello"
    "JSON API" = "/json"
}

Write-Host "Probando endpoints..." -ForegroundColor Cyan

foreach ($name in $endpoints.Keys) {
    $endpoint = $endpoints[$name]
    $url = "https://localhost:8080$endpoint"
    
    Write-Host "Probando $name ($endpoint)..." -ForegroundColor Yellow
    
    try {
        $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 10
        
        if ($response.StatusCode -eq 200) {
            Write-Host "  ✓ OK - Status: $($response.StatusCode)" -ForegroundColor Green
            
            # Mostrar contenido parcial
            $content = $response.Content
            if ($content.Length -gt 100) {
                $content = $content.Substring(0, 100) + "..."
            }
            Write-Host "    Contenido: $content" -ForegroundColor Gray
        } else {
            Write-Host "  ✗ Error - Status: $($response.StatusCode)" -ForegroundColor Red
        }
    }
    catch {
        Write-Host "  ✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host ""
}

Write-Host "Prueba completada" -ForegroundColor Green
Write-Host "La aplicacion esta corriendo en: https://localhost:8080" -ForegroundColor Cyan
Write-Host "(Acepta la advertencia de seguridad del navegador para certificados self-signed)" -ForegroundColor Yellow