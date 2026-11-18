
# JoinMe | Build and run on Android

Open the folder containing `settings.gradle.kts` in Android Studio or IntelliJ IDEA with Android support. Use the Gradle wrapper and sync the project. The first build needs internet access to download dependencies.

| Setting | Value |
|---|---|
| Gradle / JDK | Wrapper 9.5.1 with JDK 21 |
| Kotlin / Compose Multiplatform | 2.4.20 / 1.12.1 |
| Compile / target / minimum Android API | 37 / 37 / 24 |
| Demo device | Medium Phone API 36.1 emulator |

Install Android SDK Platform 37, SDK Build-Tools, Platform-Tools and the Android Emulator in SDK Manager. Create and start an emulator in Device Manager. The IDE normally writes the local SDK path to `local.properties`. If needed, set `ANDROID_HOME` to the SDK folder in the terminal.

## Run from the IDE

Select the **Android App** configuration for `androidApp`, choose the running emulator or connected phone and press **Run**. The IDE builds, installs and opens JoinMe.

## Build, install or test from PowerShell

Run these commands from the repository root.

```powershell
.\gradlew.bat :androidApp:assembleDebug
.\gradlew.bat :androidApp:installDebug
.\gradlew.bat :shared:testAndroidHostTest
.\gradlew.bat :androidApp:connectedDebugAndroidTest
```

Use `./gradlew` on macOS or Linux. The last command runs the Android response tests on a connected emulator or device.

The APK is at `androidApp/build/outputs/apk/debug/androidApp-debug.apk`. You can also install the supplied debug APK directly with `adb install -r JoinMe-M2.apk`. To show persistence, force stop the app and reopen it. Home alone may leave the process running. See the [M2 demo steps](docs/m2/demo.md).

The optional iOS prototype needs a Mac and Xcode. M2 is only assessed and tested on Android.
