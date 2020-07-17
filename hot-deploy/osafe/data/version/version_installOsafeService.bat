@echo off

rem * JavaService installation script for Tomcat Application Server
rem *
rem * JavaService - Windows NT Service Daemon for Java applications
rem * Copyright (C) 2006 Multiplan Consultants Ltd. LGPL Licensing applies
rem * Information about the JavaService software is available at the ObjectWeb
rem * web site. Refer to http://javaservice.objectweb.org for more details.


SETLOCAL

rem verify that the JavaService exe file is available
if not exist "JavaService.exe" goto no_jsexe

rem check that Java is installed and environment variable set up
if "%JAVA_HOME%" == "" goto no_java
if not exist "%JAVA_HOME%\jre" goto no_java

set OFBIZ_HOME=@ofbiz.home@
set LOGS_DIR=%OFBIZ_HOME%\runtime\logs
set SOLR_PARMS=-Dsolr.solr.home=%OFBIZ_HOME%/hot-deploy/solr -Dsolr.data.dir=%OFBIZ_HOME%/hot-deploy/solr/data

javaservice -install "@service.name@" %JAVA_HOME%\jre\bin\server\jvm.dll -Xms@Xms@ -Xmx@Xmx@ %SOLR_PARMS% -Djava.class.path=%JAVA_HOME%\lib\tools.jar;%OFBIZ_HOME%\ofbiz.jar -start org.ofbiz.base.start.Start -out %LOGS_DIR%\serviceLog.txt -err %LOGS_DIR%\serviceErr.txt -current %OFBIZ_HOME% -manual

goto end

:no_java
@echo . JavaService installation script requires the JAVA_HOME environment variable
@echo . The Java run-time files tools.jar and jvm.dll must exist under that location
goto error_exit

:error_exit

@echo .
@echo . Failed to install @service.name@ as a system service
@echo .

:end
ENDLOCAL
@echo .
@pause