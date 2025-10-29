# EOM QA - Script de Pruebas Completas
# Versión simplificada y funcional

param(
    [string]$TestSuite = "all",     # all, selenium, unit, integration
    [string]$Browser = "chrome",    # chrome, firefox
    [switch]$SkipBuild = $false,    # Skip maven build
    [switch]$DockerMode = $false,   # Use Docker for tests
    [switch]$Cleanup = $true        # Cleanup after tests
)

Write-Host "=============================================" -ForegroundColor Magenta
Write-Host "    EOM QA AUTOMATION - TEST EXECUTION      " -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Magenta

$ErrorActionPreference = "Continue"
$StartTime = Get-Date

# Configuration
$AppPort = 8080
$SeleniumPort = 4444
$AppUrl = "http://localhost:$AppPort"
$HealthUrl = "$AppUrl/actuator/health"
$ReportsDir = ".\reports"

Write-Host "🔧 Configuración:" -ForegroundColor Cyan
Write-Host "   Test Suite: $TestSuite" -ForegroundColor Yellow
Write-Host "   Browser: $Browser" -ForegroundColor Yellow
Write-Host "   Docker Mode: $DockerMode" -ForegroundColor Yellow
Write-Host "   Skip Build: $SkipBuild" -ForegroundColor Yellow

# Create reports directory
if (-not (Test-Path $ReportsDir)) {
    New-Item -ItemType Directory -Path $ReportsDir -Force | Out-Null
    Write-Host "📁 Created reports directory" -ForegroundColor Green
}

# Function: Check if port is in use
function Test-Port {
    param([int]$Port)
    try {
        $connection = New-Object System.Net.Sockets.TcpClient
        $connection.Connect("localhost", $Port)
        $connection.Close()
        return $true
    } catch {
        return $false
    }
}

# Function: Wait for service
function Wait-ForService {
    param(
        [string]$Url,
        [int]$MaxAttempts = 30,
        [string]$ServiceName = "Service"
    )
    
    Write-Host "⏳ Waiting for $ServiceName at $Url..." -ForegroundColor Cyan
    
    for ($i = 1; $i -le $MaxAttempts; $i++) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Write-Host "   ✅ $ServiceName is ready!" -ForegroundColor Green
                return $true
            }
        } catch {
            # Service not ready yet
        }
        
        if ($i -eq $MaxAttempts) {
            Write-Host "   ❌ $ServiceName failed to start after $MaxAttempts attempts" -ForegroundColor Red
            return $false
        }
        
        Write-Host "   Attempt $i/$MaxAttempts: $ServiceName not ready..." -ForegroundColor Yellow
        Start-Sleep -Seconds 2
    }
    return $false
}

