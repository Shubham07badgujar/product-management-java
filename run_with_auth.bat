@echo off
REM Quick Start Script for Product Management System with Authentication
REM This script helps you run the application with authentication

echo ================================================
echo  PRODUCT MANAGEMENT SYSTEM - Authentication
echo ================================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 11 or higher
    pause
    exit /b 1
)

echo [1/3] Checking Java version...
java -version
echo.

echo [2/3] Compiling application...
echo Please wait...
echo.

REM Create directories if they don't exist
if not exist "target\classes" mkdir "target\classes"

REM Compile all Java files
javac -d target\classes -cp "lib\*" ^
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
if exist "src\main\resources\database.properties" (
    copy /Y "src\main\resources\database.properties" "target\classes\" >nul 2>&1
)

echo [3/3] Starting application...
echo.
echo ================================================
echo  LAUNCHING APPLICATION...
echo ================================================
echo.
echo Note: Make sure your MySQL database is running!
echo.

REM Run the application
java -cp "target\classes;lib\*" main.MainWithAuth

echo.
echo ================================================
echo  Application closed
echo ================================================
echo.
pause
