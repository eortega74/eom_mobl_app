. .\.github\build\common.ps1 # import common functions

$POM_LOCATION = 'C:\apps\actions-runner\_work\ATEPC-JAVA17-SPRING\ATEPC-JAVA17-SPRING'
$POM_SUB_FOLDER = 'ATEPC-JAVA17-SPRING'

try {
    pro_info "Starting Coverity configuration"
    cov-configure --java
    check_error $? "cov-configure failed"

    pro_info "Starting Maven build with Coverity instrumentation"
    Set-Location $POM_LOCATION
    
    $COVERITY_INTERMEDIATE = Join-Path $POM_LOCATION $env:ATE_JAVA17_COVERITY_PROJECT
    cov-build --dir $COVERITY_INTERMEDIATE mvn -s settings.xml clean compile
    check_error $? "cov-build (mvn clean package) failed"

    echo "Printing files for debugging"
    Get-ChildItem -Path $COVERITY_INTERMEDIATE -Recurse | Format-List FullName, Length, LastWriteTime

    echo "Printing contacts of coverity build log"
    Get-Content C:/apps/actions-runner/_work/ATEPC-JAVA17-SPRING/ATEPC-JAVA17-SPRING/atepc-java17/build-log.txt
    pro_info "Running Coverity analysis"
    cov-analyze --dir $COVERITY_INTERMEDIATE --all
    check_error $? "cov-analyze failed"

    pro_info "Committing defects to Coverity server"
    cov-commit-defects --dir $COVERITY_INTERMEDIATE --url $env:ATE_JAVA17_COVERITY_URL --stream $env:ATE_JAVA17_COVERITY_STREAM --user $env:ATE_JAVA17_COVERITY_COMMITER_USERNAME --password $env:ATE_JAVA17_COVERITY_COMMITER_PASSWORD
    check_error $? "cov-commit-defects failed"


    pro_info "Coverity scan completed successfully"

} catch {
    pro_info "Error during Coverity scan: $_"
    exit 1
}
