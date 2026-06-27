#!/bin/bash

# Lokasi SDK dan build-tools
SDK_DIR="$HOME/android-sdk"
BUILD_TOOLS="30.0.3"
PLATFORM="android-30"
APK_NAME="GhostSpyController"

# Folder sumber
SRC_DIR="app/src/main/java"
RES_DIR="app/src/main/res"
MANIFEST="app/src/main/AndroidManifest.xml"

# Folder output
GEN_DIR="build/gen"
OBJ_DIR="build/obj"
DEX_DIR="build/dex"
APK_DIR="build/apk"

# Buat debug keystore jika belum ada
if [ ! -f debug.keystore ]; then
    keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US" 2>/dev/null
fi

# 1. Generate R.java
echo "Generating R.java..."
aapt package -f -m -J $GEN_DIR -M $MANIFEST -S $RES_DIR -I $SDK_DIR/platforms/$PLATFORM/android.jar

# 2. Kompilasi semua .java
echo "Compiling Java sources..."
find $SRC_DIR -name "*.java" > sources.txt
$GEN_DIR=$(ls -d $GEN_DIR/*/)
javac -d $OBJ_DIR -cp $SDK_DIR/platforms/$PLATFORM/android.jar:$GEN_DIR -source 1.8 -target 1.8 @sources.txt

# 3. Ubah class menjadi dex
echo "Creating classes.dex..."
dx --dex --output=$DEX_DIR/classes.dex $OBJ_DIR

# 4. Paketkan APK
echo "Packaging APK..."
aapt package -f -M $MANIFEST -S $RES_DIR -I $SDK_DIR/platforms/$PLATFORM/android.jar -F $APK_DIR/$APK_NAME-unsigned.apk
cd $APK_DIR
aapt add $APK_NAME-unsigned.apk classes.dex

# 5. Tandatangani APK
echo "Signing APK..."
apksigner sign --ks ../debug.keystore --ks-pass pass:android --out $APK_NAME-debug.apk $APK_NAME-unsigned.apk

echo "APK selesai: $APK_DIR/$APK_NAME-debug.apk"
