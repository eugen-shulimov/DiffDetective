@echo off
setlocal

set "targetSubPath=unparse-views"

rem Get the current directory
for %%A in ("%CD%") do set "currentDir=%%~nxA"

rem Check if the current directory ends with the target sub-path

if "%currentDir:~-9%"=="%targetSubPath%" (
    cd ..\..
    docker build -t diff-detective-unparse -f replication\unparse-views\Dockerfile .
    @pause
) else (
    echo error: the script must be run from inside the unparse-views directory, i.e., DiffDetective\replication\%targetSubPath%
)
endlocal

