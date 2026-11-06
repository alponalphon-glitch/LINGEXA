# Build LINGEXA Android APK

The project is an Android application (`com.lingexa.app`). Minimum Android version: Android 8.0 (API 26). Target SDK: 35.

## Build from GitHub

1. Upload/push the complete project folder to a GitHub repository.
2. Open **Actions** in that repository.
3. Select **Build LINGEXA Android APK**.
4. Press **Run workflow** (or push a change to `main`/`master`).
5. When the workflow succeeds, open its run and download the artifact named `LINGEXA-Android-1.36.0-debug`.
6. Extract the artifact; install the APK on an Android device. If Android asks, allow installation from that source.

The workflow builds a debug APK for testing. A signed release APK/AAB for Google Play requires a release keystore and signing configuration; do not publish the debug APK as a production release.
