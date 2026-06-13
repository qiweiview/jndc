@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
call "%SCRIPT_DIR%jndc.bat" start %*
exit /b %ERRORLEVEL%
