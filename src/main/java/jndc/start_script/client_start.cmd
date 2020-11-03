@echo off

if not defined JAVA_HOME (
  echo Error: JAVA_HOME is not set.
  pause
  goto :eof
)

set JAVA_HOME=%JAVA_HOME:"=%

if not exist "%JAVA_HOME%"\bin\java.exe (
  echo Error: JAVA_HOME is incorrectly set.
  pause
  goto :eof
)

set CONFIG=%~dp0%config.yml

if not exist %CONFIG% (
 echo Error: this file  "%CONFIG%"   not  found
  pause
  goto :eof
)

set LIB=%~dp0%jndc-1.0.jar

if not exist %LIB% (
  echo Error: this file  "%CONFIG%"   not  found
  pause
  goto :eof
)

set JAVA="%JAVA_HOME%"\bin\java

call %JAVA% -jar  %LIB% %CONFIG%  "CLIENT_APP_TYPE"
