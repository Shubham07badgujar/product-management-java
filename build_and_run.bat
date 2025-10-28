@echo off
REM Build and Run Script for Product Management System
REM This script compiles and runs the application using dependencies from Maven repository

echo ================================================
echo  PRODUCT MANAGEMENT SYSTEM - BUILD AND RUN
echo ================================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 or higher
    pause
    exit /b 1
)

echo [1/4] Checking Java version...
java -version
echo.

REM Set Maven repository path
set M2_REPO=%USERPROFILE%\.m2\repository

REM Define dependency paths
set MYSQL_JAR=%M2_REPO%\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar
set MAIL_JAR=%M2_REPO%\com\sun\mail\jakarta.mail\2.0.1\jakarta.mail-2.0.1.jar
set ACTIVATION_JAR=%M2_REPO%\jakarta\activation\jakarta.activation-api\2.0.1\jakarta.activation-api-2.0.1.jar
set DOTENV_JAR=%M2_REPO%\io\github\cdimascio\dotenv-java\3.0.0\dotenv-java-3.0.0.jar

REM Build classpath
set CLASSPATH=%MYSQL_JAR%;%MAIL_JAR%;%ACTIVATION_JAR%;%DOTENV_JAR%

echo [2/4] Creating target directories...
if not exist "target\classes" mkdir "target\classes"
echo.

echo [3/4] Compiling application...
echo Please wait...
echo.

REM Compile all Java files
javac -d target\classes -cp "%CLASSPATH%" ^
    src\main\java\model\*.java ^
    src\main\java\exception\*.java ^
    src\main\java\dao\*.java ^
    src\main\java\service\*.java ^
    src\main\java\util\*.java ^
    src\main\java\main\MainWithAuth.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Please check the error messages above
    pause
    exit /b 1
)

echo.
echo ================================================
echo  Compilation successful!
echo ================================================
echo.

REM Copy resources
echo [4/4] Copying resources...
if exist "src\main\resources\database.properties" (
    copy /Y "src\main\resources\database.properties" "target\classes\" >nul 2>&1
    echo  Resource files copied
)
echo.

echo ================================================
echo  LAUNCHING APPLICATION...
echo ================================================
echo.
echo Note: Make sure your MySQL database is running!
echo       and .env file is configured properly.
echo.

REM Run the application
java -cp "target\classes;%CLASSPATH%" main.MainWithAuth

echo.
echo ================================================
echo  Application closed
echo ================================================
echo.
pause
