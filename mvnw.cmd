@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------
@echo off

SET MAVEN_PROJECTBASEDIR=%~dp0

IF "%JAVA_HOME%"=="" (
  echo Error: JAVA_HOME is not set. Please set JAVA_HOME to your JDK directory.
  exit /b 1
)

SET JAVA_EXE=%JAVA_HOME%\bin\java.exe

IF NOT EXIST "%JAVA_EXE%" (
  echo Error: Java executable not found: %JAVA_EXE%
  exit /b 1
)

SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar
SET WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties

"%JAVA_EXE%" ^
  -classpath "%WRAPPER_JAR%" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  "-Dmaven.wrapper.properties=%WRAPPER_PROPERTIES%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*

IF ERRORLEVEL 1 exit /b 1
