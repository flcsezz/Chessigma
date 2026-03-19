#!/bin/bash
set -ex
cd /home/flcsezz/Chessigma
mkdir -p tmp_wrapper
echo "plugins { }" > tmp_wrapper/build.gradle.kts
cd tmp_wrapper
wget -q https://services.gradle.org/distributions/gradle-8.7-bin.zip
unzip -q gradle-8.7-bin.zip
./gradle-8.7/bin/gradle wrapper --gradle-version 8.7
mv gradlew ../
mkdir -p ../gradle/wrapper || true
mv gradle/wrapper/gradle-wrapper.jar ../gradle/wrapper/
mv gradle/wrapper/gradle-wrapper.properties ../gradle/wrapper/
cd ..
chmod +x gradlew
rm -rf tmp_wrapper
./gradlew assembleDebug > build_output.txt 2>&1
