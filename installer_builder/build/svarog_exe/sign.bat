@echo off
	    powershell -command "$dcs = (Get-ItemProperty -Path \"HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections\").DefaultConnectionSettings; $dcs[8] = 09; Set-ItemProperty -Path \"HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections\" -Name DefaultConnectionSettings -Value $dcs"
            echo.
            echo File: %1
            call "C:\Program Files (x86)\Microsoft Visual Studio 9.0\Common7\Tools\vsvars32.bat" > nul
            signtool verify /pa %1 2>nul
            if errorlevel 1 goto sign
            goto end
            :sign
            signtool sign /sha1 __WINDOWS_SIGNING_CERT_SHA__ /sm /t http://timestamp.verisign.com/scripts/timstamp.dll %1
            :end 
