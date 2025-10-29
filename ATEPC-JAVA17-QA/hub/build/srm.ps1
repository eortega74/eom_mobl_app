$projectName     = $env:ATE_JAVA17_PROJECT_NAME 
$srmDashboardUrl = "https://secdevops.rms.ray.com/srm/projects/$TIS_SRM_ID"
$token           = $env:ATE_JAVA17_TIS_SRM_TOKEN
$project_id      = $env:ATE_JAVA17_TIS_SRM_ID

$Findings_Uri      = "https://secdevops.rms.ray.com/codedx/api/projects/$project_id/findings"
$StartAnalysis_Uri = "https://secdevops.rms.ray.com/codedx/api/projects/$project_id/analysis"

$result_output_file = "result.json"

function main (){

    # Start a new analysis...  then wait 30 seconds before proceeding
    pro_info "Starting new SRM Analysis $projectName"
    echo $StartAnalysis_Uri
    start_srm_analysis "$StartAnalysis_Uri"

    pro_info "Downloading All Existing Findings from SRM for $projectName"
    download "$Findings_Uri/table" $result_output_file 

    read_severity_status $result_output_file
}

function start_srm_analysis($url_input)
{
    $params = @{
        Method      = 'Post'
        Uri         = $url_input
        Headers     = @{ 'Authorization' = "Bearer $token" }
        ContentType = 'application/json'
    }
    
    Invoke-WebRequest @params

    $r_code = $?
    check_error $r_code "Starting new SRM analysis for $projectName"
    
    Start-Sleep -Seconds 30  # usually takes < 5 seconds
}


function download($url_input, $json_file){ 
    $params = @{
        Method      = 'Post'
        Uri         = $url_input
        Headers     = @{ 'Authorization' = "Bearer $token" }
        ContentType = 'application/json'
        OutFile     = $json_file
        Body        = @{
                            pagination = @{
                            page = 1
                            perPage = 10
                            }
                        } | ConvertTo-Json
    }
    
    Invoke-WebRequest @params

    $r_code = $?
    check_error $r_code "Downloading SRM json file of $project_id"
}


# Check for severity "High" and Status name "New" or "Unresolved"
function read_severity_status($json_file){
    $findingArray = Get-Content -Path $json_file | ConvertFrom-Json #create the array
    $existing_count = 0;
    $severityUnspecified_count = 0;
    $severityLow_count    = 0;
    $severityMedium_count = 0;
    $severityHigh_count   = 0;
    $findingDetails = "Details";

    foreach($item in $findingArray)
        {
        if ($item.findingStatus.name -eq "Existing") 
            {
            $existing_count ++;


            $severity = $item.severity.name;
            if ($severity -eq "Unspecified") {$severityUnspecified_count++};
            if ($severity -eq "Low")    {$severityLow_count++};
            if ($severity -eq "Medium") {$severityMedium_count++};
            if ($severity -eq "High")   {$severityHigh_count++};

            #$statusName = $item.statusName
            $id = $item.id
            #if (($severity -eq "High") -and ( ($statusName -eq "New") -or ($statusName -eq "Unresolved")))
            #    {
            #    pro_info "There is a $severity severity that is $statusName in Project $project_id for id $id "
            #    $severity_count ++
            #    }
          
            $descriptor = $item.descriptor.name;
            $findingDetails += "`nThere is a $severity severity in Project $projectName (finding id $id - $descriptor)"
            }
        }

        $summary = "`n`nFINDINGS SUMMARY  `n-$severityUnspecified_count Unspecified issue(s)`n-$severityLow_count Low issue(s) `n-$severityMedium_count Medium issue(s) `n-$severityHigh_count High issue(s)`n`n$findingDetails"  
   
    if ( $severityHigh_count -gt 0)
        {
        $summary += "`n`nDeployment will fail due to $severityHigh_count issue(s) listed as High.`nView findings here: $srmDashboardUrl"
        pro_info $summary
        #exit 1
        }
    else 
        {
        $summary += "`n`nNo High Severity issues found.`nView findings here: $srmDashboardUrl"
        pro_info $summary
        }
}

# this function prints information
function pro_info($information){
    echo ===========================================================================================================
    echo "[ info ]   $information "
    echo ===========================================================================================================
}

# this function checks errors
function check_error($r_code, $message){
    if ($r_code -ne "true")
        {
        pro_info "Command for $message failed. Need Attention!!!!"
        exit(1)
        }
}

main
