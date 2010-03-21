
@echo off

rem Big, fance command to start jzbot.
set CMD=java -ea -cp classes;lib/* -Dh2.allowedClasses=jw.jzbot.PublicDatabaseUtils -Dsun.net.inetaddr.ttl=10 jw.jzbot.JZBot %*

echo.
echo Running with command %CMD%
echo.

:loop
  %CMD%
  set lastvalue=%ERRORLEVEL%
  echo Exit status was %lastvalue%
  if %lastvalue% == 17 goto loop