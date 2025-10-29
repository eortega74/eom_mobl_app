# Azure Government Cloud Deployment Script for EOM QA
# This script deploys the Spring Boot application to Azure App Service in Government Cloud

param(
    [Parameter(Mandatory=$true)]
    [string]$ResourceGroup,
    
    [Parameter(Mandatory=$true)]
    [string]$AppName,
    
    [Parameter(Mandatory=$true)]
    [string]$SubscriptionId,
    
    [string]$Location = "USGov Virginia",
    [string]$Environment = "prod"
)

Write-Host "=============================================" -ForegroundColor Magenta
Write-Host "   AZURE GOVERNMENT CLOUD DEPLOYMENT        " -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Magenta

$StartTime = Get-Date
$ErrorActionPreference = "Stop"

# Configuration
$JarPath = "target\eom-qa-simple-1.0.0-SNAPSHOT.jar"
$AppServicePlan = "$AppName-plan"

Write-Host "🔧 Deployment Configuration:" -ForegroundColor Cyan
Write-Host "   Resource Group: $ResourceGroup" -ForegroundColor Yellow
Write-Host "   App Service: $AppName" -ForegroundColor Yellow
Write-Host "   Subscription: $SubscriptionId" -ForegroundColor Yellow
Write-Host "   Location: $Location" -ForegroundColor Yellow
Write-Host "   Environment: $Environment" -ForegroundColor Yellow

# Step 1: Verify Prerequisites
Write-Host "`n🔍 Verifying Prerequisites..." -ForegroundColor Cyan

# Check if JAR file exists
if (-not (Test-Path $JarPath)) {
    Write-Host "   ❌ JAR file not found: $JarPath" -ForegroundColor Red
    Write-Host "   💡 Please run: mvn clean package -DskipTests" -ForegroundColor Yellow
    exit 1
}

$JarSize = (Get-Item $JarPath).Length / 1MB
Write-Host "   ✅ JAR file found: $([math]::Round($JarSize, 2)) MB" -ForegroundColor Green

# Check Azure CLI installation
try {
    $azVersion = az --version 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Azure CLI not found"
    }
    Write-Host "   ✅ Azure CLI installed and available" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Azure CLI not found or not installed" -ForegroundColor Red
    Write-Host "   💡 Please install Azure CLI: https://docs.microsoft.com/cli/azure/install-azure-cli" -ForegroundColor Yellow
    exit 1
}

# Step 2: Configure Azure Government Cloud
Write-Host "`n☁️ Configuring Azure Government Cloud..." -ForegroundColor Cyan

