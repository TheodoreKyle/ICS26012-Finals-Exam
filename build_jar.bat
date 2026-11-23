@echo off
echo Compiling SSTFSimulator.java...
javac SSTFSimulator.java
if %errorlevel% neq 0 (
    echo Compilation failed! Make sure JDK is installed and javac is in PATH.
    pause
    exit /b %errorlevel%
)

echo Packaging executable JAR file (SSTFSimulator.jar)...
jar cfm SSTFSimulator.jar MANIFEST.MF *.class
if %errorlevel% neq 0 (
    echo JAR creation failed!
    pause
    exit /b %errorlevel%
)

echo Success! You can now double-click SSTFSimulator.jar or run: java -jar SSTFSimulator.jar
pause
