. .\.github\build\common.ps1 #importing common functions

#Packaging repo to create a zip file
pro_info "Packaging build output ($env:PATH_FOR_FILES_TO_ZIP) into a zip file (package.zip)"
generate_package $env:ATE_JAVA17_PATH_FOR_FILES_TO_ZIP "package.zip"


#Generating a $env:PROPERTY_FILE_NAME file
pro_info "Generating a $env:ATE_JAVA17_PROPERTY_FILE_NAME file"
generate_property
pro_info "Done Generating $env:ATE_JAVA17_PROPERTY_FILE_NAME file"

#Configure Artifactory for $env:JF_BUILD_REPO Repo
pro_info "Configure Artifactory for $env:ATE_JAVA17_JFROG_URL/$env:ATE_JAVA17_JF_BUILD_REPO Repo"
config_artifactory "--artifactory-url=$env:ATE_JAVA17_JFROG_URL" "$env:ATE_JAVA17_JF_USER" "$env:ATE_JAVA17_JF_USER_TOKEN"

#Publishing package and property file to $env:JF_BUILD_REPO Repo
pro_info "Publishing package and property file to $env:ATE_JAVA17_JF_BUILD_REPO Repo"

if ($env:BRANCH_NAME -eq "master")
  {
  publish_to_artifactory "$env:ATE_JAVA17_PROPERTY_FILE_NAME" "$env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION/"
  publish_to_artifactory "$env:ATE_JAVA17_PROPERTY_FILE_NAME" "$env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION/$env:ATE_JAVA17_WEBAPP_FOLDER_NAME/"
  publish_to_artifactory "package.zip" "$env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION/$env:ATE_JAVA17_WEBAPP_FOLDER_NAME/"
  }
else
  {
  publish_to_artifactory "$env:ATE_JAVA17_PROPERTY_FILE_NAME" "$env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION_WITH_BRANCH_PREFIX/"
  publish_to_artifactory "$env:ATE_JAVA17_PROPERTY_FILE_NAME" "$env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION_WITH_BRANCH_PREFIX/$env:ATE_JAVA17_WEBAPP_FOLDER_NAME/"
  publish_to_artifactory "package.zip"             "$env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION_WITH_BRANCH_PREFIX/$env:ATE_JAVA17_WEBAPP_FOLDER_NAME/"
  }


pro_info "Done Publishing package and property file to $env:ATE_JAVA17_JF_BUILD_REPO Repo"