try {
    Write-Host "   Setting Azure Government Cloud environment..." -ForegroundColor Yellow
    az cloud set --name AzureUSGovernment
    
    Write-Host "   Current Azure environment:" -ForegroundColor Yellow
    az cloud show --query name -o tsv
    
    Write-Host "   ✅ Azure Government Cloud configured successfully" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Failed to configure Azure Government Cloud" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 3: Azure Login Check
Write-Host "`n🔐 Checking Azure Authentication..." -ForegroundColor Cyan

try {
    $currentAccount = az account show 2>$null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "   ⚠️ Not logged into Azure. Starting login process..." -ForegroundColor Yellow
        Write-Host "   💡 Use device code login for government cloud in private browser" -ForegroundColor Yellow
        
        # Use device code flow for government cloud
        az login --use-device-code --cloud AzureUSGovernment
        
        if ($LASTEXITCODE -ne 0) {
            Write-Host "   ❌ Azure login failed" -ForegroundColor Red
            exit 1
        }
    }
    
    Write-Host "   ✅ Azure authentication verified" -ForegroundColor Green
    
    # Set target subscription
    Write-Host "   Setting target subscription: $SubscriptionId" -ForegroundColor Yellow
    az account set --subscription $SubscriptionId
    
    $currentSub = az account show --query name -o tsv
    Write-Host "   ✅ Active subscription: $currentSub" -ForegroundColor Green
    
} catch {
    Write-Host "   ❌ Azure authentication failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 4: Create or Verify Resource Group
Write-Host "`n🏗️ Managing Azure Resources..." -ForegroundColor Cyan

try {
    Write-Host "   Checking if resource group exists: $ResourceGroup" -ForegroundColor Yellow
    $rgExists = az group exists --name $ResourceGroup
    
    if ($rgExists -eq "false") {
        Write-Host "   Creating resource group: $ResourceGroup" -ForegroundColor Yellow
        az group create --name $ResourceGroup --location $Location
        Write-Host "   ✅ Resource group created successfully" -ForegroundColor Green
    } else {
        Write-Host "   ✅ Resource group already exists" -ForegroundColor Green
    }
} catch {
    Write-Host "   ❌ Failed to manage resource group" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 5: Create or Verify App Service Plan
Write-Host "`n📋 Managing App Service Plan..." -ForegroundColor Cyan

try {
    Write-Host "   Checking App Service Plan: $AppServicePlan" -ForegroundColor Yellow
    $planExists = az appservice plan show --name $AppServicePlan --resource-group $ResourceGroup 2>$null
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "   Creating App Service Plan: $AppServicePlan" -ForegroundColor Yellow
        az appservice plan create `
            --name $AppServicePlan `
            --resource-group $ResourceGroup `
            --location $Location `
            --sku B1 `
            --is-linux
        Write-Host "   ✅ App Service Plan created successfully" -ForegroundColor Green
    } else {
        Write-Host "   ✅ App Service Plan already exists" -ForegroundColor Green
    }
} catch {
    Write-Host "   ❌ Failed to manage App Service Plan" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 6: Create or Update Web App
Write-Host "`n🌐 Managing Web Application..." -ForegroundColor Cyan

try {
    Write-Host "   Checking Web App: $AppName" -ForegroundColor Yellow
    $appExists = az webapp show --name $AppName --resource-group $ResourceGroup 2>$null
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "   Creating Web App: $AppName" -ForegroundColor Yellow
        az webapp create `
            --name $AppName `
            --resource-group $ResourceGroup `
            --plan $AppServicePlan `
            --runtime "JAVA:17-java17"
        Write-Host "   ✅ Web App created successfully" -ForegroundColor Green
    } else {
        Write-Host "   ✅ Web App already exists" -ForegroundColor Green
    }
    
    # Configure Java runtime
    Write-Host "   Configuring Java 17 runtime..." -ForegroundColor Yellow
    az webapp config set `
        --name $AppName `
        --resource-group $ResourceGroup `
        --java-version "17" `
        --java-container "JAVA" `
        --java-container-version "SE"
    
    Write-Host "   ✅ Runtime configuration completed" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Failed to manage Web App" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 7: Configure Application Settings
Write-Host "`n⚙️ Configuring Application Settings..." -ForegroundColor Cyan

try {
    Write-Host "   Setting Spring Boot configuration..." -ForegroundColor Yellow
    az webapp config appsettings set `
        --name $AppName `
        --resource-group $ResourceGroup `
        --settings `
            SPRING_PROFILES_ACTIVE=$Environment `
            SERVER_PORT="8080" `
            JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -Xmx1024m" `
            WEBSITE_TIME_ZONE="Eastern Standard Time" `
            SCM_DO_BUILD_DURING_DEPLOYMENT="false"
    
    Write-Host "   ✅ Application settings configured" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Failed to configure application settings" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 8: Deploy Application
Write-Host "`n🚀 Deploying Application..." -ForegroundColor Cyan

try {
    Write-Host "   Uploading JAR file: $JarPath" -ForegroundColor Yellow
    az webapp deploy `
        --name $AppName `
        --resource-group $ResourceGroup `
        --src-path $JarPath `
        --type jar `
        --async false
    
    Write-Host "   ✅ Application deployed successfully" -ForegroundColor Green
    
    # Get application URL
    $AppUrl = "https://$AppName.azurewebsites.us"
    Write-Host "   🌐 Application URL: $AppUrl" -ForegroundColor Green
    
} catch {
    Write-Host "   ❌ Deployment failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 9: Verify Deployment
Write-Host "`n🔍 Verifying Deployment..." -ForegroundColor Cyan

try {
    $AppUrl = "https://$AppName.azurewebsites.us"
    $HealthUrl = "$AppUrl/actuator/health"
    
    Write-Host "   Testing application endpoints..." -ForegroundColor Yellow
    Write-Host "   Application URL: $AppUrl" -ForegroundColor Yellow
    Write-Host "   Health Check URL: $HealthUrl" -ForegroundColor Yellow
    
    # Wait for application to start
    Write-Host "   ⏳ Waiting for application to start (up to 5 minutes)..." -ForegroundColor Yellow
    
    for ($i = 1; $i -le 20; $i++) {
        try {
            $response = Invoke-WebRequest -Uri $HealthUrl -UseBasicParsing -TimeoutSec 10 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Write-Host "   ✅ Application is healthy and responding!" -ForegroundColor Green
                Write-Host "   📊 Health Status: $($response.Content)" -ForegroundColor Green
                break
            }
        } catch {
            if ($i -eq 20) {
                Write-Host "   ⚠️ Health check timeout - application may still be starting" -ForegroundColor Yellow
                Write-Host "   💡 Manual check: $HealthUrl" -ForegroundColor Yellow
            } else {
                Write-Host "   Attempt $i/20: Application starting..." -ForegroundColor Yellow
                Start-Sleep -Seconds 15
            }
        }
    }
    
} catch {
    Write-Host "   ⚠️ Verification completed with warnings" -ForegroundColor Yellow
    Write-Host "   💡 Manual verification recommended: $AppUrl" -ForegroundColor Yellow
}

# Step 10: Generate Deployment Summary
Write-Host "`n📊 Generating Deployment Summary..." -ForegroundColor Cyan

$EndTime = Get-Date
$Duration = $EndTime - $StartTime
$AppUrl = "https://$AppName.azurewebsites.us"

$DeploymentSummary = @"
# Azure Government Cloud Deployment Summary

## 🎯 Deployment Details
- **Application Name**: $AppName
- **Resource Group**: $ResourceGroup
- **Subscription**: $SubscriptionId
- **Location**: $Location
- **Environment**: $Environment

## 🌐 Application URLs
- **Main Application**: $AppUrl
- **Health Check**: $AppUrl/actuator/health
- **Info Endpoint**: $AppUrl/actuator/info

## ⚙️ Configuration
- **Java Version**: 17
- **Spring Profile**: $Environment
- **Memory Allocation**: 1024MB
- **Time Zone**: Eastern Standard Time

## 📊 Deployment Statistics
- **JAR Size**: $([math]::Round($JarSize, 2)) MB
- **Deployment Duration**: $($Duration.ToString('mm\:ss'))
- **Completion Time**: $($EndTime.ToString('yyyy-MM-dd HH:mm:ss'))

## 🔗 Useful Commands
```powershell
# View application logs
az webapp log tail --name $AppName --resource-group $ResourceGroup

# Restart application
az webapp restart --name $AppName --resource-group $ResourceGroup

# Scale application
az webapp up --name $AppName --resource-group $ResourceGroup --sku B2
```

Generated on: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
"@

$DeploymentSummary | Out-File -FilePath "azure-deployment-summary.md" -Encoding UTF8
Write-Host "   📄 Deployment summary saved: azure-deployment-summary.md" -ForegroundColor Green

# Final Summary
Write-Host "`n" + "="*50 -ForegroundColor Magenta
Write-Host "    AZURE DEPLOYMENT COMPLETED    " -ForegroundColor Green
Write-Host "="*50 -ForegroundColor Magenta

Write-Host "🎉 SUCCESS: EOM QA deployed to Azure Government!" -ForegroundColor Green
Write-Host "🌐 Application URL: $AppUrl" -ForegroundColor Green
Write-Host "⏱️ Total Deployment Time: $($Duration.ToString('mm\:ss'))" -ForegroundColor Cyan
Write-Host "📊 Deployment Report: azure-deployment-summary.md" -ForegroundColor Cyan
Write-Host ""

Write-Host "🔧 Next Steps:" -ForegroundColor Yellow
Write-Host "1. Test application: $AppUrl" -ForegroundColor White
Write-Host "2. Monitor health: $AppUrl/actuator/health" -ForegroundColor White
Write-Host "3. View logs: az webapp log tail --name $AppName --resource-group $ResourceGroup" -ForegroundColor White

exit 0