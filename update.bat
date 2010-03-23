
@echo off

REM Runs jsvn then creates the restart file.

set PATH=%PATH%;%CD%\lib

echo Updating to the latest version of JZBot...
call jsvn up

echo Restart! > storage\restart

echo Updates have completed successfully. If your bot
echo is currently running, it will be restarted momentarily.