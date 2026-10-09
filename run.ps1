Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Launching TypeRush - Typing Speed Tester" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

if (-not (Test-Path "TypeRush.jar")) {
    Write-Host "TypeRush.jar not found. Building project first..." -ForegroundColor Yellow
    & "$PSScriptRoot\build.ps1"
}

Write-Host "Starting TypeRush..." -ForegroundColor Green
java -jar "TypeRush.jar"
