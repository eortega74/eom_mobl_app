# EOM QA - Azure App Service Setup Script
# Este script configura los recursos de Azure necesarios para el despliegue

param(
    [Parameter(Mandatory=$true)]
    [string]$SubscriptionId,
    
    [Parameter(Mandatory=$false)]
    [string]$ResourceGroupName = "eom-qa-rg",
    
    [Parameter(Mandatory=$false)]
    [string]$Location = "USGov Virginia",
    
    [Parameter(Mandatory=$false)]
    [string]$AppServicePlan = "eom-qa-plan",
    
    [Parameter(Mandatory=$false)]
    [string]$WebAppDev = "eom-qa-dev",
    
    [Parameter(Mandatory=$false)]
    [string]$WebAppQa = "eom-qa-qa",
    
    [Parameter(Mandatory=$false)]
    [string]$WebAppProd = "eom-qa-prod"
)

Write-Host "🚀 EOM QA - Configurando Azure App Service" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

# Login a Azure
Write-Host "1. 🔐 Iniciando sesión en Azure..." -ForegroundColor Yellow
try {
    az login
    az account set --subscription $SubscriptionId
    Write-Host "   ✅ Sesión iniciada correctamente" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Error en el login: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Crear Resource Group
Write-Host "2. 📦 Creando Resource Group..." -ForegroundColor Yellow
try {
    az group create --name $ResourceGroupName --location $Location
    Write-Host "   ✅ Resource Group '$ResourceGroupName' creado" -ForegroundColor Green
} catch {
    Write-Host "   ⚠️  Resource Group ya existe o error: $($_.Exception.Message)" -ForegroundColor Orange
}

# Crear App Service Plan (Free Tier para desarrollo)
Write-Host "3. 🏗️  Creando App Service Plan..." -ForegroundColor Yellow
try {
    az appservice plan create `
        --name $AppServicePlan `
        --resource-group $ResourceGroupName `
        --location $Location `
        --sku F1 `
        --is-linux
    Write-Host "   ✅ App Service Plan '$AppServicePlan' creado (Free Tier)" -ForegroundColor Green
} catch {
    Write-Host "   ⚠️  App Service Plan ya existe o error: $($_.Exception.Message)" -ForegroundColor Orange
}

# Crear Web App para DEV
Write-Host "4. 🌐 Creando Web App DEV..." -ForegroundColor Yellow
try {
    az webapp create `
        --name $WebAppDev `
        --resource-group $ResourceGroupName `
        --plan $AppServicePlan `
        --runtime "JAVA:17-java17"
    
    # Configurar settings para DEV
    az webapp config appsettings set `
        --name $WebAppDev `
        --resource-group $ResourceGroupName `
        --settings SPRING_PROFILES_ACTIVE=azure JAVA_OPTS="-Dserver.port=80"
        
    Write-Host "   ✅ Web App DEV '$WebAppDev' creado" -ForegroundColor Green
    Write-Host "   🔗 URL: https://$WebAppDev.azurewebsites.net" -ForegroundColor Cyan
} catch {
    Write-Host "   ❌ Error creando Web App DEV: $($_.Exception.Message)" -ForegroundColor Red
}

# Crear Web App para QA
Write-Host "5. 🧪 Creando Web App QA..." -ForegroundColor Yellow
try {
    az webapp create `
        --name $WebAppQa `
        --resource-group $ResourceGroupName `
        --plan $AppServicePlan `
        --runtime "JAVA:17-java17"
    
    # Configurar settings para QA
    az webapp config appsettings set `
        --name $WebAppQa `
        --resource-group $ResourceGroupName `
        --settings SPRING_PROFILES_ACTIVE=azure JAVA_OPTS="-Dserver.port=80"
        
    Write-Host "   ✅ Web App QA '$WebAppQa' creado" -ForegroundColor Green
    Write-Host "   🔗 URL: https://$WebAppQa.azurewebsites.net" -ForegroundColor Cyan
} catch {
    Write-Host "   ❌ Error creando Web App QA: $($_.Exception.Message)" -ForegroundColor Red
}

# Crear Web App para PROD
Write-Host "6. 🏭 Creando Web App PROD..." -ForegroundColor Yellow
try {
    az webapp create `
        --name $WebAppProd `
        --resource-group $ResourceGroupName `
        --plan $AppServicePlan `
        --runtime "JAVA:17-java17"
    
    # Configurar settings para PROD
    az webapp config appsettings set `
        --name $WebAppProd `
        --resource-group $ResourceGroupName `
        --settings SPRING_PROFILES_ACTIVE=azure JAVA_OPTS="-Dserver.port=80"
        
    Write-Host "   ✅ Web App PROD '$WebAppProd' creado" -ForegroundColor Green
    Write-Host "   🔗 URL: https://$WebAppProd.azurewebsites.net" -ForegroundColor Cyan
} catch {
    Write-Host "   ❌ Error creando Web App PROD: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "🎉 ¡Configuración de Azure completada!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Resumen de recursos creados:" -ForegroundColor Cyan
Write-Host "   • Resource Group: $ResourceGroupName" -ForegroundColor White
Write-Host "   • App Service Plan: $AppServicePlan (Free Tier)" -ForegroundColor White
Write-Host "   • Web App DEV: https://$WebAppDev.azurewebsites.net" -ForegroundColor White
Write-Host "   • Web App QA: https://$WebAppQa.azurewebsites.net" -ForegroundColor White
Write-Host "   • Web App PROD: https://$WebAppProd.azurewebsites.net" -ForegroundColor White
Write-Host ""
Write-Host "🚀 Próximos pasos:" -ForegroundColor Yellow
Write-Host "   1. Configurar secrets en GitHub Actions:" -ForegroundColor White
Write-Host "      - AZURE_SUBSCRIPTION_ID: $SubscriptionId" -ForegroundColor Gray
Write-Host "      - AZURE_CLIENT_ID: (del service principal)" -ForegroundColor Gray
Write-Host "      - AZURE_CLIENT_SECRET: (del service principal)" -ForegroundColor Gray
Write-Host "      - AZURE_TENANT_ID: (del tenant)" -ForegroundColor Gray
Write-Host "   2. Ejecutar: mvn clean package" -ForegroundColor White
Write-Host "   3. Desplegar: mvn azure-webapp:deploy" -ForegroundColor White