@echo off
rem Change to the project directory where the batch file is located
cd /d "%~dp0"

rem Set the classpath to include the current directory, the bin folder, and the MySQL JDBC driver relative to the batch file location
set CLASSPATH=.;ocsf.jar;mysql-connector-java-5.1.40-bin.jar

rem Print the classpath for debugging
echo Current classpath: %CLASSPATH%

rem Run the Java program
java -cp %CLASSPATH% sweProject.DatabaseServer sweProject.DatabaseServer

rem Pause to view output
pause