@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"

echo ============================================================
echo   PM6-ZM-Coder 部署包打包脚本
echo ============================================================
echo.

set OUTPUT_DIR=output
set COPY_COUNT=0
set SKIP_COUNT=0

REM 确保 output 目录存在
if not exist "%OUTPUT_DIR%" (
    echo [ERROR] output 目录不存在，请先手动创建并放入 packages/、sql/、app/ 等目录
    pause
    exit /b 1
)

REM 复制部署脚本
echo [1/4] 复制部署脚本...
for %%f in (deploy.py deploy.bat deploy-config.ini) do (
    if exist "%%f" (
        copy /Y "%%f" "%OUTPUT_DIR%\%%f" >nul 2>&1
        echo   [OK] %%f
        set /a COPY_COUNT+=1
    ) else (
        echo   [SKIP] %%f (不存在)
        set /a SKIP_COUNT+=1
    )
)

REM 复制配置文件
echo [2/4] 复制配置文件...
if not exist "%OUTPUT_DIR%\config" mkdir "%OUTPUT_DIR%\config"
for %%f in (config\my.ini config\redis.conf config\mosquitto.conf config\nginx.conf config\application.yml) do (
    if exist "%%f" (
        copy /Y "%%f" "%OUTPUT_DIR%\%%f" >nul 2>&1
        echo   [OK] %%f
        set /a COPY_COUNT+=1
    ) else (
        echo   [SKIP] %%f (不存在)
        set /a SKIP_COUNT+=1
    )
)

REM 复制打包脚本自身（方便 output 目录独立分发后仍可参考）
echo [3/4] 复制打包脚本...
copy /Y "pack.bat" "%OUTPUT_DIR%\pack.bat" >nul 2>&1
echo   [OK] pack.bat

REM 验证 output 目录完整性
echo [4/4] 验证 output 目录完整性...
set VERIFY_OK=1

for %%d in (packages\jdk packages\mysql packages\redis packages\nginx packages\mosquitto packages\python sql app config) do (
    if exist "%OUTPUT_DIR%\%%d" (
        echo   [OK] %OUTPUT_DIR%\%%d
    ) else (
        echo   [MISSING] %OUTPUT_DIR%\%%d
        set VERIFY_OK=0
    )
)

for %%f in (app\ruoyi-admin.jar sql\zm.sql packages\jdk\bin\java.exe packages\mysql\bin\mysqld.exe packages\redis\redis-server.exe packages\nginx\nginx.exe packages\mosquitto\mosquitto.exe packages\python\python.exe) do (
    if exist "%OUTPUT_DIR%\%%f" (
        echo   [OK] %OUTPUT_DIR%\%%f
    ) else (
        echo   [MISSING] %OUTPUT_DIR%\%%f
        set VERIFY_OK=0
    )
)

echo.
echo ============================================================
echo   打包完成: 复制 %COPY_COUNT% 个文件, 跳过 %SKIP_COUNT% 个
if %VERIFY_OK%==1 (
    echo   验证: 所有必要文件完整
) else (
    echo   验证: 存在缺失文件，请检查上方输出
)
echo ============================================================
echo.
pause
