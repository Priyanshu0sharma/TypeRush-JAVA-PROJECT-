# TypeRush - Automated Standalone Release Packager
$ErrorActionPreference = "Stop"

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Packaging TypeRush for Release Distribution" -ForegroundColor Cyan
Write-Host "  Team: ZeroX" -ForegroundColor Yellow
Write-Host "========================================================" -ForegroundColor Cyan

# 1. Build fresh JAR if needed
Write-Host "[1/3] Building executable standalone fat JAR..." -ForegroundColor Yellow
& powershell -ExecutionPolicy Bypass -File "$PSScriptRoot\build.ps1"

# 2. Prepare Distribution Directory
$distDir = "$PSScriptRoot\dist\TypeRush-v1.0-Windows"
if (Test-Path "$PSScriptRoot\dist") {
    Remove-Item -Recurse -Force "$PSScriptRoot\dist"
}
New-Item -ItemType Directory -Path $distDir | Out-Null

Write-Host "[2/3] Collecting runtime files and launchers..." -ForegroundColor Yellow
# Recompile TypeRush.exe if needed
& 'C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe' /target:winexe /win32icon:"$PSScriptRoot\assets\icon.ico" /out:"$PSScriptRoot\TypeRush.exe" "$PSScriptRoot\tools\TypeRushLauncher.cs" | Out-Null

Copy-Item "$PSScriptRoot\TypeRush.exe" "$distDir\"
Copy-Item "$PSScriptRoot\TypeRush.jar" "$distDir\"
Copy-Item "$PSScriptRoot\run.bat" "$distDir\"
Copy-Item "$PSScriptRoot\TypeRush.vbs" "$distDir\"
Copy-Item "$PSScriptRoot\Install-Desktop-Shortcut.bat" "$distDir\"

if (Test-Path "$PSScriptRoot\assets") {
    Copy-Item -Recurse "$PSScriptRoot\assets" "$distDir\"
}

if (Test-Path "$PSScriptRoot\lib") {
    Copy-Item -Recurse "$PSScriptRoot\lib" "$distDir\"
}
if (Test-Path "$PSScriptRoot\database") {
    Copy-Item -Recurse "$PSScriptRoot\database" "$distDir\"
}

# Create Quickstart Readme inside distribution
$readmeContent = @"
========================================================================
⚡ TypeRush - Desktop Typing Speed Tester
Developed by Team ZeroX
GitHub: https://github.com/Priyanshu0sharma/TypeRush-JAVA-PROJECT-
========================================================================

How to Run:
1. Double-click 'TypeRush.vbs' to run silently with NO command prompt.
2. Or double-click 'run.bat' to run via console.
3. Or run 'Install-Desktop-Shortcut.bat' to create a desktop shortcut icon.

Prerequisites:
- Java JDK or JRE 17+ installed on your computer.
- Download Java for free at: https://www.oracle.com/java/technologies/downloads/

Team ZeroX Members:
1. Priyanshu Sharma (Team Lead) - UI/UX & System Integration
2. Kumawat Yogesh Dinesh - Typing Engine & Simulation
3. Jatin Yadav - Mathematical Calculations & Telemetry
4. Mukesh - Database Architecture & History CRUD
5. Prashant Soni - Security, Roles & Administration
========================================================================
"@
[System.IO.File]::WriteAllText("$distDir\README.txt", $readmeContent)

# 3. Zip into release package
Write-Host "[3/3] Compressing into distributable ZIP package..." -ForegroundColor Yellow
$zipPath = "$PSScriptRoot\dist\TypeRush-v1.0-Windows.zip"
Compress-Archive -Path "$distDir\*" -DestinationPath $zipPath -Force

Write-Host "========================================================" -ForegroundColor Green
Write-Host "[SUCCESS] Distribution package created successfully!" -ForegroundColor Green
Write-Host "ZIP Location:  $zipPath" -ForegroundColor Cyan
Write-Host "Folder Location: $distDir" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Green
