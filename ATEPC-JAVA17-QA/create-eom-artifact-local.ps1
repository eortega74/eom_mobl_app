# Script para crear artifact EOM QA localmente (simulación del pipeline)

Write-Host "🎯 CREANDO ARTIFACT EOM QA LOCALMENTE" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Cyan

# Variables (simulando las del pipeline)
$BUILD_NUMBER = Get-Date -Format "HHmm"  # Simular run number
$CURRENT_DATE = Get-Date -Format "yyyyMMdd"
$ARTIFACT_NAME = "eom-qa-$BUILD_NUMBER-$CURRENT_DATE"
$RELEASE_VERSION = "eom-qa-v1.0.$BUILD_NUMBER"
$BRANCH_NAME = "local-development"

Write-Host "📋 Configuración:" -ForegroundColor Yellow
Write-Host "   Artifact: $ARTIFACT_NAME" -ForegroundColor White
Write-Host "   Version: $RELEASE_VERSION" -ForegroundColor White
Write-Host "   Fecha: $CURRENT_DATE" -ForegroundColor White

# Step 1: Build Application
Write-Host "`n🏗️ STEP 1: Compilando aplicación..." -ForegroundColor Cyan
try {
    mvn clean package -DskipTests -q
    if (Test-Path "target\eom-qa-automation-1.0.0-SNAPSHOT.jar") {
        Write-Host "   ✅ JAR creado exitosamente" -ForegroundColor Green
    } else {
        throw "JAR no encontrado"
    }
} catch {
    Write-Host "   ❌ Error en compilación: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Create Artifact Structure
Write-Host "`n📦 STEP 2: Creando estructura de artifact..." -ForegroundColor Cyan

# Create artifact directory structure
$artifactDir = "eom-qa-artifact"
Remove-Item $artifactDir -Recurse -Force -ErrorAction SilentlyContinue

New-Item -ItemType Directory -Path $artifactDir -Force | Out-Null
New-Item -ItemType Directory -Path "$artifactDir\app" -Force | Out-Null
New-Item -ItemType Directory -Path "$artifactDir\config" -Force | Out-Null
New-Item -ItemType Directory -Path "$artifactDir\docs" -Force | Out-Null
New-Item -ItemType Directory -Path "$artifactDir\qa-reports" -Force | Out-Null
New-Item -ItemType Directory -Path "$artifactDir\scripts" -Force | Out-Null

Write-Host "   ✅ Estructura de directorios creada" -ForegroundColor Green

# Step 3: Copy Application Files
Write-Host "`n📁 STEP 3: Copiando archivos de aplicación..." -ForegroundColor Cyan

# Copy main JAR
Copy-Item "target\eom-qa-automation-1.0.0-SNAPSHOT.jar" "$artifactDir\app\eom-qa-app.jar"
Write-Host "   ✅ Aplicación copiada: eom-qa-app.jar" -ForegroundColor Green

# Copy configuration
if (Test-Path "src\main\resources\application.properties") {
    Copy-Item "src\main\resources\application.properties" "$artifactDir\config\"
    Write-Host "   ✅ Configuración copiada: application.properties" -ForegroundColor Green
}

# Copy documentation
if (Test-Path "README.md") {
    Copy-Item "README.md" "$artifactDir\docs\"
    Write-Host "   ✅ Documentación copiada: README.md" -ForegroundColor Green
}

# Copy QA test results if they exist
if (Test-Path "target\surefire-reports") {
    Copy-Item "target\surefire-reports\*" "$artifactDir\qa-reports\" -Recurse -ErrorAction SilentlyContinue
    Write-Host "   ✅ Reportes QA copiados" -ForegroundColor Green
} else {
    Write-Host "   ⚠️ No se encontraron reportes QA" -ForegroundColor Yellow
}

# Step 4: Create Deployment Scripts
Write-Host "`n⚙️ STEP 4: Creando scripts de deployment..." -ForegroundColor Cyan

# Windows deployment script
@"
@echo off
echo ========================================
echo    EOM QA Automation - $RELEASE_VERSION
echo ========================================
echo.
echo Iniciando aplicacion EOM QA...
echo URL: https://localhost:8080
echo.
cd /d "%~dp0"
java -jar app\eom-qa-app.jar
pause
"@ | Out-File -FilePath "$artifactDir\scripts\start-eom-qa.bat" -Encoding ASCII

# PowerShell deployment script
@"
# EOM QA Automation Launcher
# Version: $RELEASE_VERSION

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   EOM QA Automation - $RELEASE_VERSION" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "Iniciando aplicacion EOM QA..." -ForegroundColor Yellow
Write-Host "URL: https://localhost:8080" -ForegroundColor Green

# Change to script directory
Set-Location `$PSScriptRoot

# Start application
java -jar app\eom-qa-app.jar
"@ | Out-File -FilePath "$artifactDir\scripts\start-eom-qa.ps1" -Encoding UTF8

Write-Host "   ✅ Scripts de deployment creados" -ForegroundColor Green

# Step 5: Create Artifact Information
Write-Host "`n📋 STEP 5: Generando información del artifact..." -ForegroundColor Cyan

$artifactInfo = @"
EOM QA Automation Artifact
==========================

📦 ARTIFACT INFORMATION:
   Name: $ARTIFACT_NAME
   Version: $RELEASE_VERSION
   Build Date: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
   Branch: $BRANCH_NAME
   Build Type: Local Development Build

🗂️ STRUCTURE:
   app\
   ├── eom-qa-app.jar          # Main Spring Boot Application
   
   config\
   ├── application.properties   # Application Configuration
   
   docs\
   ├── README.md               # Project Documentation
   
   qa-reports\
   ├── [test-results]          # QA Test Reports (if available)
   
   scripts\
   ├── start-eom-qa.bat       # Windows Launcher
   └── start-eom-qa.ps1       # PowerShell Launcher

🚀 DEPLOYMENT:
   1. Extract this ZIP to desired location
   2. Run: scripts\start-eom-qa.bat (Windows)
      OR: scripts\start-eom-qa.ps1 (PowerShell)
   3. Access application: https://localhost:8080
   4. Accept SSL certificate warning (self-signed)

🧪 QA FEATURES:
   - HTTPS enabled with self-signed certificate
   - Health endpoint: /health
   - API endpoints: /api/hello, /json
   - Web interface with form processing
   - Selenium-ready for automated testing

🔧 REQUIREMENTS:
   - Java 21 or later
   - Port 8080 available
   - Network access for HTTPS

📝 NOTES:
   - This is a development/testing build
   - SSL certificate is self-signed (expect browser warnings)
   - Application includes embedded Tomcat server
   - Configuration can be modified in config\application.properties

Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
EOM QA Team
"@

$artifactInfo | Out-File -FilePath "$artifactDir\ARTIFACT-INFO.txt" -Encoding UTF8
Write-Host "   ✅ Información del artifact generada" -ForegroundColor Green

# Step 6: Create ZIP Package
Write-Host "`n📦 STEP 6: Creando paquete ZIP..." -ForegroundColor Cyan

$zipFile = "$ARTIFACT_NAME.zip"
Remove-Item $zipFile -Force -ErrorAction SilentlyContinue

Compress-Archive -Path "$artifactDir\*" -DestinationPath $zipFile -Force

if (Test-Path $zipFile) {
    $fileSize = (Get-Item $zipFile).Length
    $fileSizeMB = [math]::Round($fileSize / 1MB, 2)
    
    Write-Host "   ✅ ZIP creado exitosamente" -ForegroundColor Green
    Write-Host "   📁 Archivo: $zipFile" -ForegroundColor White
    Write-Host "   📏 Tamaño: $fileSizeMB MB" -ForegroundColor White
} else {
    Write-Host "   ❌ Error creando ZIP" -ForegroundColor Red
    exit 1
}

# Step 7: Verification
Write-Host "`n✅ STEP 7: Verificación final..." -ForegroundColor Cyan

Write-Host "   📋 Archivos en artifact:" -ForegroundColor Yellow
Get-ChildItem $artifactDir -Recurse | ForEach-Object {
    $relativePath = $_.FullName.Replace("$(Get-Location)\$artifactDir\", "")
    if (!$_.PSIsContainer) {
        Write-Host "      $relativePath" -ForegroundColor Gray
    }
}

# Cleanup
Write-Host "`n🧹 Limpiando archivos temporales..." -ForegroundColor Cyan
Remove-Item $artifactDir -Recurse -Force -ErrorAction SilentlyContinue
Write-Host "   ✅ Limpieza completada" -ForegroundColor Green

# Final Summary
Write-Host "`n" -NoNewline
Write-Host "🎉 ARTIFACT EOM QA CREADO EXITOSAMENTE" -ForegroundColor Green -BackgroundColor Black
Write-Host "=======================================" -ForegroundColor Cyan

Write-Host "`n📦 RESUMEN:" -ForegroundColor Yellow
Write-Host "   Artifact: $zipFile" -ForegroundColor White
Write-Host "   Version: $RELEASE_VERSION" -ForegroundColor White
Write-Host "   Tamaño: $fileSizeMB MB" -ForegroundColor White
Write-Host "   Ubicación: $(Get-Location)\$zipFile" -ForegroundColor Gray

Write-Host "`n🚀 PRÓXIMOS PASOS:" -ForegroundColor Cyan
Write-Host "   1. Extraer: $zipFile" -ForegroundColor White
Write-Host "   2. Ejecutar: scripts\start-eom-qa.bat" -ForegroundColor White
Write-Host "   3. Acceder: https://localhost:8080" -ForegroundColor White
Write-Host "   4. Ejecutar pruebas QA con Selenium" -ForegroundColor White

Write-Host "`n🔗 Este artifact simula exactamente lo que crea el pipeline de GitHub Actions" -ForegroundColor Magenta