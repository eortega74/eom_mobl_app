# EOM QA - Quick Azure Deployment Script
# Simple deployment script for Azure Government Cloud

param(
    [string]$AppName = "eom-qa-app",
    [string]$ResourceGroup = "eom-qa-rg",
    [string]$Location = "USGov Virginia"
)

Write-Host "[DEPLOY] EOM QA Quick Azure Deployment" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# Step 1: Build application
Write-Host "`n[BUILD] Building application..." -ForegroundColor Cyan
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Build failed" -ForegroundColor Red
    exit 1
}

# Step 2: Check Azure login
Write-Host "`n[AUTH] Checking Azure authentication..." -ForegroundColor Cyan
az account show > $null 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "[WARNING] Not logged in. Starting Azure login..." -ForegroundColor Yellow
    az login --use-device-code
}

# Step 3: Set Government Cloud
Write-Host "`n[CLOUD] Setting Azure Government Cloud..." -ForegroundColor Cyan
az cloud set --name AzureUSGovernment

# Step 4: Quick deployment
Write-Host "`n[DEPLOY] Deploying to Azure..." -ForegroundColor Cyan

# Create resource group
az group create --name $ResourceGroup --location $Location

# Create and deploy app
az webapp up `
    --name $AppName `
    --resource-group $ResourceGroup `
    --location $Location `
    --runtime "JAVA:17-java17" `
    --sku B1

$AppUrl = "https://$AppName.azurewebsites.us"

Write-Host "`n[SUCCESS] Deployment completed!" -ForegroundColor Green
Write-Host "[URL] Application URL: $AppUrl" -ForegroundColor Green
Write-Host "[HEALTH] Health Check: $AppUrl/actuator/health" -ForegroundColor Green