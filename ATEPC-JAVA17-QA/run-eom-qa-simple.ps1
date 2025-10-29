# EOM QA - Script de Pruebas Simplificado
# Versión corregida y funcional

param(
    [string]$TestSuite = "all",
    [string]$Browser = "chrome", 
    [switch]$SkipBuild = $false,
    [switch]$DockerMode = $false,
    [switch]$Cleanup = $true
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

# Step 2: Run Tests
Write-Host "`n🧪 Running Tests..." -ForegroundColor Cyan

$testResults = @{
    "Total" = 0
    "Passed" = 0
    "Failed" = 0
}

# Unit Tests
if ($TestSuite -in @("all", "unit")) {
    Write-Host "   📝 Running Unit Tests..." -ForegroundColor Yellow
    try {
        mvn test -Dtest="!com.example.demo.Selenium" -q
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

# Step 3: Collect Results
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
  Total Tests: $($testResults.Total)
  Passed: $($testResults.Passed)
  Failed: $($testResults.Failed)
  Success Rate: $(if($testResults.Total -gt 0){[math]::Round(($testResults.Passed / $testResults.Total) * 100, 2)}else{0})%

Application Health:
  Port: $AppPort
  Health Endpoint: $HealthUrl
  Status: $(if(Test-Port $AppPort){'Running'}else{'Stopped'})

Generated: $($EndTime.ToString('yyyy-MM-dd HH:mm:ss'))
"@

$reportContent | Out-File -FilePath "$ReportsDir\test-execution-report.txt" -Encoding UTF8
Write-Host "   📋 Test report generated: $ReportsDir\test-execution-report.txt" -ForegroundColor Green

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