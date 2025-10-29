# Quick Create EOM QA App Service
# Simple script to create the App Service for EOM QA

Write-Host "🚀 EOM QA App Service Creator" -ForegroundColor Green

# Ask for app name
$AppName = Read-Host "Enter the App Service name (e.g., eom-qa-prod, ate-eom-qa)"

if ([string]::IsNullOrWhiteSpace($AppName)) {
    Write-Host "❌ App name is required!" -ForegroundColor Red
    exit 1
}

# Validate app name (Azure naming rules)
if ($AppName -notmatch '^[a-zA-Z0-9\-]{2,60}$') {
    Write-Host "❌ Invalid app name. Use only letters, numbers, and hyphens (2-60 chars)" -ForegroundColor Red
    exit 1
}

Write-Host "`n📋 Configuration:" -ForegroundColor Cyan
Write-Host "   • App Name: $AppName" -ForegroundColor White
Write-Host "   • Java Version: 17" -ForegroundColor White
Write-Host "   • Cloud: Azure Government" -ForegroundColor White
Write-Host "   • URL: https://$AppName.azurewebsites.us" -ForegroundColor White

$confirm = Read-Host "`nCreate this App Service? (y/N)"

if ($confirm -ne 'y' -and $confirm -ne 'Y') {
    Write-Host "❌ Operation cancelled" -ForegroundColor Yellow
    exit 0
}

# Execute creation script
Write-Host "`n🚀 Creating App Service..." -ForegroundColor Green
.\create-azure-appservice.ps1 -AppName $AppName

Write-Host "`n✅ Done! Use this app name in your pipeline: $AppName" -ForegroundColor Green