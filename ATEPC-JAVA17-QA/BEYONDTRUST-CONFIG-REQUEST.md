# BeyondTrust Configuration Request - EOM QA Pipeline

## 📋 **New Environment Variable Request**

### **Variable Details:**
- **Variable Name**: `EOM_QA_PUBLISH_PROFILE`
- **Type**: Secret/Credential
- **Purpose**: Azure App Service deployment credentials for EOM QA pipeline
- **Usage**: GitHub Actions pipeline deployment to Azure Government Cloud

### **Technical Details:**
- **Target App Service**: `eom-qa-java-selenium`
- **Azure Resource Group**: `zg1-rtx-dce-npd01-x12d1-ateco`
- **Azure Cloud**: Government (AzureUSGovernment)
- **Pipeline**: `eom-qa-pipeline.yml`
- **Runner Access**: App-Factory-Win-2019

### **Variable Content:**
The value should be the complete XML content from the publish profile downloaded from Azure Portal for the app service `eom-qa-java-selenium`.

**XML Structure Example:**
```xml
<publishData>
  <publishProfile 
    profileName="eom-qa-java-selenium - Web Deploy" 
    publishMethod="MSDeploy" 
    publishUrl="eom-qa-java-selenium.scm.azurewebsites.us:443" 
    msdeploySite="eom-qa-java-selenium" 
    userName="$eom-qa-java-selenium" 
    userPWD="[CREDENTIAL_FROM_AZURE]" 
    destinationAppUrl="https://eom-qa-java-selenium.azurewebsites.us" 
    .../>
  <!-- Additional profiles for FTP and Zip Deploy -->
</publishData>
```

### **Security Requirements:**
- **Access Control**: Only accessible by GitHub Actions runners
- **Encryption**: Store as encrypted secret
- **Audit**: Log access for compliance
- **Scope**: Project-specific (eom-qa pipeline only)

### **Similar Existing Variables:**
- **Reference**: `ATE_JAVA17_PUBLISH_PROFILE` (for ate-java17 app service)
- **Same Pattern**: Following established naming convention
- **Same Permissions**: Similar access level required

### **Justification:**
1. **Security**: Each app service requires its own deployment credentials
2. **Isolation**: Independent deployment targets prevent cross-contamination
3. **Compliance**: Follows corporate security practices for credential management
4. **Scalability**: Enables multiple environments with proper separation

### **Deployment Flow:**
1. Pipeline retrieves `EOM_QA_PUBLISH_PROFILE` from BeyondTrust
2. Authenticates with Azure using embedded credentials
3. Deploys Java 17 application to `eom-qa-java-selenium`
4. Application becomes available at `https://eom-qa-java-selenium.azurewebsites.us`

### **Testing Verification:**
After configuration, the following endpoints will be accessible:
- **Application**: https://eom-qa-java-selenium.azurewebsites.us
- **Health Check**: https://eom-qa-java-selenium.azurewebsites.us/actuator/health

### **Contact Information:**
- **Requestor**: [Your Name/Team]
- **Technical Contact**: [Your Email]
- **Business Justification**: EOM QA automation pipeline deployment to Azure Government Cloud
- **Urgency**: Standard deployment requirement

---

## ✅ **Ready for Implementation**

Once this variable is configured in BeyondTrust, the EOM QA pipeline will be fully functional for automated deployment to Azure Government Cloud.

**Pipeline Status**: Ready and waiting for BeyondTrust configuration.