# EOM QA Pipeline - Azure Deployment Configuration

## 🎯 Changes Made to Match test01 Pipeline

### ✅ Updated Deploy Job Configuration

The `deploy-to-azure` job now follows the same pattern as the test01 pipeline:

1. **Runner**: Changed from `ubuntu-latest` to `App-Factory-Win-2019` (same as test01)
2. **Environment**: Using `Prod` environment (same as test01)
3. **Authentication**: Using BeyondTrust vault action (same as test01)
4. **Deploy Method**: Using `vendor-actions/azure-webapps-deploy@v2` (same as test01)

### 🔧 Key Differences from test01

| Configuration | test01 | eom-qa-pipeline |
|---------------|--------|-----------------|
| **App Service Name** | `ate-java17` | `eom-qa-java-selenium` |
| **Resource Group** | `TBD` | `zg1-rtx-dce-npd01-x12d1-ateco` |
| **Publish Profile Env** | `ATE_JAVA17_PUBLISH_PROFILE` | `EOM_QA_PUBLISH_PROFILE` |
| **Release Version** | `v1.0.${{ github.run_number }}` | `eom-qa-v1.0.${{ github.run_number }}` |

### 📋 Pipeline Job Structure

1. **build-and-test**: Build Spring Boot JAR (Java 17)
2. **selenium-qa-tests**: Execute Selenium QA automation
3. **docker-integration-test**: Integration testing with Docker
4. **deploy-to-azure**: Deploy to Azure Government Cloud (SAME AS TEST01)
5. **pipeline-summary**: Generate final pipeline report

### 🏗️ Azure Deployment Flow

```yaml
deploy-to-azure:
  runs-on: App-Factory-Win-2019    # Same runner as test01
  environment: Prod                 # Same environment as test01
  
  steps:
  - Checkout code
  - Setup Java 17
  - Download build artifacts
  - Get secrets from BeyondTrust    # Same auth method as test01
  - Setup XetaDev MS Build          # Same build tools as test01
  - Read publish profile            # Same profile method as test01
  - Deploy using azure-webapps-deploy # Same deploy action as test01
```

### 🔐 Required Environment Variables

The pipeline expects these environment variables from BeyondTrust:
- `EOM_QA_PUBLISH_PROFILE`: Publish profile for eom-qa-app Azure App Service

### 🌐 Deployment Target

- **Azure Cloud**: Government Cloud (AzureUSGovernment)
- **App Service**: `eom-qa-java-selenium` 
- **Resource Group**: `zg1-rtx-dce-npd01-x12d1-ateco`
- **URL**: `https://eom-qa-java-selenium.azurewebsites.us`

### ✅ Validation

The pipeline is now configured to deploy exactly like test01 but to our specific app service (`eom-qa-app` instead of `ate-java17`).

---
**Next Steps**: 
1. Configure `EOM_QA_PUBLISH_PROFILE` environment variable in BeyondTrust
2. Ensure `eom-qa-app` App Service exists in Azure Government Cloud
3. Test pipeline execution