@echo off
setlocal
echo ========================================================
echo   TypeRush - Desktop App Installer
echo   Team ZeroX
echo ========================================================
echo.
echo Creating native desktop shortcut with TypeRush icon...

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ws = New-Object -ComObject WScript.Shell; " ^
  "$desktop = [Environment]::GetFolderPath('Desktop'); " ^
  "$appDir = '%~dp0'.TrimEnd('\'); " ^
  "$exePath = Join-Path $appDir 'TypeRush.exe'; " ^
  "$icoPath = Join-Path $appDir 'assets\icon.ico'; " ^
  "$sc = $ws.CreateShortcut((Join-Path $desktop 'TypeRush.lnk')); " ^
  "if (Test-Path $exePath) { $sc.TargetPath = $exePath; } else { $sc.TargetPath = (Join-Path $appDir 'TypeRush.vbs'); } " ^
  "$sc.WorkingDirectory = $appDir; " ^
  "$sc.Description = 'TypeRush - High Performance Typing Speed Tester by Team ZeroX'; " ^
  "if (Test-Path $icoPath) { $sc.IconLocation = \"$icoPath,0\"; } " ^
  "$sc.Save(); " ^
  "Write-Host '[SUCCESS] TypeRush desktop shortcut created with official logo icon!' -ForegroundColor Green"

echo.
echo TypeRush has been added to your Windows Desktop!
echo ========================================================
pause
