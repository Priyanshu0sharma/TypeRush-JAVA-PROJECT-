@echo off
setlocal
powershell -ExecutionPolicy Bypass -NoProfile -File "%~dp0build.ps1"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Build failed.
    exit /b %ERRORLEVEL%
)
