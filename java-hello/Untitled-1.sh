

az cloud set --name AzureUSGovernment
az login --use-device-code
az webapp deployment list-publishing-profiles --name ed-ora-java-app --resource-group zg1-rtx-dce-npd01-x12d1-atecor --output json > publish-profile.json

mvn azure-webapp:deploy -Dfile=demo-0.0.5-SNAPSHOT.jar -DappName=ed-ora-java-app -DresourceGroup=zg1-rtx-dce-npd01-x12d1-atecor

az webapp deployment source config-zip --resource-group zg1-rtx-dce-npd01-x12d1-atecor --name ed-ora-java-app --src demo-0.0.5-SNAPSHOT.zip


az webapp deploy --resource-group zg1-rtx-dce-npd01-x12d1-atecor --name ed-ora-java-app --src-path demo-0.0.5-SNAPSHOT.zip


Stage 0
Scan 
Stage 1
mvn clean package -DskipTests

Stage 2
az webapp deploy  --resource-group zg1-rtx-dce-npd01-x12d1-atecor --name ed-ora-java-app --src-path output/app.jar  --type jar --clean true