# JoinMe — Setup and demo guide

Run commands from the folder containing `settings.gradle.kts`.

## 1. Tools and versions

Use an Android development environment with SDK Manager and Device Manager. The team uses IntelliJ IDEA with Android support. Android Studio is another option for the Android target.

| Setting | Value in this project |
|---|---|
| Gradle wrapper | 9.5.1 |
| Gradle daemon JDK | Azul JDK 21, selected in `gradle/gradle-daemon-jvm.properties` |
| Kotlin / Compose Multiplatform | 2.4.20 / 1.12.1 |
| Android Gradle Plugin | 9.1.1 |
| Compile / target / minimum SDK | API 37 / API 37 / API 24 |
| Existing screenshot device | Pixel 8 emulator, Android 16 (API 36) |

Versions are in `gradle/libs.versions.toml`. The first build needs internet access for downloads. Use the included Gradle wrapper.

## 2. Open and configure the project

Clone if needed:

```powershell
git clone https://github.com/nicolasezequielmaqueda/JoinMe.git
cd JoinMe
```

Open this folder in the IDE. In SDK Manager, install API 37, Android SDK Build-Tools, Platform-Tools and Android Emulator. In Device Manager, create a Pixel 8 with Android 16 / API 36. Choose an image matching your computer's processor.

Select JDK 21 in the IDE's Gradle settings and sync. The included daemon settings select Azul JDK 21. If its download fails, check the error and network connection.

The original notes reported a startup problem with one API 37 emulator image. This does not mean all API 37 images fail. API 36 was used for our existing screenshots.

## 3. Set the SDK path

The IDE may create `local.properties`. Otherwise, create it in the project root using your real SDK path. Windows example:

```properties
sdk.dir=C:/Users/YOUR_USER/AppData/Local/Android/Sdk
```

On macOS or Linux, use the path shown in SDK Manager. Git ignores this file because each computer can use a different path. You can also set `ANDROID_HOME` in the terminal running Gradle. Remember that a setting made in one terminal may not be available in another.

## 4. Build and run

Start the emulator. Select an Android App run configuration for `androidApp` and press Run. The exact module label depends on the IDE.

Build from PowerShell:

```powershell
.\gradlew.bat :androidApp:assembleDebug
```

APK output: `androidApp/build/outputs/apk/debug/androidApp-debug.apk`.

Install on a running emulator or connected device:

```powershell
.\gradlew.bat :androidApp:installDebug
```

Open JoinMe from the device's app list. On macOS or Linux, use `./gradlew`. If needed, run `chmod +x gradlew` first.

## 5. Demo steps

Start with a fresh app process to reset the sample data. Returning to the home screen may leave it running. Use Force stop in Android's app settings if necessary, then reopen JoinMe.

| Action | Expected result |
|---|---|
| Open Table Tennis | Today at 23:00, Games Room, 1 of 2 people, OPEN. |
| Press Join activity | 2 of 2, FULL and JOINED; Leave activity is available. |
| Go back, then open My Activities | Table Tennis appears under Joined. |
| Leave Table Tennis | The count returns to 1 of 2 and OPEN. |
| Press New and complete the form | A future time and required fields enable Create activity. |
| Create the activity | Its details open; it appears under Created by you. |
| Cancel Board Games and confirm | It appears in My Activities → Past as CANCELLED. |

M1 data resets after a process restart. This is a known prototype limit, not the intended final behaviour.

## 6. Automated tests

```powershell
.\gradlew.bat :shared:testAndroidHostTest
```

There are 14 tests in `ActivityRulesTest` and `ActivityValidatorTest`. The report is in `shared/build/reports/tests/testAndroidHostTest/index.html`. These check rules and validation, not visual layout.

## 7. Accessibility checks

Set Android's Font size to maximum. Repeat the join task and create an activity. Check for overlapping text, unreadable information or actions you cannot reach. Scrolling is acceptable.

For the colour requirement, keep the normal display settings and manually check the labels:

- Open Table Tennis: identify OPEN and the Join activity action from their text.
- Join Table Tennis: identify FULL, JOINED and Leave activity from their text. Pool also provides a FULL example.
- Open My Activities → Past: identify COMPLETED on Darts.
- Cancel Board Games and open it under Past: identify CANCELLED.

Pass this check only if each status can be explained using its visible text, without referring to its colour, and the main actions have clear labels. Record any missing, hidden or unclear label. A greyscale filter is optional for this check.

Menu names depend on the Android version. Record the device, version, settings, result and any problem. Save useful screenshots. The team reports that maximum font size works well on the Medium Phone API 36.1 emulator (22 October 2026). On the same date and emulator, the team confirmed that OPEN, FULL, JOINED, CANCELLED and COMPLETED and the main action labels are visible and understandable without relying on colour. TalkBack and control-size checks are additional work. See the [supporting notes](docs/m1/M1-details.md).

## 8. Optional iOS build

The iOS project has not been verified in this Windows review. On a Mac with Xcode, open `iosApp/iosApp.xcodeproj` and select an iPhone simulator. For a physical iPhone, configure signing and the team ID in `iosApp/Configuration/Config.xcconfig`. Test it before claiming that iOS works.

## 9. Common problems

| Problem | What to check |
|---|---|
| SDK location not found | Check `local.properties` or `ANDROID_HOME` in the terminal running Gradle. |
| Missing SDK platform | Install the platform requested by the build error. |
| No Android run configuration | Check IDE Android support and Gradle sync. |
| JDK error | Read the first error and check the daemon JDK settings. |
| Emulator does not start | Check virtualisation and the image; try the API 36 setup used for the screenshots. |
| No device found | Start the emulator, or connect a phone with USB debugging enabled. |
| Changes are missing | Rerun after code changes. M1 data resets after a process restart. |

See [CONTEXT.md](CONTEXT.md) for an explanation of the code.
