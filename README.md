# JoinMe

JoinMe is a course project for **Introduction to Mobile Systems — Mobile for Social Good**.

**Team:** Nicolás Maqueda and Jaime Criado. The team has confirmed that both instructors have accepted access to the repository and approved Kotlin Multiplatform + Compose Multiplatform.

## Our idea

We want to help adult Erasmus and international students find people for simple activities in their residence. A student may want to play table tennis but not know who to ask. A group may want to play football but need more players.

JoinMe puts activities first. Students can see what is planned, when and where it happens, and how many places are available. We do not claim that the app solves loneliness or guarantees friendships.

## Current version: M1 prototype

The Android prototype has four screens:

- **Activities:** browse and filter sample activities.
- **Activity Detail:** see the information and join, leave or cancel an activity you created.
- **Create Activity:** choose an activity, date, time, place and number of people.
- **My Activities:** see activities you created or joined, grouped into Upcoming and Past.

The main task is: **find Table Tennis → open its details → join → find it in My Activities**.

The prototype uses made-up data and one fixed test user. Data stays in memory and resets when the app process restarts. Two phones do not share activities yet. Sample dates are based on the day the repository is created when the app starts.

## What comes later

We plan to add Room for saved data on the phone, Supabase for shared data, network loading and error messages, and access to previously saved activities without a connection. We also plan optional activity reminders. If notification permission is denied, users will still be able to check My Activities.

These features are not implemented in M1. Android is our required platform. iOS is an optional extension that needs a Mac and Xcode to build and test.

Chat, social profiles, followers, photos, GPS, payments, ratings, AI matching and complex login are outside the project scope. Editing activities and checking overlapping times are also outside the current agreed scope.

## Run and understand the project

Start with the [setup guide](DEPLOY.md). It explains the Android SDK, emulator and build commands.

With the SDK configured, run these commands from PowerShell in the project folder:

```powershell
.\gradlew.bat :androidApp:assembleDebug
.\gradlew.bat :shared:testAndroidHostTest
```

On macOS or Linux, use `./gradlew` instead of `.\gradlew.bat`.

The code uses Kotlin and Compose Multiplatform. Screens show the interface, ViewModels prepare screen data, and a repository stores activities. The [project notes](CONTEXT.md) explain these parts with a join example.

## Project documents

- [M1 brief and prototype images](M1.md): the short document for submission.
- [M1 supporting notes](docs/m1/M1-details.md): acceptance criteria, peer feedback, accessibility checks and risks.
- [Project notes](CONTEXT.md): code structure, limits and next steps.
- [Setup and demo guide](DEPLOY.md): build, run and test instructions.

The app uses Inter fonts and Phosphor icons. The bundled Inter licence is in [docs/licenses/Inter-OFL.txt](docs/licenses/Inter-OFL.txt).
