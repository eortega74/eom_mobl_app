if (-not $env:BLACKDUCK_URL) {
    Write-Host "Black Duck Server URL is NOT defined. Please set the BLACKDUCK_URL environment variable."
    Exit 2
}

if (-not $env:BLACKDUCK_API_TOKEN) {
    Write-Host "Black Duck API Token NOT defined. Please set the BLACKDUCK_API_TOKEN environment variable."
    Exit 2
}

if (-not $env:DETECT_PROJECT_NAME) {
    Write-Host "Black Duck Project Name NOT defined. Please set the DETECT_PROJECT_NAME environment variable."
    Exit 2
}

if (-not $env:DETECT_PROJECT_VERSION) {
    Write-Host "Black Duck Project Version NOT defined. Please set the DETECT_PROJECT_VERSION environment variable."
    Exit 2
}

if (-not $env:JAVA_HOME) {
    Write-Host "JAVA_HOME is NOT defined. Please set the JAVA_HOME environment variable."
    Exit 2
}

java -jar C:\apps\blackduck\synopsys-detect-8.8.0.jar --detect.diagnostic=true --detect.npm.path=./.npmrc $args
