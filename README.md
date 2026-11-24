# SSTF Disk Scheduling Simulator (Executable JAR Project)

## Included Files
- `SSTFSimulator.java` — Complete Java Swing GUI application implementing Shortest Seek Time First (SSTF) disk scheduling with step-by-step table and visual head-movement graph.
- `MANIFEST.MF` — JAR manifest specifying `Main-Class: SSTFSimulator`.
- `build_jar.bat` — One-click build script for Windows to compile and generate `SSTFSimulator.jar`.
- `build_jar.sh` — Build script for macOS/Linux to compile and generate `SSTFSimulator.jar`.

## How to Build & Run the Executable `.jar`
1. Ensure you have a Java Development Kit (JDK 11 or newer) installed (`javac -version`).
2. Run the build script (`build_jar.bat` on Windows or `./build_jar.sh` on macOS/Linux), or run manually in terminal:
   ```bash
   javac SSTFSimulator.java
   jar cfm SSTFSimulator.jar MANIFEST.MF *.class
   ```
3. Double-click `SSTFSimulator.jar` to launch the desktop GUI, or run:
   ```bash
   java -jar SSTFSimulator.jar
   ```
