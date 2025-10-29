# EOM QA - Deploy to Azure Script
# Este script despliega la aplicación directamente a Azure App Service

param(
    [Parameter(Mandatory=$false)]
    [string]$Environment = "dev",
    
    [Parameter(Mandatory=$false)]
    [string]$ResourceGroup = "eom-qa-rg",
    
    [Parameter(Mandatory=$false)]
    [string]$SubscriptionId
)

$WebAppName = switch ($Environment.ToLower()) {
    "dev"  { "eom-qa-dev" }
    "qa"   { "eom-qa-qa" }
    "prod" { "eom-qa-prod" }
    default { "eom-qa-dev" }
}

Write-Host "🚀 EOM QA - Desplegando a Azure App Service" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host "   Environment: $Environment" -ForegroundColor Cyan
Write-Host "   Web App: $WebAppName" -ForegroundColor Cyan
Write-Host "   Resource Group: $ResourceGroup" -ForegroundColor Cyan
Write-Host ""

# Verificar que Maven esté disponible
Write-Host "1. ✅ Verificando Maven..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Maven disponible" -ForegroundColor Green
    } else {
        throw "Maven no encontrado"
    }
} catch {
    Write-Host "   ❌ Error: Maven no está disponible. Instalar Maven primero." -ForegroundColor Red
    exit 1
}

# Compilar la aplicación
Write-Host "2. 🔨 Compilando aplicación..." -ForegroundColor Yellow
try {
    mvn clean package -DskipTests
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Compilación exitosa" -ForegroundColor Green
    } else {
        throw "Error en compilación"
    }
} catch {
    Write-Host "   ❌ Error en la compilación: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Verificar que el JAR existe
$jarFile = Get-ChildItem -Path "target" -Filter "*.jar" | Where-Object { $_.Name -notlike "*sources*" -and $_.Name -notlike "*javadoc*" } | Select-Object -First 1

if ($jarFile) {
    Write-Host "   📦 JAR encontrado: $($jarFile.Name)" -ForegroundColor Green
} else {
    Write-Host "   ❌ No se encontró archivo JAR en target/" -ForegroundColor Red
    exit 1
}

# Configurar Azure CLI (si se proporciona subscription)
if ($SubscriptionId) {
    Write-Host "3. 🔐 Configurando Azure CLI..." -ForegroundColor Yellow
    try {
        az account set --subscription $SubscriptionId
        Write-Host "   ✅ Subscription configurada: $SubscriptionId" -ForegroundColor Green
    } catch {
        Write-Host "   ❌ Error configurando subscription: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "   ℹ️  Ejecute 'az login' manualmente si es necesario" -ForegroundColor Blue
    }
}

# Desplegar a Azure
Write-Host "4. 🌐 Desplegando a Azure App Service..." -ForegroundColor Yellow
try {
    $deployCommand = "mvn azure-webapp:deploy -Dazure.resourceGroup=$ResourceGroup -Dazure.appName=$WebAppName"
    
    if ($SubscriptionId) {
        $deployCommand += " -Dazure.subscription.id=$SubscriptionId"
    }
    
    Write-Host "   Ejecutando: $deployCommand" -ForegroundColor Gray
    Invoke-Expression $deployCommand
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Despliegue exitoso!" -ForegroundColor Green
        Write-Host ""
        Write-Host "🎉 ¡Aplicación desplegada correctamente!" -ForegroundColor Green
        Write-Host "🔗 URL: https://$WebAppName.azurewebsites.net" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "🧪 Prueba la aplicación:" -ForegroundColor Yellow
        Write-Host "   • Web: https://$WebAppName.azurewebsites.net" -ForegroundColor White
        Write-Host "   • API: https://$WebAppName.azurewebsites.net/api/hello" -ForegroundColor White
        Write-Host "   • Health: https://$WebAppName.azurewebsites.net/actuator/health" -ForegroundColor White
    } else {
        throw "Error en el despliegue"
    }
} catch {
    Write-Host "   ❌ Error en el despliegue: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "🔧 Soluciones posibles:" -ForegroundColor Yellow
    Write-Host "   1. Verificar que az login esté ejecutado" -ForegroundColor White
    Write-Host "   2. Verificar que el Web App '$WebAppName' exista" -ForegroundColor White
    Write-Host "   3. Ejecutar azure-setup.ps1 primero para crear recursos" -ForegroundColor White
    exit 1
}