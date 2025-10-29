# Create Azure App Service - Java 17
# Script to create Azure App Service for EOM QA application with Java 17

param(
    [Parameter(Mandatory=$true)]
    [string]$AppName,
    
    [string]$ResourceGroup = "eom-qa-rg",
    [string]$Location = "USGov Virginia",
    [string]$PlanName = "eom-qa-plan",
    [string]$Sku = "B1"
)

Write-Host "🚀 Creating Azure App Service with Java 17" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green

# Step 1: Set Azure Government Cloud
Write-Host "`n☁️ Setting Azure Government Cloud..." -ForegroundColor Cyan
az cloud set --name AzureUSGovernment

# Step 2: Create Resource Group if it doesn't exist
Write-Host "`n📦 Creating Resource Group: $ResourceGroup..." -ForegroundColor Cyan
az group create --name $ResourceGroup --location $Location

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to create resource group" -ForegroundColor Red
    exit 1
}

# Step 3: Create App Service Plan
Write-Host "`n🏗️ Creating App Service Plan: $PlanName..." -ForegroundColor Cyan
$planExists = az appservice plan show --name $PlanName --resource-group $ResourceGroup 2>$null

if (-not $planExists) {
    az appservice plan create `
        --name $PlanName `
        --resource-group $ResourceGroup `
        --location $Location `
        --sku $Sku `
        --is-linux
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Failed to create App Service Plan" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ App Service Plan created successfully" -ForegroundColor Green
} else {
    Write-Host "✅ App Service Plan already exists" -ForegroundColor Yellow
}

# Step 4: Create App Service with Java 17
Write-Host "`n🚀 Creating App Service: $AppName..." -ForegroundColor Cyan
$appExists = az webapp show --name $AppName --resource-group $ResourceGroup 2>$null

if (-not $appExists) {
    az webapp create `
        --name $AppName `
        --resource-group $ResourceGroup `
        --plan $PlanName `
        --runtime "JAVA:17-java17"
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Failed to create App Service" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ App Service created successfully" -ForegroundColor Green
} else {
    Write-Host "✅ App Service already exists" -ForegroundColor Yellow
}

# Step 5: Configure App Service for Java 17
Write-Host "`n⚙️ Configuring Java 17 settings..." -ForegroundColor Cyan

# Set Java configuration
az webapp config set `
    --name $AppName `
    --resource-group $ResourceGroup `
    --java-version "17" `
    --java-container "JAVA" `
    --java-container-version "SE"

# Configure app settings for Spring Boot
az webapp config appsettings set `
    --name $AppName `
    --resource-group $ResourceGroup `
    --settings `
        SPRING_PROFILES_ACTIVE="prod" `
        SERVER_PORT="8080" `
        JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -Xmx1024m" `
        WEBSITE_TIME_ZONE="Eastern Standard Time" `
        WEBSITE_JAVA_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8"

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to configure App Service" -ForegroundColor Red
    exit 1
}

# Step 6: Get App Service Information
Write-Host "`n📊 App Service Information:" -ForegroundColor Cyan
$appUrl = "https://$AppName.azurewebsites.us"

Write-Host "✅ SUCCESS! App Service created and configured" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green
Write-Host "📋 App Service Details:" -ForegroundColor White
Write-Host "   • Name: $AppName" -ForegroundColor White
Write-Host "   • Resource Group: $ResourceGroup" -ForegroundColor White
Write-Host "   • Location: $Location" -ForegroundColor White
Write-Host "   • Java Version: 17" -ForegroundColor White
Write-Host "   • SKU: $Sku" -ForegroundColor White
Write-Host "   • URL: $appUrl" -ForegroundColor White
Write-Host "`n🌐 Application URLs:" -ForegroundColor Cyan
Write-Host "   • Main App: $appUrl" -ForegroundColor White
Write-Host "   • Health Check: $appUrl/actuator/health" -ForegroundColor White

# Step 7: Generate Publish Profile
Write-Host "`n📝 Generating Publish Profile..." -ForegroundColor Cyan
$publishProfile = az webapp deployment list-publishing-profiles --name $AppName --resource-group $ResourceGroup --xml

if ($publishProfile) {
    Write-Host "✅ Publish profile generated successfully" -ForegroundColor Green
    Write-Host "`n⚠️ IMPORTANT: Configure this publish profile in BeyondTrust as:" -ForegroundColor Yellow
    Write-Host "   Environment Variable: EOM_QA_PUBLISH_PROFILE" -ForegroundColor Yellow
    
    # Save publish profile to file
    $publishProfile | Out-File -FilePath "publish-profile-$AppName.xml" -Encoding UTF8
    Write-Host "   📄 Profile saved to: publish-profile-$AppName.xml" -ForegroundColor White
} else {
    Write-Host "⚠️ Could not generate publish profile" -ForegroundColor Yellow
}

Write-Host "`n🎯 Next Steps:" -ForegroundColor Cyan
Write-Host "1. Configure EOM_QA_PUBLISH_PROFILE in BeyondTrust" -ForegroundColor White
Write-Host "2. Update pipeline to use app name: $AppName" -ForegroundColor White
Write-Host "3. Test deployment via GitHub Actions pipeline" -ForegroundColor White

Write-Host "`n🚀 App Service is ready for deployment!" -ForegroundColor Green