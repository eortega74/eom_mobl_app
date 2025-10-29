# EOM QA Azure Government Cloud Deployment
# Complete setup and deployment guide

## 🎯 Quick Start

1. **Execute Quick Deployment:**
   ```powershell
   .\quick-deploy-azure.ps1
   ```

2. **Custom Deployment:**
   ```powershell
   .\quick-deploy-azure.ps1 -AppName "my-eom-app" -ResourceGroup "my-rg"
   ```

## 📋 Prerequisites

- **Maven**: For building the Java application
- **PowerShell**: Windows PowerShell 5.1 or later
- **Azure Account**: With Government Cloud access
- **Internet**: For downloading Azure CLI

## 🔧 Manual Setup (if needed)

### Step 1: Setup Azure CLI
```powershell
.\setup-azure-cli.ps1
```

### Step 2: Full Deployment
```powershell
.\deploy-azure-gov.ps1
```

## 🌐 Azure Government Cloud Specifics

- **Login Method**: Device code authentication (private browser compatible)
- **Cloud Environment**: AzureUSGovernment
- **App URL Format**: `https://your-app.azurewebsites.us`
- **Location**: USGov Virginia, USGov Texas, USGov Arizona

## 📊 Application Endpoints

After deployment, your application will be available at:

- **Main App**: `https://your-app.azurewebsites.us`
- **Health Check**: `https://your-app.azurewebsites.us/actuator/health`
- **API Endpoints**: `https://your-app.azurewebsites.us/api/*`

## 🧪 QA Testing

The application includes Selenium QA capabilities:

1. **Local Testing**: `docker-compose up`
2. **Pipeline Testing**: Triggered via GitHub Actions
3. **Azure Testing**: Health endpoints available

## 🔍 Troubleshooting

### Common Issues:

1. **Build Fails**: 
   - Check Java 17 installation: `java -version`
   - Verify Maven setup: `mvn -version`

2. **Azure Login Issues**:
   - Use device code: `az login --use-device-code`
   - Check cloud setting: `az cloud show`

3. **Deployment Fails**:
   - Check resource limits in Government Cloud
   - Verify naming conventions (lowercase, no special chars)

### Support Commands:

```powershell
# Check Azure status
az account show

# List available locations
az account list-locations --query "[?metadata.regionCategory=='Other']"

# Check app status
az webapp show --name your-app --resource-group your-rg
```

## 📁 Project Structure

```
eom-qa-app/
├── pom.xml                 # Maven configuration (Java 17)
├── docker-compose.yaml     # Local development with Selenium
├── .github/workflows/      # CI/CD pipeline
├── quick-deploy-azure.ps1  # Quick deployment script
├── deploy-azure-gov.ps1    # Complete deployment script
└── src/main/              # Java application code
```

## 🎯 Next Steps

1. Execute `.\quick-deploy-azure.ps1`
2. Wait for Azure deployment completion
3. Test application at provided URL
4. Configure GitHub Actions for CI/CD

---

**Note**: This setup is optimized for Azure Government Cloud with private browser compatibility.