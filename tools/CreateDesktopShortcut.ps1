$ws = New-Object -ComObject WScript.Shell
$desktop = [Environment]::GetFolderPath('Desktop')
$appDir = (Get-Item $PSScriptRoot\..).FullName
$exePath = Join-Path $appDir 'TypeRush.exe'
$icoPath = Join-Path $appDir 'assets\icon.ico'
$lnkPath = Join-Path $desktop 'TypeRush.lnk'

$sc = $ws.CreateShortcut($lnkPath)
$sc.TargetPath = $exePath
$sc.WorkingDirectory = $appDir
$sc.Description = 'TypeRush - High Performance Typing Speed Tester by Team ZeroX'
$sc.IconLocation = "$icoPath,0"
$sc.Save()

Write-Host "[SUCCESS] TypeRush desktop shortcut created at: $lnkPath"
if (Test-Path $lnkPath) {
    Write-Host "[VERIFIED] Desktop shortcut exists and is ready to launch!" -ForegroundColor Green
}
