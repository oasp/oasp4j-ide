@echo off

if not exist O:\ (
subst O: %~dp0.
) 

if exist O:\START.bat ( 
rem All good. Starting application.
	goto :startEclipse
) else (
 	echo WARNING! WARNING! WARNING!
	echo You're currently using the local disk O:\ for another application please clear the local disk O:\  
	goto :end
)

pause

pushd O:\.
call scripts\environment-project.bat


cd workspaces\main\oasp4js


call npm install -g gulp

call npm install -g bower

call npm install

cd java


call mvn install
echo Finished installing npm and maven

:startEclipse
echo opens eclipse-workspace
call \eclipse-main.bat

:end
popd





