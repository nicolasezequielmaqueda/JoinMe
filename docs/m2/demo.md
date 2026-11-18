
# JoinMe M2 | Demo steps

Use the Android emulator and the debug APK. [Build instructions](../../DEPLOY.md)

1. Open the app with fresh demo data. Find **Table Tennis**, open its details and press **Join activity**. Show **2 of 2**, **FULL** and **Leave activity**. Go back and find it under **My Activities → Upcoming → Joined**.
2. Press **New**. Show the validation message with the form incomplete. Choose an activity, tomorrow's date, a time and a place. Create it and find it under **Created by you**.
3. Force stop and reopen the app. Show both items in My Activities. Rotating with a form open should keep the draft on screen. An unfinished form is not saved when the process stops.
4. Open the sample **Football** activity, scroll down and select **Check weather**. Show the reading and its time. Disable Wi-Fi and mobile data. Select **Refresh weather** and wait for the error. The saved reading stays visible and activity changes still work. Reconnect and try again.
5. On an upcoming activity, select **Share activity**. Show the Android share menu and dismiss it without sending anything. Select **Show invite text** and long press the text to copy it. This uses the same invite dialog as the unavailable path.
6. Open **My Activities** again. The joined list and locally saved activities do not depend on the weather request.

A fresh installation creates sample dates relative to the first launch. After you save a change, those dates stay fixed. If you want to repeat the demo later and the sample events have ended, install on a fresh emulator or create new activities. Clearing app storage also resets the demo but removes saved activities.

## API example

`GET /v1/forecast?latitude=51.76&longitude=19.46&current=temperature_2m,precipitation&timezone=Europe%2FWarsaw`

Example response fragment (test fixture):

```json
{"current":{"time":"2026-11-18T16:30","temperature_2m":8.5,"precipitation":0.0}}
```

The response tests use sample JSON for a good reading, an empty result and missing fields. The offline demo uses the real service with the emulator connection switched off.

## Checks on Medium Phone API 36.1

| Check | Result |
|---|---|
| Find, join, create and restart | Table Tennis showed 2 of 2 and appeared under Joined. The created Badminton activity and the joined activity both survived a force stop and reopen. |
| Cancel and reopen | Board Games moved to Past and still showed CANCELLED after a process restart. |
| Rotate an unfinished form | The selected activity type stayed in the form. The missing date message still appeared. |
| Live weather and retry | The app received a real Open-Meteo reading and loaded it again after reconnecting. |
| Network loss | With Wi-Fi and mobile data off, the app showed an error and kept the saved reading visible. Joining still used the local list. |
| Good, empty and unexpected JSON | All three Android response tests passed with controlled JSON examples. |
| Android sharing | The system share menu opened. No message was sent during this check. Show invite text opened the manual option. |
| Share unavailable | A temporary test build tried to open a missing activity. The app caught Android's error and showed the invite text. |
| Activity rules and form validation | All 14 tests passed. |

The Windows host uses Avast HTTPS inspection. The emulator rejected its certificate with Android's normal trust settings. For the live API check only, a temporary build trusted the host certificate for the Open-Meteo domain. The submitted APK uses Android's normal HTTPS trust settings. Use a connected Android phone or an emulator with a trusted connection for the live demo if this host still blocks it.
