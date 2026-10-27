@echo off
setlocal
set "JAVA_HOME=C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot"
set "MAVEN_HOME=C:\maven"
"%MAVEN_HOME%\bin\mvn.cmd" %*
exit /b %ERRORLEVEL%
