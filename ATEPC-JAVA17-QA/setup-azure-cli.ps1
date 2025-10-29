# Azure CLI Setup for Government Cloud - Quick Configuration Script
# This script configures Azure CLI for Azure Government Cloud environment

Write-Host "=============================================" -ForegroundColor Magenta
Write-Host "   AZURE CLI GOVERNMENT CLOUD SETUP        " -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Magenta

$ErrorActionPreference = "Continue"

# Step 1: Check Azure CLI Installation
Write-Host "`n🔍 Checking Azure CLI Installation..." -ForegroundColor Cyan

try {
    $azVersion = az version --output json | ConvertFrom-Json
    Write-Host "   ✅ Azure CLI Version: $($azVersion.'azure-cli')" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Azure CLI not found!" -ForegroundColor Red
    Write-Host "   💡 Installing Azure CLI..." -ForegroundColor Yellow
    
    # Download and install Azure CLI
    Write-Host "   Downloading Azure CLI installer..." -ForegroundColor Yellow
    $installerUrl = "https://aka.ms/installazurecliwindows"
    $installerPath = "$env:TEMP\AzureCLI.msi"
    
    try {
        Invoke-WebRequest -Uri $installerUrl -OutFile $installerPath -UseBasicParsing
        Write-Host "   Installing Azure CLI (this may take a few minutes)..." -ForegroundColor Yellow
        Start-Process msiexec.exe -Wait -ArgumentList "/I $installerPath /quiet"
        Remove-Item $installerPath -ErrorAction SilentlyContinue
        
        # Refresh PATH
        $env:PATH = [System.Environment]::GetEnvironmentVariable("PATH", "Machine") + ";" + [System.Environment]::GetEnvironmentVariable("PATH", "User")
        
        Write-Host "   ✅ Azure CLI installed successfully!" -ForegroundColor Green
        Write-Host "   ⚠️ Please restart PowerShell and run this script again" -ForegroundColor Yellow
        exit 0
    } catch {
        Write-Host "   ❌ Failed to install Azure CLI" -ForegroundColor Red
        Write-Host "   💡 Please install manually: https://docs.microsoft.com/cli/azure/install-azure-cli-windows" -ForegroundColor Yellow
        exit 1
    }
}

# Step 2: Configure Azure Government Cloud
Write-Host "`n☁️ Configuring Azure Government Cloud..." -ForegroundColor Cyan

try {
    # List available clouds
    Write-Host "   Available Azure clouds:" -ForegroundColor Yellow
    az cloud list --query "[].{Name:name, IsActive:isActive}" --output table
    
    # Set Government Cloud
    Write-Host "   Setting Azure Government Cloud as active..." -ForegroundColor Yellow
    az cloud set --name AzureUSGovernment
    
    # Verify cloud setting
    $currentCloud = az cloud show --query name --output tsv
    Write-Host "   ✅ Active Azure Cloud: $currentCloud" -ForegroundColor Green
    
    # Show Government Cloud endpoints
    Write-Host "   🌐 Azure Government Cloud Endpoints:" -ForegroundColor Yellow
    az cloud show --query "{Portal:endpoints.portal, Management:endpoints.management, ActiveDirectory:endpoints.activeDirectory}" --output table
    
} catch {
    Write-Host "   ❌ Failed to configure Azure Government Cloud" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 3: Login Instructions
Write-Host "`n🔐 Azure Government Cloud Login..." -ForegroundColor Cyan

Write-Host "   📋 Login Options for Government Cloud:" -ForegroundColor Yellow
Write-Host ""
Write-Host "   Option 1: Device Code Login (Recommended for Private Browser)" -ForegroundColor White
Write-Host "   Command: az login --use-device-code" -ForegroundColor Gray
Write-Host "   - Best for government environments" -ForegroundColor Gray
Write-Host "   - Works with private/incognito browser" -ForegroundColor Gray
Write-Host "   - Supports MFA and conditional access" -ForegroundColor Gray
Write-Host ""
Write-Host "   Option 2: Service Principal (For Automation)" -ForegroundColor White
Write-Host "   Command: az login --service-principal -u <client-id> -p <client-secret> --tenant <tenant-id>" -ForegroundColor Gray
Write-Host "   - For CI/CD pipelines" -ForegroundColor Gray
Write-Host "   - Requires pre-created service principal" -ForegroundColor Gray
Write-Host ""
Write-Host "   Option 3: Managed Identity (Azure VMs only)" -ForegroundColor White
Write-Host "   Command: az login --identity" -ForegroundColor Gray
Write-Host "   - For Azure VMs with managed identity" -ForegroundColor Gray
Write-Host ""

$loginChoice = Read-Host "   Would you like to login now? (y/n)"

if ($loginChoice -eq "y" -or $loginChoice -eq "Y") {
    Write-Host "`n   Starting device code login..." -ForegroundColor Yellow
    Write-Host "   💡 Copy the device code and open the URL in your private browser" -ForegroundColor Yellow
    
    az login --use-device-code
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Successfully logged into Azure Government!" -ForegroundColor Green
        
        # Show account information
        Write-Host "   📊 Account Information:" -ForegroundColor Yellow
        az account show --query "{Name:name, SubscriptionId:id, TenantId:tenantId, CloudName:environmentName}" --output table
        
        # List all subscriptions
        Write-Host "   📋 Available Subscriptions:" -ForegroundColor Yellow
        az account list --query "[].{Name:name, SubscriptionId:id, State:state, IsDefault:isDefault}" --output table
        
    } else {
        Write-Host "   ❌ Login failed" -ForegroundColor Red
    }
} else {
    Write-Host "   ⏭️ Skipping login - you can login later with: az login --use-device-code" -ForegroundColor Yellow
}

