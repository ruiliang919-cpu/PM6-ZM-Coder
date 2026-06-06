@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"

if not exist "packages\python\python.exe" (
    echo [ERROR] Embedded Python not found, package is incomplete.
    pause
    exit /b 1
)

"packages\python\python.exe" "deploy.py" %*

if %errorlevel% neq 0 (
    echo.
    echo [Deploy exited with error code: %errorlevel%]
    pause
)
