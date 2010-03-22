
@echo off

REM Simple script for installing JZBot
REM Required files are:
REM   svnkit.jar
REM   svnkit-cli.jar
REM   jsvn.bat

SET PATH=%PATH%;%CD%\ant\bin
mkdir .\InstallationLogs

set SVNRepo=http://jwutils.googlecode.com/svn/trunk/projects/jzbot2-old/

echo.
echo Checking out JZBot code from %SVNRepo%
call jsvn co %SVNRepo% . > InstallationLogs\SVNCheckout.log

echo.
echo Deleting Installation Files
echo   Deleting svnkit.jar
del svnkit.jar
echo   Deleting svnkit-cli.jar
del svnkit-cli.jar
echo   Deleting jsvn.bat
del jsvn.bat

echo.
echo Building JZBot
call build > InstallationLogs\Build.log

findstr "BUILD FAILED" InstallationLogs\Build.log
if errorlevel 1 goto BuildFailed

echo.
echo == JZBot Configuration ==
echo You'll need to specify your hostname as the superuser. You can find that by
echo joining Freenode on IRC and typing "/whois [your nick]". The string after
echo the @ is your hostname.
SET /P HostName=Your Freenode Hostname: 
SET /P Nick=JZBot Nickname: 
echo.
echo Configuring JZBot to join Freenode as %Nick%
call jzbot addserver freenode irc irc.freenode.net 6667 %Nick% %HostName% > InstallationLogs\JZBotConfigure.log

echo.
echo JZBot will now join Freenode in a few seconds. You may then configure it via
echo /msg %Nick% help
echo If JZBot fails to start, join #jzbot and tell the JZBot developers because
echo something failed and needs fixing. Be prepared to share the InstallationLogs
echo directory of your new JZBot installation to help track down the problem.

start jzbot
pause

:BuildFailed
echo ERROR: Build Failed
echo.
goto PrintError

:PrintError
echo Contact the JZBot developers on #jzbot (freenode)
echo This problem is serious and needs to be fixed right away.
echo Be prepared to share the InstallationLogs directory of
echo your new JZBot installation to help fix the problem.
pause
goto eof

:eof