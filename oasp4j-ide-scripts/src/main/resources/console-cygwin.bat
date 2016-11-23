@echo off

pushd %~dp0

rem sets the required environment variables like JAVA_HOME, M2_REPO...
call scripts\environment-project.bat

popd

#start C:\cygwin64\bin\mintty.exe -i /Cygwin-Terminal.ico -
start C:\cygwin64\bin\mintty.exe -i /Cygwin-Terminal.ico /bin/env CHERE_INVOKING=1 TERMINAL_TITLE=[PNR] /bin/bash -l