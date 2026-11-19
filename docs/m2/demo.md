
# JoinMe M2 | Android demo

Follow the [build and Supabase setup](../../DEPLOY.md). Use the demo project and fictional data. Before the demo, run `supabase/seed.sql` so the sample activities are upcoming again. Keep the device signed in for the restart and offline checks.

1. Sign in as **demo2@joinme.test** with the password set in Supabase. **demo1** created Table Tennis.
2. Open **Table Tennis**, which starts with one of two places taken. Press **Join activity**. Show **2 of 2**, **FULL** and **Leave activity**. Find it under **My Activities → Upcoming → Joined**.
3. Tap **New** and show the validation message with the form still incomplete. Pick a future date and finish the form. Show **Preview**, create the activity, and find it under **Created by you**.
4. Force stop the app and reopen it. Check the joined and created activities are visible. To check rotation, start another form, choose a type, and turn the device. The draft should keep the chosen type.
5. Open **Football** at the Sports Court. Use **Check forecast** and show the hourly reading and its update time. The demo city is Łódź. The response comes from Open-Meteo.
6. Disable Wi-Fi and mobile data. Pull to refresh in **Activities** and wait for **Offline · Last updated ...**. The saved activities should stay visible. Open an activity with places left and show why **Join** is disabled. Refresh the weather to show the failed refresh and the saved forecast. Reconnect and refresh again.
7. For reminders, allow notifications and create or join an activity that starts more than 30 minutes ahead. For a short rehearsal, set its start about 33–35 minutes away. WorkManager aims to show the reminder 30 minutes before the start and Android may delay it. Then turn off notifications in Android settings and reopen **My Activities**. Show **Reminders are off** and that the activity details remain available.
8. On an upcoming activity, use **Share activity** to open Android's share menu. Dismiss it. Select **Show invite text** to see the text that can be copied.
9. Sign out after the offline checks. The app returns to login and clears saved activities and scheduled reminders for this session.

## Forecast response example

`GET /v1/forecast` with the coordinates of Łódź, the activity date as both `start_date` and `end_date`, and `temperature_2m`, `precipitation_probability` and `precipitation` as hourly fields.

Example fragment for a test, using made-up values:

```json
{"hourly":{"time":["2026-11-20T17:00"],"temperature_2m":[8.5],"precipitation_probability":[15],"precipitation":[0.0]}}
```

## Verification for this build

| Check | Result |
|---|---|
| Build and launch | The debug APK built and opened the login screen on Medium Phone API 36.1. |
| Automated tests | 98 JVM tests and two Android forecast cache tests passed. |
| Database | The schema and seed ran in local PostgreSQL with test identities. Create, join, leave, cancel and account deletion passed. Two people trying for the last place left the activity at its limit. |
| Live forecast | Open-Meteo returned the hourly fields from Windows. |
| Full Android demo | Sign-in with the team's Supabase project, restart and offline checks, and notification delivery with permission on and off still need a live run. |
