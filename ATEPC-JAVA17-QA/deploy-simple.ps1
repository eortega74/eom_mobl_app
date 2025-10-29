Write-Host "Deploying EOM QA to ed-ora..." -ForegroundColor Green

# Compile
Write-Host "Compiling..." -ForegroundColor Yellow  
mvn clean package -DskipTests

# Deploy
Write-Host "Deploying to Azure..." -ForegroundColor Yellow
mvn azure-webapp:deploy

Write-Host "Done! Check: https://ed-ora.azurewebsites.us" -ForegroundColor Green