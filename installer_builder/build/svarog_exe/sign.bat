@echo off
            echo.
            echo File: %1
            call "C:\Program Files (x86)\Microsoft Visual Studio 9.0\Common7\Tools\vsvars32.bat" > nul
            signtool verify /pa %1 2>nul
            if errorlevel 1 goto sign
            goto end
            :sign
            signtool sign /sha1 39bef11ce57cbc35300408c82a675c0ac3200483 /sm /t http://timestamp.verisign.com/scripts/timstamp.dll %1
            :end 
