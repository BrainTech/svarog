@echo off
echo.
echo File: %1
call "C:\Program Files (x86)\Microsoft Visual Studio 9.0\Common7\Tools\vsvars32.bat" > nul
signtool verify /pa %1 2>nul
if errorlevel 1 goto sign
goto end
:sign
signtool sign /sha1 __WINDOWS_SIGNING_CERT_SHA__ /sm /t  http://timestamp.globalsign.com/scripts/timestamp.dll %1
:end
