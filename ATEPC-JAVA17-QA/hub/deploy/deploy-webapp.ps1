. .\.github\build\common.ps1 #importing common functions


try {
    echo "Attempting to deplot to azure"
    az webapp deploy --resource-group $env:AZURE_RESOURCE_GROUP --name $env:AZURE_WEBAPP_NAME --src-path ./$env:RELEASE_VERSION_WITH_BRANCH_PREFIX/$env:WEBAPP_FOLDER_NAME/package.zip
    #$env:JF_BUILD_REPO/$env:RELEASE_VERSION_WITH_BRANCH_PREFIX/$env:WEBAPP_FOLDER_NAME/package.zip
    echo "Deployment succeeded!"
}
catch {
    echo "Deployment failed!"
    echo "Error message: $($_.Exception.Message)"
    echo "Full error: $($_ | Out-String)"    
}

# if ($env:BRANCH_NAME -eq "master")
#     {
#     pro_info "Deploying webapp package for release version $env:RELEASE_VERSION ($env:BRANCH_NAME branch branch)"
#     az webapp config appsettings set        --resource-group $env:AZURE_RESOURCE_GROUP --name $env:AZURE_WEBAPP_NAME --subscription $env:AZURE_SUBSCRIPTION_ID --settings WEBSITE_RUN_FROM_PACKAGE="1"     
#     az webapp config appsettings set        --resource-group $env:AZURE_RESOURCE_GROUP --name $env:AZURE_WEBAPP_NAME --subscription $env:AZURE_SUBSCRIPTION_ID --settings ASPNETCORE_ENVIRONMENT=$env:TIS_ASPNETCORE_ENVIRONMENT_VALUE      
#     az webapp config appsettings set        --resource-group $env:AZURE_RESOURCE_GROUP --name $env:AZURE_WEBAPP_NAME --subscription $env:AZURE_SUBSCRIPTION_ID --settings RELEASE_VERSION=$env:RELEASE_VERSION                                                   
#     az webapp deployment source config-zip  --resource-group $env:AZURE_RESOURCE_GROUP --name $env:AZURE_WEBAPP_NAME --subscription $env:AZURE_SUBSCRIPTION_ID --src ./$env:RELEASE_VERSION/$env:WEBAPP_FOLDER_NAME/package.zip
#     if ($? -eq "true") 
#         {
#         echo "Deploying package.zip to Azure webapp finished successfully"
#         }
#     else 
#         {
#         echo "Deploying package.zip to Azure webapp failed. Need Attention!!!!"
#         exit(1)
#         }
#     }
# else
#     {
#     pro_info "Deploying webapp package for release version $env:RELEASE_VERSION_WITH_BRANCH_PREFIX ($env:BRANCH_NAME branch)"
#     az webapp config appsettings set        --resource-group $env:AZURE_RESOURCE_GROUP --name $env:AZURE_WEBAPP_NAME --subscription $env:AZURE_SUBSCRIPTION_ID --settings WEBSITE_RUN_FROM_PACKAGE="1"     
#     az webapp config appsettings set        --resource-group $env:AZURE_RESOURCE_GROUP --name $env:AZURE_WEBAPP_NAME --subscription $env:AZURE_SUBSCRIPTION_ID --settings ASPNETCORE_ENVIRONMENT=$env:TIS_ASPNETCORE_ENVIRONMENT_VALUE  
#     az webapp config appsettings set        --resource-group $env:AZURE_RESOURCE_GROUP --name $env:AZURE_WEBAPP_NAME --subscription $env:AZURE_SUBSCRIPTION_ID --settings RELEASE_VERSION=$env:RELEASE_VERSION_WITH_BRANCH_PREFIX                                                   
#     az webapp deployment source config-zip  --resource-group $env:AZURE_RESOURCE_GROUP --name $env:AZURE_WEBAPP_NAME --subscription $env:AZURE_SUBSCRIPTION_ID --src ./$env:RELEASE_VERSION_WITH_BRANCH_PREFIX/$env:WEBAPP_FOLDER_NAME/package.zip
#     if ($? -eq "true") 
#         {
#         echo "Deploying package.zip to Azure webapp finished successfully"
#         }
#     else 
#         {
#         echo "Deploying package.zip to Azure webapp failed. Need Attention!!!!"
#         exit(1)
#         }
#     }

# pro_info "TIS webapp deployment complete"
