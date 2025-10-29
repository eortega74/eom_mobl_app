# EOM QA - Deploy to ed-ora App Service
Write-Host "🚀 EOM QA - Desplegando a ed-ora" -ForegroundColor Green
Write-Host "===========================" -ForegroundColor Green
Write-Host "   Resource Group: zg1-rtx-dce-npd01-x12d1-atecor" -ForegroundColor Cyan
Write-Host "   App Service: ed-ora" -ForegroundColor Cyan
Write-Host ""

# Verificar conexión Azure
Write-Host "1. ✅ Verificando Azure CLI..." -ForegroundColor Yellow
az account show --query "name" --output tsv

# Compilar la aplicación
Write-Host "2. 🔨 Compilando aplicación..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✅ Compilación exitosa" -ForegroundColor Green
    
    # Verificar JAR
    $jarFile = Get-ChildItem -Path "target" -Filter "*.jar" | Where-Object { $_.Name -notlike "*sources*" } | Select-Object -First 1
    if ($jarFile) {
        Write-Host "   📦 JAR: $($jarFile.Name)" -ForegroundColor Green
    }
    
    # Desplegar a Azure
    Write-Host "3. 🌐 Desplegando a ed-ora..." -ForegroundColor Yellow
    mvn azure-webapp:deploy
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "🎉 ¡Despliegue exitoso!" -ForegroundColor Green
        Write-Host "🔗 URL: https://ed-ora.azurewebsites.us" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "🧪 Prueba la aplicación:" -ForegroundColor Yellow
        Write-Host "   • Web: https://ed-ora.azurewebsites.us" -ForegroundColor White
        Write-Host "   • Hello API: https://ed-ora.azurewebsites.us/api/hello" -ForegroundColor White
        Write-Host "   • Health: https://ed-ora.azurewebsites.us/actuator/health" -ForegroundColor White
    } else {
        Write-Host "   ❌ Error en el despliegue" -ForegroundColor Red
    }
} else {
    Write-Host "   ❌ Error en la compilación" -ForegroundColor Red
}