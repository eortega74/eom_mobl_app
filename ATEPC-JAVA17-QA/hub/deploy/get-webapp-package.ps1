. .\.github\build\common.ps1 #importing common functions


jf c add --artifactory-url=https://artifactory.utc.com/artifactory --user $env:ATE_JAVA17_JF_USER --access-token $env:ATE_JAVA17_JF_USER_TOKEN

pro_info "Getting TIS webapp package.zip for release version $env:ATE_JAVA17_RELEASE_VERSION ($env:ATE_JAVA17_BRANCH_NAME branch)"

config_artifactory "--artifactory-url=$env:ATE_JAVA17_JFROG_URL" "$env:ATE_JAVA17_JF_USER" "$env:ATE_JAVA17_JF_USER_TOKEN"

pro_info "Signed into JFROG CLI"

if ($env:ATE_JAVA17_BRANCH_NAME -eq "master")
  {
  pro_info  "downloading $env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION/$env:ATE_JAVA17_WEBAPP_FOLDER_NAME/package.zip"
  jf rt dl               $env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION/$env:ATE_JAVA17_WEBAPP_FOLDER_NAME/package.zip
  pro_info  "downloaded  $env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION/$env:ATE_JAVA17_WEBAPP_FOLDER_NAME/package.zip"
  }
else
  {
  pro_info  "downloading $env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION_WITH_BRANCH_PREFIX/$env:ATE_JAVA17_WEBAPP_FOLDER_NAME/package.zip"
  jf rt dl               $env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION_WITH_BRANCH_PREFIX/$env:ATE_JAVA17_WEBAPP_FOLDER_NAME/package.zip
  pro_info  "downloaded  $env:ATE_JAVA17_JF_BUILD_REPO/$env:ATE_JAVA17_RELEASE_VERSION_WITH_BRANCH_PREFIX/$env:ATE_JAVA17_WEBAPP_FOLDER_NAME/package.zip"
  }
