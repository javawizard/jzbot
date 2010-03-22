
@echo off

REM Creates the folder dist with all the files needed to install JZBot.
echo This command will delete everything in your dist folder.
set /P Continue=Are you sure you want to continue? [y,n] 

if /I %Continue%==n goto eof

rmdir .\dist /S /Q
mkdir .\dist

echo Copying files needed to run install.

echo Copying svnkit.jar
copy .\lib\svnkit.jar .\dist\svnkit.jar > nul

echo Copying svnkit-cli.jar
copy .\lib\svnkit-cli.jar .\dist\svnkit-cli.jar > nul

echo Copying jsvn.bat
copy .\lib\jsvn.bat .\dist\jsvn.bat > nul

echo Copying install.bat
copy .\install.bat .\dist\install.bat > nul

echo "install" can now be run from dist to install JZBot there.

:eof