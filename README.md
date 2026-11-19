
# JoinMe

JoinMe is a course project for **Introduction to Mobile Systems: Mobile for Social Good**.

**Team:** Nicolás Maqueda and Jaime Criado. Both instructors accepted access to the repository and approved Kotlin Multiplatform and Compose Multiplatform, as confirmed by the team.

JoinMe helps adult Erasmus and international students find people for activities in their residence. A newcomer may want to play table tennis but not know who to ask. A group may want to play football but need more people.

## What the app does

- Sign in or create an account with email and password. The session is kept on the device.
- Activities are shared: they live in Supabase and every signed-in user sees the same list. The app keeps a copy in Room.
- Without a connection the saved list is read-only, with an "Offline · Last updated ..." line. Pull to refresh downloads it again.
- Join, create, leave and cancel activities. The create form shows a preview before you save.
- A local reminder 30 minutes before an activity you take part in. The app asks for the notification permission after sign-in. If you deny it, everything else works the same and you only lose the reminders.
- Share an activity as plain text through the Android share menu.
- Hourly forecast for the start of outdoor activities, from Open-Meteo.
- Sign out and delete your account (this also deletes your data in the database).

The main task is **find Table Tennis → open details → join → find it in My Activities**. The demo people, places and activities are **fictional** and come from `supabase/seed.sql`. Do not put personal information in activity notes.

## Run it

The app needs a Supabase project, so there are a few setup steps before the first run. [DEPLOY.md](DEPLOY.md) covers the SDK, the database, `local.properties`, the build commands and a demo script.

## Known limitations

- Without a connection the app is read-only. Join, create, leave and cancel need the network.
- "Offline" is detected when a request fails, so the notice can take about 10 seconds to appear.
- Reminders use WorkManager and may arrive a few minutes late.
- The time zone is fixed to Europe/Warsaw (the residence) in the app and in the database. It does not follow the phone and has not been checked around daylight saving changes.
- Leaked-password protection is not available on the free Supabase plan, so it is off.
- A free Supabase project is paused after 7 days without use. Restore it in the dashboard before a demo.
- The list only downloads activities from the last 7 days onwards.
- If the app starts without a connection and the saved session has expired, it may ask for sign-in again until the connection is back.
- iOS compiles but has no login and no Room, uses in-memory sample data and has not been tested. Android is the supported platform.

## Project documents

- [Build, run and demo](DEPLOY.md): setup, Supabase, commands and demo steps.
- [Database](supabase/README.md): schema, demo users and seed.
- [M2 report](M2.md): demo checklist, architecture, verification and next fixes.
- [Demo checklist](docs/m2/demo.md): how to show the main flow and a network failure.
- [AI use and checks](AI_USE.md): review of a small change.
- [Project notes](CONTEXT.md): code structure and event flow.
- [M1 brief](M1.md): earlier prototype and original scope.

Android is the required platform. iOS remains an optional prototype. Inter fonts are bundled under the [Inter licence](docs/licenses/Inter-OFL.txt). The app uses Phosphor icons.
