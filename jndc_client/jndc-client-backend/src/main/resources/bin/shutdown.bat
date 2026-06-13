@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
call "%SCRIPT_DIR%jndc.bat" stop %*
exit /b %ERRORLEVEL%
