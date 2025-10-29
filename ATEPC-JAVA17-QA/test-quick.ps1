# EOM QA - Simplified Test Execution Script
param(
    [string]$TestSuite = "unit"
)

Write-Host "=============================================" -ForegroundColor Magenta
Write-Host "    EOM QA AUTOMATION - TEST EXECUTION      " -ForegroundColor Green  
Write-Host "=============================================" -ForegroundColor Magenta

$StartTime = Get-Date
Write-Host "🔧 Target Test Suite: $TestSuite" -ForegroundColor Cyan

# Create reports directory for test output
$ReportsDir = ".\reports"
if (-not (Test-Path $ReportsDir)) {
    New-Item -ItemType Directory -Path $ReportsDir -Force | Out-Null
    Write-Host "📁 Test reports directory created successfully" -ForegroundColor Green
}

# Execute test suite
Write-Host "`n🧪 Executing Test Suite..." -ForegroundColor Cyan

$testsPassed = 0
$testsFailed = 0

try {
    Write-Host "   📝 Executing Unit Tests..." -ForegroundColor Yellow
    
    # Compile application and test sources
    mvn compile test-compile -q
    
    # Execute unit tests (excluding Selenium integration tests)
    mvn test -Dtest="!com.example.demo.Selenium" -q
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Unit tests executed successfully" -ForegroundColor Green
        $testsPassed++
    } else {
        Write-Host "   ❌ Unit tests execution failed" -ForegroundColor Red
        $testsFailed++
    }
} catch {
    Write-Host "   ❌ Test execution error encountered: $($_.Exception.Message)" -ForegroundColor Red
    $testsFailed++
}

# Collect and copy test results
if (Test-Path "target\surefire-reports") {
    Copy-Item "target\surefire-reports\*" $ReportsDir -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "📄 Test reports copied successfully to $ReportsDir" -ForegroundColor Green
}

# Generate execution summary
$EndTime = Get-Date  
$Duration = $EndTime - $StartTime

Write-Host "`n" + "="*50 -ForegroundColor Magenta
Write-Host "    TEST EXECUTION COMPLETED    " -ForegroundColor Green
Write-Host "="*50 -ForegroundColor Magenta

if ($testsFailed -eq 0) {
    Write-Host "🎉 SUCCESS: All test suites executed successfully!" -ForegroundColor Green
} else {
    Write-Host "❌ FAILED: $testsFailed test suite(s) failed execution" -ForegroundColor Red
}

Write-Host "⏱️ Total Execution Duration: $($Duration.ToString('mm\:ss'))" -ForegroundColor Cyan
Write-Host ""

exit $testsFailed