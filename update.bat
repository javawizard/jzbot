
@echo off

ECHO Searching for Python in the Include Path
SET HasPython = No

REM Loop through PATH and see if there is python.exe
FOR %%P IN (%PATH%) DO IF EXIST %%P\python.exe SET HasPython = Yes

IF HasPython == No GOTO NoPython
IF HasPython == Yes ECHO Found Python

REM Run the Python script.
python update.py

GOTO End

:NoPython
ECHO Python not installed.
ECHO Get it at www.python.org
GOTO End

:End