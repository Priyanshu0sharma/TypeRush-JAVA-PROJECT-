$ErrorActionPreference = "Stop"

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Building TypeRush - Typing Speed Tester" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

$javac = if (Test-Path "C:\Program Files\Java\jdk-17\bin\javac.exe") { "C:\Program Files\Java\jdk-17\bin\javac.exe" } else { "javac" }
$jar   = if (Test-Path "C:\Program Files\Java\jdk-17\bin\jar.exe") { "C:\Program Files\Java\jdk-17\bin\jar.exe" } else { "jar" }

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "[1/3] Compiling Java source files with UTF-8 encoding..." -ForegroundColor Yellow
$sources = (Get-ChildItem -Recurse -Filter "*.java" src\com\typerush | Select-Object -ExpandProperty FullName)
& $javac -encoding UTF-8 -cp "lib/*;src" -d bin $sources
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed." -ForegroundColor Red
    exit 1
}

Write-Host "[2/3] Compilation successful!" -ForegroundColor Green

Write-Host "[3/3] Creating fat runnable TypeRush.jar with bundled dependencies..." -ForegroundColor Yellow
if (Test-Path "build_jar") {
    Remove-Item -Recurse -Force "build_jar"
}
New-Item -ItemType Directory -Path "build_jar" | Out-Null
Copy-Item -Recurse -Force "bin\*" "build_jar\"
if (Test-Path "assets") {
    Copy-Item -Recurse -Force "assets" "build_jar\"
}

Push-Location "build_jar"
try {
    & $jar -xf "..\lib\mysql-connector-j-8.3.0.jar"
} finally {
    Pop-Location
}

$manifestContent = "Manifest-Version: 1.0`r`nMain-Class: com.typerush.Main`r`n"
[System.IO.File]::WriteAllText("$PSScriptRoot\manifest.txt", $manifestContent)

& $jar -cfm "TypeRush.jar" "manifest.txt" -C "build_jar" "."

if (Test-Path "manifest.txt") { Remove-Item -Force "manifest.txt" }
if (Test-Path "build_jar") { Remove-Item -Recurse -Force "build_jar" }

Write-Host "========================================================" -ForegroundColor Green
Write-Host "[SUCCESS] TypeRush built successfully!" -ForegroundColor Green
Write-Host "Executable JAR: TypeRush.jar ($((Get-Item 'TypeRush.jar').Length / 1KB) KB)" -ForegroundColor Cyan
Write-Host "Launch with:   run.bat or java -jar TypeRush.jar" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Green
