#!/usr/bin/env bash
set -e
echo "Compiling SSTFSimulator.java..."
javac SSTFSimulator.java

echo "Packaging executable JAR file (SSTFSimulator.jar)..."
jar cfm SSTFSimulator.jar MANIFEST.MF *.class

echo "Success! Run with: java -jar SSTFSimulator.jar"
