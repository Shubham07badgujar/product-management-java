@echo off
REM =====================================================
REM Email Credentials Setup Script
REM =====================================================
REM This script helps you set up email credentials for
REM email verification functionality
REM =====================================================

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     EMAIL CREDENTIALS SETUP                                  ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

echo This script will help you configure email settings for the
echo Product Management System's email verification feature.
echo.

:menu
echo Please select an option:
echo.
echo [1] Set up environment variables (Permanent - System-wide)
echo [2] Set up for current session only (Temporary)
echo [3] View current settings
echo [4] Test email configuration
echo [5] View Gmail App Password instructions
echo [6] Exit
echo.
set /p choice="Enter your choice (1-6): "

if "%choice%"=="1" goto permanent_setup
if "%choice%"=="2" goto temporary_setup
if "%choice%"=="3" goto view_settings
if "%choice%"=="4" goto test_config
if "%choice%"=="5" goto show_instructions
if "%choice%"=="6" goto end
echo Invalid choice! Please try again.
echo.
goto menu

:permanent_setup
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     PERMANENT SETUP (System Environment Variables)           ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.
echo This will set system-wide environment variables.
echo You'll need to RESTART your terminal/IDE after this.
echo.
set /p confirm="Continue? (yes/no): "
if not "%confirm%"=="yes" goto menu

echo.
set /p mail_user="Enter your Gmail address: "
if "%mail_user%"=="" (
    echo ❌ Email address cannot be empty!
    pause
    goto menu
)

echo.
echo ⚠️  IMPORTANT: You need a Gmail App Password, not your regular password!
echo    If you don't have one, select option [5] for instructions.
echo.
set /p mail_pass="Enter your Gmail App Password (16 characters): "
if "%mail_pass%"=="" (
    echo ❌ App password cannot be empty!
    pause
    goto menu
)

echo.
echo Setting environment variables...
setx MAIL_USER "%mail_user%"
setx MAIL_PASS "%mail_pass%"

if %errorlevel% == 0 (
    echo.
    echo ✅ Environment variables set successfully!
    echo.
    echo ⚠️  IMPORTANT: You MUST restart your terminal/IDE for changes to take effect!
    echo.
    echo Current values:
    echo    MAIL_USER: %mail_user%
    echo    MAIL_PASS: **************** (hidden)
    echo.
) else (
    echo.
    echo ❌ Failed to set environment variables!
    echo Please run this script as Administrator.
    echo.
)
pause
goto menu

:temporary_setup
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     TEMPORARY SETUP (Current Session Only)                   ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.
echo This will set environment variables for this PowerShell session only.
echo They will be lost when you close the terminal.
echo.

set /p mail_user="Enter your Gmail address: "
if "%mail_user%"=="" (
    echo ❌ Email address cannot be empty!
    pause
    goto menu
)

echo.
echo ⚠️  IMPORTANT: You need a Gmail App Password, not your regular password!
echo.
set /p mail_pass="Enter your Gmail App Password (16 characters): "
if "%mail_pass%"=="" (
    echo ❌ App password cannot be empty!
    pause
    goto menu
)

echo.
echo Setting temporary environment variables...
set MAIL_USER=%mail_user%
set MAIL_PASS=%mail_pass%

echo.
echo ✅ Temporary environment variables set for this session!
echo.
echo Current values:
echo    MAIL_USER: %mail_user%
echo    MAIL_PASS: **************** (hidden)
echo.
echo ⚠️  These will be lost when you close this terminal!
echo    Run your application from THIS terminal window now.
echo.
pause
goto menu

:view_settings
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     CURRENT EMAIL SETTINGS                                   ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

if defined MAIL_USER (
    echo ✅ MAIL_USER is set: %MAIL_USER%
) else (
    echo ❌ MAIL_USER is NOT set
)

if defined MAIL_PASS (
    echo ✅ MAIL_PASS is set: **************** (hidden for security)
) else (
    echo ❌ MAIL_PASS is NOT set
)

echo.
if defined MAIL_USER (
    if defined MAIL_PASS (
        echo ✅ Email configuration appears to be complete!
        echo    You can try sending a verification email.
    ) else (
        echo ⚠️  MAIL_PASS is missing! Email verification will not work.
    )
) else (
    echo ⚠️  Email credentials are not configured!
    echo    Email verification will not work until you set them up.
)
echo.
pause
goto menu

:test_config
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     TEST EMAIL CONFIGURATION                                 ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

if not defined MAIL_USER (
    echo ❌ MAIL_USER is not set! Cannot test.
    echo    Please set up email credentials first (Option 1 or 2).
    pause
    goto menu
)

if not defined MAIL_PASS (
    echo ❌ MAIL_PASS is not set! Cannot test.
    echo    Please set up email credentials first (Option 1 or 2).
    pause
    goto menu
)

echo Current configuration:
echo    MAIL_USER: %MAIL_USER%
echo    MAIL_PASS: **************** (hidden)
echo.
echo To test email verification:
echo    1. Run: run_with_auth.bat
echo    2. Choose option [3] Verify Mail
echo    3. Enter your registered email address
echo    4. Check your inbox for the OTP
echo.
pause
goto menu

:show_instructions
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     HOW TO GET GMAIL APP PASSWORD                            ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.
echo ⚠️  You CANNOT use your regular Gmail password!
echo    You must create a Gmail App Password.
echo.
echo Step 1: Enable 2-Step Verification
echo    → Go to: https://myaccount.google.com/security
echo    → Click "2-Step Verification"
echo    → Follow the setup process
echo.
echo Step 2: Generate App Password
echo    → Go to: https://myaccount.google.com/apppasswords
echo    → Or search "App passwords" in Google Account settings
echo    → Select app: "Mail"
echo    → Select device: "Windows Computer"
echo    → Click "Generate"
echo.
echo Step 3: Copy the 16-character password
echo    → Example: "abcd efgh ijkl mnop"
echo    → Remove spaces: "abcdefghijklmnop"
echo    → Use this as your MAIL_PASS value
echo.
echo Step 4: Return to this script and select option [1] or [2]
echo.
echo 🔗 Direct links:
echo    Google Security: https://myaccount.google.com/security
echo    App Passwords: https://myaccount.google.com/apppasswords
echo.
pause
goto menu

:end
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     EMAIL SETUP COMPLETE                                     ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.
if defined MAIL_USER (
    if defined MAIL_PASS (
        echo ✅ Email credentials are configured!
        echo    You can now use email verification in your application.
        echo.
        echo Remember to:
        echo    - Restart your terminal/IDE if you used option [1]
        echo    - Run application from this terminal if you used option [2]
    ) else (
        echo ⚠️  Email credentials are incomplete!
    )
) else (
    echo ⚠️  Email credentials are not configured yet!
)
echo.
echo For detailed instructions, see: EMAIL_SETUP_GUIDE.md
echo.
pause
exit /b 0
