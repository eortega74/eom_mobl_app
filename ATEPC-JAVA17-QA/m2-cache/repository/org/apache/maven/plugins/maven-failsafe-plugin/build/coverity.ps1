. .\.github\build\common.ps1 #importing common functions
$CSPROJ_LOCATION = $args[0]
$CSPROJ_SUB_FOLDER = $args[1]

pro_info "Running Coverity for the $CSPROJ_LOCATION project "
pro_info "On $env:COVERITY_PROJECT-$CSPROJ_SUB_FOLDER "
pro_info "Coverity is starting ................."
pro_info "Configure Coverity"

$timestamp_log = Get-Date -Format "dddd MM/dd/yyyy HH:mm:ss:M K"
pro_info "Configure is Starting at $timestamp_log"
cov-configure --cs
#cov-configure --vb
check_error $? "Configure Coverity"

$timestamp_log = Get-Date -Format "dddd MM/dd/yyyy HH:mm:ss:M K"
pro_info "Build is Starting at $timestamp_log"
pro_info "Build the App"
#cov-build --dir $env:COVERITY_PROJECT-$CSPROJ_SUB_FOLDER msbuild $CSPROJ_LOCATION /nologo /verbosity:m /t:clean /t:Build /t:pipelinePreDeployCopyAllFilesToOneFolder /p:Configuration=Release /p:DeployOnBuild=true /p:WebPublishMethod=Package /p:PackageAsSingleFile=false /p:SkipInvalidConfigurations=true 
cov-build --dir $env:COVERITY_PROJECT-$CSPROJ_SUB_FOLDER  msbuild.exe  $CSPROJ_LOCATION  /nologo /verbosity:m /t:clean /target:Publish /p:RestorePackages=false /p:_IsPublishing=true /p:Configuration=Release  
check_error $? "Build the App on $timestamp_log"

$timestamp_log = Get-Date -Format "dddd MM/dd/yyyy HH:mm:ss:M K"
pro_info "Analyze is Starting at $timstamp_log"
pro_info "Analyze the Build on"
cov-analyze --dir $env:COVERITY_PROJECT-$CSPROJ_SUB_FOLDER --all
check_error $? "Analyze the App"

$timestamp_log = Get-Date -Format "dddd MM/dd/yyyy HH:mm:ss:M K"
pro_info "Push is Starting at $timestamp_log"
pro_info "Push the Results to the Coverity Server on $timestamp_log"
cov-commit-defects --dir $env:COVERITY_PROJECT-$CSPROJ_SUB_FOLDER --url "$env:COVERITY_URL" --stream $env:COVERITY_STREAM --user "$env:COVERITY_COMMITER_USERNAME" --password "$env:COVERITY_COMMITER_PASSWORD"
#check_error $? "Push the Results to the Coverity Server on $timestamp_log"
$timestamp_log = Get-Date -Format "dddd MM/dd/yyyy HH:mm:ss:M K"
pro_info "Coverity Build for $CSPROJ_LOCATION ended at $timestamp_log"
