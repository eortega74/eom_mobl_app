# EOM QA - Simple Azure Setup Test
param(
    [string]$SubscriptionId = "4b73be29-7b7f-415c-8aa9-807a1b44bcc4"
)

Write-Host "🚀 EOM QA - Configuración Azure Government" -ForegroundColor Green

# Verificar login
Write-Host "1. Verificando Azure CLI..." -ForegroundColor Yellow
az account show --output table

# Crear Resource Group
Write-Host "2. Creando Resource Group..." -ForegroundColor Yellow
az group create --name "eom-qa-rg" --location "USGov Virginia"

# Crear App Service Plan
Write-Host "3. Creando App Service Plan..." -ForegroundColor Yellow
az appservice plan create --name "eom-qa-plan" --resource-group "eom-qa-rg" --location "USGov Virginia" --sku F1 --is-linux

Write-Host "✅ Setup básico completado!" -ForegroundColor Green