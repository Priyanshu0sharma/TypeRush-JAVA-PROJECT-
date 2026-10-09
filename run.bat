 @echo off
echo ========================================================
echo  Launching TypeRush - Typing Speed Tester
echo ========================================================

if not exist TypeRush.jar (
    if not exist bin\com\typerush\Main.class (
        echo [INFO] TypeRush executable not found. Running build.bat first...
        call build.bat
    )
)

if exist TypeRush.jar (
    echo [INFO] Launching packaged executable TypeRush.jar...
    java -jar TypeRush.jar
) else (
    echo [INFO] Launching from compiled binaries...
    java -cp "bin;lib/*" com.typerush.Main
)
