	. .\.github\build\common.ps1 #importing common functions

#Ensure artifactory authorization 
pro_info "Configure Artifactory for $env:JFROG_URL/$env:JF_MAVEN_REPO Repo"
config_artifactory "--artifactory-url=$env:JFROG_URL" "$env:JF_USER" "$env:JF_USER_TOKEN"

#upload dependencies to artifactory
#jf rt u "$env:MAVEN_PATH" "$env:JF_MAVEN_REPO/{1}" --flat=false --recursive=true
jf rt u "$env:MAVEN_PATH/(*)" "$env:JF_MAVEN_REPO/{1}" --flat=false --recursive=true
# jf rt u "$env:MAVEN_PATH/(*)" "$env:JF_MAVEN_REPO/{1}" `
#     --flat=false `
#     --recursive=true `
#     --regexp=true `
#     --exclude="*.sha1" `
#     --exclude="*.md5" `
#     --exclude="_remote.repositories"
pro_info "Successfully pushed mvn packages to artifactory"
