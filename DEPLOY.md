# JoinMe | Build and run on Android

Open the folder containing `settings.gradle.kts` in IntelliJ IDEA (with the Android plugin) and sync with the Gradle wrapper. The first build needs internet access to download dependencies.

| Setting | Value |
|---|---|
| IDE | IntelliJ IDEA 2026.2 or later with the Android plugin. 2026.1 does not support AGP 9.1 |
| Gradle / JDK | Wrapper 9.5.1 with JDK 21 |
| Android Gradle Plugin | 9.1.1 |
| Kotlin / Compose Multiplatform | 2.4.20 / 1.12.1 |
| Compile / target / minimum Android API | 37 / 37 / 24 |
| Demo device | Medium Phone API 36.1 emulator |

Install Android SDK Platform 37, SDK Build-Tools, Platform-Tools and the Android Emulator in SDK Manager. Create and start an emulator in Device Manager. The IDE normally writes the local SDK path to `local.properties`. If needed, set `ANDROID_HOME` to the SDK folder in the terminal.

On Linux with SELinux the emulator may crash on start. Set the emulator's Graphics to **Hardware** in Device Manager, or start it with `-gpu host`.

## Set up Supabase

The app reads its data from a Supabase project, so create one first (the free plan is enough).

1. Run `supabase/schema.sql` in the SQL editor.
2. Create the demo users and run `supabase/seed.sql`. The steps are in [supabase/README.md](supabase/README.md).
3. In **Authentication**, turn off **Confirm email** and set the minimum password length to 8.
4. Add the project URL and the publishable key (Project Settings > API) to `local.properties`:

```properties
SUPABASE_URL=https://YOUR-PROJECT.supabase.co
SUPABASE_PUBLISHABLE_KEY=sb_publishable_...
```

`local.properties` is not committed. Without these two lines the app can open the login screen but sign-in fails and may show an offline error. Rebuild after changing them.

The seed uses dates relative to today. Run `seed.sql` again before each demo, otherwise the activities may already be over.

## Run from the IDE

Select the **Android App** configuration for `androidApp`, choose the running emulator or connected phone and press **Run**. The IDE builds, installs and opens JoinMe.

## Build, install or test from the terminal

Run these commands from the repository root (`gradlew.bat` on Windows).

```bash
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
./gradlew :shared:testAndroidHostTest
./gradlew :androidApp:connectedDebugAndroidTest
```

The last command runs the Android weather response tests on a connected emulator or device. `testAndroidHostTest` runs the unit tests in `shared` on the JVM.

The APK is at `androidApp/build/outputs/apk/debug/androidApp-debug.apk`. To install it on a running emulator or phone: `adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk`.

The optional iOS target needs a Mac and Xcode. It compiles but has no login or Room and has not been tested.

## Demo steps

Use the emulator with the network on. Run the demo seed in Supabase first.

1. Sign in as **demo2@joinme.test** with the password set for that account. **demo1** created Table Tennis.
2. Open **Table Tennis**, press **Join activity**, and show **2 of 2**, **FULL** and the entry in **My Activities → Upcoming → Joined**.
3. Show the validation and preview when creating an activity. Save it, force stop the app, and reopen it with the same account to show the saved list.
4. Open **Football** and use **Check forecast**. Then turn off Wi-Fi and mobile data, pull to refresh, and show the saved list and the offline message. Reconnect and refresh.
5. Show the reminder permission and its fallback. With notifications off, **My Activities** still shows the activity time and place and explains how to turn reminders on.

See the [full M2 demo checklist](docs/m2/demo.md) for preparation, timing and test results.
