@echo off
echo Compiling Java project...
javac -cp "lib\mysql-connector-j-8.0.33.jar;." -d target\classes src\main\java\util\*.java src\main\java\exception\*.java src\main\java\model\Product.java src\main\java\dao\ProductDao.java src\main\java\dao\ProductDaoImpl.java src\main\java\service\ProductService.java src\main\java\main\Main.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful! Running application...
    echo.
    java -cp "lib\mysql-connector-j-8.0.33.jar;target\classes" main.Main
) else (
    echo Compilation failed!
    pause
)