# Script para crear certificado SSL self-signed para EOM QA Hello World

Write-Host "🔐 Creando certificado SSL para EOM QA Hello World..." -ForegroundColor Green

# Directorio donde guardar el keystore
$keystoreDir = "src\main\resources"
$keystorePath = "$keystoreDir\keystore.p12"

# Crear directorio si no existe
if (!(Test-Path $keystoreDir)) {
    New-Item -ItemType Directory -Force -Path $keystoreDir
    Write-Host "📁 Directorio resources creado" -ForegroundColor Yellow
}

# Eliminar keystore anterior si existe
if (Test-Path $keystorePath) {
    Remove-Item $keystorePath -Force
    Write-Host "🗑️ Keystore anterior eliminado" -ForegroundColor Yellow
}

# Generar keystore con certificado self-signed usando keytool
$keytoolCommand = @"
keytool -genkeypair -alias localdev -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore $keystorePath -validity 365 -dname "CN=localhost, OU=EOM QA, O=EOM, L=City, ST=State, C=US" -storepass changeit -keypass changeit
"@

Write-Host "🔑 Ejecutando keytool..." -ForegroundColor Cyan
try {
    Invoke-Expression $keytoolCommand
    
    if (Test-Path $keystorePath) {
        Write-Host "✅ Certificado SSL creado exitosamente en: $keystorePath" -ForegroundColor Green
        Write-Host "🔒 Password del keystore: changeit" -ForegroundColor Yellow
        Write-Host "🌐 Alias del certificado: localdev" -ForegroundColor Yellow
        Write-Host "📅 Válido por: 365 días" -ForegroundColor Yellow
        
        # Mostrar información del certificado
        Write-Host "`n📋 Información del certificado:" -ForegroundColor Cyan
        keytool -list -v -keystore $keystorePath -storepass changeit -alias localdev
        
        Write-Host "`n🚀 Ahora puedes iniciar la aplicación con HTTPS en: https://localhost:8080" -ForegroundColor Green
        Write-Host "⚠️ El navegador mostrará advertencia de seguridad (es normal con certificados self-signed)" -ForegroundColor Yellow
        
    } else {
        Write-Host "❌ Error: No se pudo crear el keystore" -ForegroundColor Red
        exit 1
    }
    
} catch {
    Write-Host "❌ Error ejecutando keytool: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "💡 Asegúrate de tener Java instalado y keytool en el PATH" -ForegroundColor Yellow
    exit 1
}

Write-Host "`n📝 Siguientes pasos:" -ForegroundColor Magenta
Write-Host "1. Compilar la aplicación: mvn package -Dmaven.test.skip=true" -ForegroundColor White
Write-Host "2. Ejecutar con HTTPS: java -jar target\eom-qa-automation-1.0.0-SNAPSHOT.jar" -ForegroundColor White
Write-Host "3. Acceder a: https://localhost:8080" -ForegroundColor White
Write-Host "4. Ejecutar pruebas Selenium QA" -ForegroundColor White

Write-Host "`n🔐 Certificado SSL listo para EOM QA Framework" -ForegroundColor Green