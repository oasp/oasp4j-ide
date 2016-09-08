@echo OFF
echo This is the installation report. >> install.log
echo Rerun all tasks in Eclipse (Help > Perform Setup Tasks) that are listed below > install.log
echo ----------Begin------------ >> install.log
IF NOT EXIST update-all-workspaces.bat (
	echo OASP files are missing >> install.log
	IF NOT EXIST temp\oasp.zip (
		echo   rerun download.oasp >> install.log
		)
	IF NOT EXIST temp\oasp4j-ide-master (
		echo   rerun unzip.oasp >> install.log
		)
	echo   rerun xcopy.oasp.scripts >> install.log
	)

IF NOT EXIST workspaces\main\development\settings\ide-properties.bat (
	echo OASP files are missing >> install.log
	IF NOT EXIST temp\oasp.zip (
		echo   rerun download.oasp >> install.log
		)
	IF NOT EXIST temp\oasp4j-ide-master (
		echo   rerun unzip.oasp >> install.log
		)
	IF NOT EXIST workspaces\main\development\settings\ (
		echo   rerun mkdir.oasp.settings >> install.log
		)
	echo   rerun move.oasp.settings >> install.log
	)

IF NOT EXIST software\ant\ (
	echo Ant is missing >> install.log
	IF NOT EXIST temp\ant.zip (
		echo   rerun download.ant >> install.log
		)
	echo   rerun unzip.ant >> install.log
	echo   rerun rename.ant >> install.log
	)
IF NOT EXIST software\elasticsearch\ (
	echo Elasticsearch is missing >> install.log
	IF NOT EXIST temp\elasticsearch.zip (
		echo   rerun download.elasticsearch >> install.log
		)
	echo   rerun unzip.elasticsearch >> install.log
	echo   rerun rename.elasticsearch >> install.log
	)
IF NOT EXIST software\logstash\ (
	echo Logstash is missing >> install.log
	IF NOT EXIST temp\logstash.zip (
		echo   rerun download.logstash >> install.log
		)
	echo   rerun unzip.logstash >> install.log
	echo   rerun rename.log >> install.log
	)
IF NOT EXIST software\maven\ (
	echo maven is missing >> install.log
	IF NOT EXIST temp\maven.zip (
		echo   rerun download.maven >> install.log
		)
	echo   rerun unzip.maven >> install.log
	echo   rerun rename.maven >> install.log
	)
IF NOT EXIST software\tomcat\ (
	echo tomcat is missing >> install.log
	IF NOT EXIST temp\tomcat.zip (
		echo   rerun download.tomcat >> install.log
		)
	echo   rerun unzip.tomcat >> install.log
	echo   rerun rename.tomcat >> install.log
	)
IF NOT EXIST software\nodejs\ (
	echo nodejs is missing >> install.log
	IF NOT EXIST temp\nodejs.exe (
		echo   rerun download.nodejs >> install.log
		)
	echo   rerun run.nodejs >> install.log
	) 
IF NOT EXIST software\nodejs\node_modules\ (
	echo nodejs restructuring failed >> install.log
	echo   rerun reorder.nodejs.1 >> install.log
	)
IF NOT EXIST software\nodejs\node.exe (
	echo nodejs restructuring failed >> install.log
	echo   rerun reorder.nodejs.2 >> install.log
	)
IF NOT EXIST software\python\ (
	echo python is missing >> install.log
	IF NOT EXIST temp\python.exe (
		echo   rerun download.python >> install.log
		)
	echo   rerun run.python >> install.log
	) 
IF NOT EXIST software\java\ (
	echo Java is missing >> install.log
	echo   rerun xcopy.java >> install.log
	)
echo -----------End------------- >> install.log
echo When there is no entry between the two lines your installation is probably complete >> install.log 