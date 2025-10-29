. .\.github\build\common.ps1 #importing common functions

#Packaging repo to create a zip file
pro_info "Packaging build output into a zip file"
generate_package $env:PATH_FOR_DACPAC_TO_ZIP "package.zip"


#Generating a $env:PROPERTY_FILE_NAME file
pro_info "Generating a $env:PROPERTY_FILE_NAME file"
generate_property
pro_info "Done Generating $env:PROPERTY_FILE_NAME file"

#Configure Artifactory for $env:JF_BUILD_REPO Repo
pro_info "Configure Artifactory for $env:JFROG_URL/$env:JF_BUILD_REPO Repo"
config_artifactory "--artifactory-url=$env:JFROG_URL" "$env:JF_USER" "$env:JF_USER_TOKEN"

#Publishing package and property file to $env:JF_BUILD_REPO Repo
pro_info "Publishing package and property file to $env:JF_BUILD_REPO Repo"


if ($env:BRANCH_NAME -eq "master")
  {
  publish_to_artifactory "$env:PROPERTY_FILE_NAME" "$env:JF_BUILD_REPO/$env:RELEASE_VERSION/"
  publish_to_artifactory "$env:PROPERTY_FILE_NAME" "$env:JF_BUILD_REPO/$env:RELEASE_VERSION/$env:DB_FOLDER_NAME/"
  publish_to_artifactory "package.zip"             "$env:JF_BUILD_REPO/$env:RELEASE_VERSION/$env:DB_FOLDER_NAME/"
  }
else
  {
  publish_to_artifactory "$env:PROPERTY_FILE_NAME" "$env:JF_BUILD_REPO/$env:RELEASE_VERSION_WITH_BRANCH_PREFIX/"
  publish_to_artifactory "$env:PROPERTY_FILE_NAME" "$env:JF_BUILD_REPO/$env:RELEASE_VERSION_WITH_BRANCH_PREFIX/$env:DB_FOLDER_NAME/"
  publish_to_artifactory "package.zip"             "$env:JF_BUILD_REPO/$env:RELEASE_VERSION_WITH_BRANCH_PREFIX/$env:DB_FOLDER_NAME/"
  }

pro_info "Done Publishing package and property file to $env:JF_BUILD_REPO Repo"
