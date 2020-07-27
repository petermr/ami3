@echo off

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\..
set BASEDIR=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto pathSetup

:WinNTGetScriptDir
set BASEDIR=%~dp0\..

:pathSetup

powershell -executionpolicy bypass %BASEDIR%\updatepath.ps1

::set OLD_AMI_HOME=%AMI_HOME%
::
::if "%BASEDIR%" == "%OLD_AMI_HOME%" (
::    echo AMI_HOME is already set to %BASEDIR%
::    goto checkPath
::)
::if "%AMI_HOME%" == "" (
::    echo AMI_HOME was not set
::) else (
::    echo AMI_HOME was previously set to "%OLD_AMI_HOME%"
::)
::
::echo Setting AMI_HOME to %BASEDIR%
::setx AMI_HOME %BASEDIR%
::
:::checkPath
::echo %PATH%
::
::REM if PATH does not contain %AMI_HOME%\bin
::call %BASEDIR%\inPath %AMI_HOME%\bin && (goto pathAlreadySet)
::
::echo Adding AMI_HOME to the PATH...
::setx PATH "%AMI_HOME%\bin;%PATH%"
::goto done
::
:::pathAlreadySet
::echo AMI_HOME is already in the PATH
::goto done
::
:::done
::echo Done.
::echo Open a new Command Prompt window for the changes to take effect.