# Step 1: Build Application
if (-not $SkipBuild) {
    Write-Host "`n🏗️ Building Application..." -ForegroundColor Cyan
    
    try {
        Write-Host "   Cleaning previous builds..." -ForegroundColor Yellow
        & mvn clean -q
        
        Write-Host "   Compiling application..." -ForegroundColor Yellow
        & mvn compile -q
        
        Write-Host "   Building JAR package..." -ForegroundColor Yellow
        & mvn package -DskipTests -q
        
        if (Test-Path "target\eom-qa-simple-1.0.0-SNAPSHOT.jar") {
            Write-Host "   ✅ Build completed successfully" -ForegroundColor Green
            $jarSize = (Get-Item "target\eom-qa-simple-1.0.0-SNAPSHOT.jar").Length / 1MB
            Write-Host "   📦 JAR Size: $([math]::Round($jarSize, 2)) MB" -ForegroundColor Green
        } else {
            Write-Host "   ❌ Build failed - JAR not found" -ForegroundColor Red
            exit 1
        }
    } catch {
        Write-Host "   ❌ Build error: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "⏭️ Skipping build step" -ForegroundColor Yellow
}

# Step 2: Setup Test Environment
Write-Host "`n🔧 Setting up Test Environment..." -ForegroundColor Cyan

if ($DockerMode) {
    Write-Host "   🐳 Starting Docker services..." -ForegroundColor Yellow
    
    # Stop existing containers
    & docker-compose down --remove-orphans 2>$null
    
    # Start Selenium first
    & docker-compose up -d selenium
    if ($LASTEXITCODE -ne 0) {
        Write-Host "   ❌ Failed to start Selenium container" -ForegroundColor Red
        exit 1
    }
    
    # Wait for Selenium
    if (-not (Wait-ForService "http://localhost:$SeleniumPort/wd/hub/status" 30 "Selenium Grid")) {
        Write-Host "   ❌ Selenium Grid failed to start" -ForegroundColor Red
        & docker-compose logs selenium
        exit 1
    }
    
    # Start application
    & docker-compose up -d app
    if ($LASTEXITCODE -ne 0) {
        Write-Host "   ❌ Failed to start application container" -ForegroundColor Red
        exit 1
    }
    
    # Wait for application
    if (-not (Wait-ForService $HealthUrl 60 "EOM QA Application")) {
        Write-Host "   ❌ Application failed to start" -ForegroundColor Red
        & docker-compose logs app
        exit 1
    }
    
} else {
    Write-Host "   💻 Starting local services..." -ForegroundColor Yellow
    
    # Check if Selenium is already running
    if (-not (Test-Port $SeleniumPort)) {
        Write-Host "   ⚠️ Selenium Grid not running on port $SeleniumPort" -ForegroundColor Yellow
        Write-Host "   💡 Start Selenium with: docker run -d -p 4444:4444 --shm-size=2g selenium/standalone-chrome:latest" -ForegroundColor Yellow
        Write-Host "   Or use Docker mode: -DockerMode" -ForegroundColor Yellow
    } else {
        Write-Host "   ✅ Selenium Grid detected on port $SeleniumPort" -ForegroundColor Green
    }
    
    # Start application if not running
    if (-not (Test-Port $AppPort)) {
        Write-Host "   🚀 Starting EOM QA application..." -ForegroundColor Yellow
        $env:SELENIUM_HOST = "localhost"
        $appProcess = Start-Process -FilePath "java" -ArgumentList "-jar", "target\eom-qa-simple-1.0.0-SNAPSHOT.jar", "--server.port=$AppPort" -PassThru -WindowStyle Hidden
        
        if (-not (Wait-ForService $HealthUrl 60 "EOM QA Application")) {
            Write-Host "   ❌ Application failed to start" -ForegroundColor Red
            if ($appProcess -and -not $appProcess.HasExited) {
                $appProcess.Kill()
            }
            exit 1
        }
        
        # Store process for cleanup
        $appProcess.Id | Out-File -FilePath "$ReportsDir\app.pid" -Encoding ASCII
    } else {
        Write-Host "   ✅ Application already running on port $AppPort" -ForegroundColor Green
    }
}

# Step 3: Run Tests
Write-Host "`n🧪 Running Tests..." -ForegroundColor Cyan

$testResults = @{
    "Total" = 0
    "Passed" = 0
    "Failed" = 0
    "Skipped" = 0
}

# Unit Tests
if ($TestSuite -in @("all", "unit")) {
    Write-Host "   📝 Running Unit Tests..." -ForegroundColor Yellow
    try {
        & mvn test -Dtest="!com.example.demo.Selenium" -q
        if ($LASTEXITCODE -eq 0) {
            Write-Host "   ✅ Unit tests passed" -ForegroundColor Green
            $testResults.Passed++
        } else {
            Write-Host "   ❌ Unit tests failed" -ForegroundColor Red
            $testResults.Failed++
        }
    } catch {
        Write-Host "   ❌ Unit test error: $($_.Exception.Message)" -ForegroundColor Red
        $testResults.Failed++
    }
    $testResults.Total++
}

# Selenium Tests
if ($TestSuite -in @("all", "selenium")) {
    Write-Host "   🖥️ Running Selenium Tests..." -ForegroundColor Yellow
    try {
        $env:SELENIUM_HOST = if ($DockerMode) { "selenium" } else { "localhost" }
        & mvn test -Dtest="com.example.demo.Selenium" -DSELENIUM_HOST=$env:SELENIUM_HOST -q
        if ($LASTEXITCODE -eq 0) {
            Write-Host "   ✅ Selenium tests passed" -ForegroundColor Green
            $testResults.Passed++
        } else {
            Write-Host "   ❌ Selenium tests failed" -ForegroundColor Red
            $testResults.Failed++
        }
    } catch {
        Write-Host "   ❌ Selenium test error: $($_.Exception.Message)" -ForegroundColor Red
        $testResults.Failed++
    }
    $testResults.Total++
}

# Integration Tests
if ($TestSuite -in @("all", "integration")) {
    Write-Host "   🔗 Running Integration Tests..." -ForegroundColor Yellow
    try {
        # Simple connectivity test
        $response = Invoke-WebRequest -Uri $AppUrl -UseBasicParsing -TimeoutSec 10
        if ($response.StatusCode -eq 200 -or $response.StatusCode -eq 302) {
            Write-Host "   ✅ Integration tests passed" -ForegroundColor Green
            $testResults.Passed++
        } else {
            Write-Host "   ❌ Integration tests failed - Unexpected status: $($response.StatusCode)" -ForegroundColor Red
            $testResults.Failed++
        }
    } catch {
        Write-Host "   ❌ Integration test error: $($_.Exception.Message)" -ForegroundColor Red
        $testResults.Failed++
    }
    $testResults.Total++
}

# Step 4: Collect Results
Write-Host "`n📊 Collecting Test Results..." -ForegroundColor Cyan

# Copy test reports
if (Test-Path "target\surefire-reports") {
    Copy-Item "target\surefire-reports\*" $ReportsDir -Recurse -Force 2>$null
    Write-Host "   📄 Surefire reports copied" -ForegroundColor Green
}

# Generate summary report
$EndTime = Get-Date
$Duration = $EndTime - $StartTime
$reportContent = @"
EOM QA Test Execution Report
============================
Execution Date: $($StartTime.ToString('yyyy-MM-dd HH:mm:ss'))
Duration: $($Duration.ToString('hh\:mm\:ss'))
Test Suite: $TestSuite
Browser: $Browser
Docker Mode: $DockerMode

Test Results:
- Total Tests: $($testResults.Total)
- Passed: $($testResults.Passed)
- Failed: $($testResults.Failed)
- Success Rate: $(if($testResults.Total -gt 0){[math]::Round(($testResults.Passed / $testResults.Total) * 100, 2)}else{0})%

Application Health:
- Port: $AppPort
- Health Endpoint: $HealthUrl
- Status: $(if(Test-Port $AppPort){'Running'}else{'Stopped'})

Test Environment:
- Java Version: $((java -version 2>&1)[0])
- Maven Version: $((mvn -version 2>&1)[0])
- Selenium Grid: $(if(Test-Port $SeleniumPort){'Available'}else{'Not Available'})

Generated: $($EndTime.ToString('yyyy-MM-dd HH:mm:ss'))
"@

$reportContent | Out-File -FilePath "$ReportsDir\test-execution-report.txt" -Encoding UTF8
Write-Host "   📋 Test report generated: $ReportsDir\test-execution-report.txt" -ForegroundColor Green

# Step 5: Cleanup
if ($Cleanup) {
    Write-Host "`n🧹 Cleaning up..." -ForegroundColor Cyan
    
    if ($DockerMode) {
        Write-Host "   🐳 Stopping Docker containers..." -ForegroundColor Yellow
        & docker-compose down --remove-orphans 2>$null
    } else {
        # Stop local application
        if (Test-Path "$ReportsDir\app.pid") {
            $appPid = Get-Content "$ReportsDir\app.pid" -ErrorAction SilentlyContinue
            if ($appPid) {
                try {
                    Stop-Process -Id $appPid -Force -ErrorAction SilentlyContinue
                    Write-Host "   🛑 Application stopped (PID: $appPid)" -ForegroundColor Green
                } catch {
                    Write-Host "   ⚠️ Could not stop application process" -ForegroundColor Yellow
                }
            }
            Remove-Item "$ReportsDir\app.pid" -ErrorAction SilentlyContinue
        }
    }
}

# Final Summary
Write-Host "`n" + "="*50 -ForegroundColor Magenta
Write-Host "    EOM QA TEST EXECUTION COMPLETED    " -ForegroundColor Green
Write-Host "="*50 -ForegroundColor Magenta

if ($testResults.Failed -eq 0) {
    Write-Host "🎉 SUCCESS: All tests passed!" -ForegroundColor Green
    Write-Host "✅ Tests Passed: $($testResults.Passed)/$($testResults.Total)" -ForegroundColor Green
} else {
    Write-Host "❌ FAILED: Some tests failed" -ForegroundColor Red
    Write-Host "🔴 Tests Failed: $($testResults.Failed)/$($testResults.Total)" -ForegroundColor Red
}

Write-Host "⏱️ Total Execution Time: $($Duration.ToString('hh\:mm\:ss'))" -ForegroundColor Cyan
Write-Host "📁 Reports: $ReportsDir\" -ForegroundColor Cyan
Write-Host ""

exit $testResults.Failed