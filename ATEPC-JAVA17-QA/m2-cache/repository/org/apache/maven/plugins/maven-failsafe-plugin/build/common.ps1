Get-ChildItem Env:


# This function create a zip file 
function generate_package($src_folder, $dest_package) {
    Compress-Archive -Verbose -Path $src_folder  -DestinationPath $dest_package -Force
    $r_code = $?
    check_error $r_code "Compressing $src_folder config folders to $dest_package"
}


# this function unzips package.zip file
function expand_package($src_package, $dest_folder, $App_name) {
    Expand-Archive -Verbose $src_package -DestinationPath $dest_folder 
    $r_code = $?
    check_error $r_code "Unzipping $App_name "
}


# This function generate a webapp property file
function generate_property(){
    new-item $env:PROPERTY_FILE_NAME -Force
    $Contents = "version=" + $env:RELEASE_VERSION + "`n" +
    "repo_name=" + $env:REPO_NAME + "`n" + 
    "branch_name=" + $env:GITHUB_REF_NAME + "`n" +
    "commit_id=" + $env:GITHUB_SHA + "`n" +
    "build_date=" + (Get-Date).ToString("yyyy-MM-dd-HH:mm:ss")

    $Utf8NoBomEncoding = New-Object System.Text.UTF8Encoding $False
    [System.IO.File]::WriteAllLines($env:PROPERTY_FILE_NAME, $Contents, $Utf8NoBomEncoding)  
    cat $env:PROPERTY_FILE_NAME
}

# this function initiate the jfrog configuration in the runner
function config_artifactory($jfrog_url, $jfrog_username, $jfrog_password){
    jf config add $jfrog_url --user $jfrog_username --access-token $jfrog_password
    $r_code = $?
    check_error $r_code "Configuring artifactory"
}

# this function deploy the latest webapp  package
function publish_to_artifactory($src_file, $target_path){
    echo $env:RELEASE_VERSION
    jf rt u $src_file $target_path --build-name $env:RELEASE_VERSION --build-number $env:RELEASE_VERSION --recursive=false > output.json
    $FileContent = Get-Content "output.json"
    $Matches = Select-String -InputObject $FileContent -Pattern "success" -AllMatches
    $r_code = $Matches.Matches.Count -eq 2
    check_error $r_code "Publishing $src_file to artifactory"
}

# this function prints information
function pro_info($information){
    echo ===========================================================================================================
    echo "[ info ]   $information "
    echo ===========================================================================================================
}

# this function check errors
function check_error($r_code, $message){
    if ($r_code -eq "true"){
        pro_info "Command for $message is finished successfully"
    }
    else {
        pro_info "Command for $message failed. Need Attention!!!!"
        exit(1)
    }
}