# Step 4: Show Useful Commands
Write-Host "`n🛠️ Useful Azure Government Commands:" -ForegroundColor Cyan

$commands = @"
# Basic Commands
az account show                          # Show current account
az account list                          # List all subscriptions
az account set --subscription <id>       # Set active subscription

# Resource Management
az group list                            # List resource groups
az group create --name <name> --location "USGov Virginia"  # Create resource group

# App Service Commands
az webapp list                           # List web apps
az webapp create --name <name> --resource-group <rg> --plan <plan> --runtime "JAVA:17-java17"
az webapp deploy --name <name> --resource-group <rg> --src-path <jar-file> --type jar

# Monitoring Commands
az webapp log tail --name <name> --resource-group <rg>     # View live logs
az webapp show --name <name> --resource-group <rg>         # Show app details
az webapp restart --name <name> --resource-group <rg>      # Restart app

# Configuration Commands
az webapp config show --name <name> --resource-group <rg>  # Show configuration
az webapp config appsettings set --name <name> --resource-group <rg> --settings KEY=VALUE
"@

Write-Host $commands -ForegroundColor Gray

# Step 5: Create Quick Reference File
Write-Host "`n📄 Creating Quick Reference Guide..." -ForegroundColor Cyan

$quickReference = @"
# Azure Government Cloud - Quick Reference

## 🌐 Environment Information
- **Cloud Name**: AzureUSGovernment
- **Portal**: https://portal.azure.us
- **Management Endpoint**: https://management.usgovcloudapi.net/
- **Active Directory**: https://login.microsoftonline.us

## 🔐 Authentication
```bash
# Device code login (recommended for private browser)
az login --use-device-code

# Service principal login
az login --service-principal -u <client-id> -p <client-secret> --tenant <tenant-id>
```

## 📍 Government Cloud Regions
- USGov Virginia (usgovvirginia)
- USGov Iowa (usgoviowa)
- USGov Arizona (usgovarizona)
- USGov Texas (usgovtexas)

## 🚀 EOM QA Deployment Commands
```bash
# Set active subscription
az account set --subscription "<subscription-id>"

# Create resource group
az group create --name "eom-qa-rg" --location "USGov Virginia"

# Create App Service Plan
az appservice plan create --name "eom-qa-plan" --resource-group "eom-qa-rg" --location "USGov Virginia" --sku B1 --is-linux

# Create Web App
az webapp create --name "eom-qa-app" --resource-group "eom-qa-rg" --plan "eom-qa-plan" --runtime "JAVA:17-java17"

# Deploy JAR file
az webapp deploy --name "eom-qa-app" --resource-group "eom-qa-rg" --src-path "target/eom-qa-simple-1.0.0-SNAPSHOT.jar" --type jar
```

## 🔧 Configuration Examples
```bash
# Set Spring Boot configuration
az webapp config appsettings set --name "eom-qa-app" --resource-group "eom-qa-rg" --settings SPRING_PROFILES_ACTIVE=prod SERVER_PORT=8080

# View application logs
az webapp log tail --name "eom-qa-app" --resource-group "eom-qa-rg"

# Restart application
az webapp restart --name "eom-qa-app" --resource-group "eom-qa-rg"
```

Generated on: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
"@

$quickReference | Out-File -FilePath "azure-gov-quick-reference.md" -Encoding UTF8
Write-Host "   📋 Quick reference saved: azure-gov-quick-reference.md" -ForegroundColor Green

# Final Summary
Write-Host "`n" + "="*50 -ForegroundColor Magenta
Write-Host "    AZURE CLI SETUP COMPLETED    " -ForegroundColor Green
Write-Host "="*50 -ForegroundColor Magenta

Write-Host "✅ Azure CLI configured for Government Cloud" -ForegroundColor Green
Write-Host "📄 Quick reference guide created" -ForegroundColor Green
Write-Host ""
Write-Host "🔧 Next Steps:" -ForegroundColor Yellow
Write-Host "1. Run: .\deploy-azure-gov.ps1 -ResourceGroup 'your-rg' -AppName 'your-app' -SubscriptionId 'your-sub-id'" -ForegroundColor White
Write-Host "2. Or use individual az commands from the reference guide" -ForegroundColor White
Write-Host "3. Test your deployment at: https://your-app.azurewebsites.us" -ForegroundColor White
Write-Host ""