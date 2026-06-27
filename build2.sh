#!/bin/bash
SDK="$HOME/android-sdk"
PLATFORM="android-30"
BT="30.0.3"

rm -rf build
mkdir -p build/gen build/obj build/apk

# 1. Generate R.java
aapt package -f -m -J build/gen -M app/src/main/AndroidManifest.xml -S app/src/main/res -I $SDK/platforms/$PLATFORM/android.jar

# 2. Compile Java
find app/src/main/java -name "*.java" > sources.txt
javac -d build/obj -cp $SDK/platforms/$PLATFORM/android.jar:build/gen -source 1.8 -target 1.8 @sources.txt

# 3. Convert to dex
dx --dex --output=build/classes.dex build/obj

# 4. Package APK
cd build
aapt package -f -M ../app/src/main/AndroidManifest.xml -S ../app/src/main/res -I $SDK/platforms/$PLATFORM/android.jar -F apk/temp.apk
aapt add apk/temp.apk classes.dex
cd ..

# 5. Sign
apksigner sign --ks ~/.keystore --ks-pass pass:android --out build/apk/GhostSpyController.apk build/apk/temp.apk

echo "APK jadi: build/apk/GhostSpyController.apk"
