
# JoinMe

JoinMe is a course project for **Introduction to Mobile Systems: Mobile for Social Good**.

**Team:** Nicolás Maqueda and Jaime Criado. Both instructors accepted access to the repository and approved Kotlin Multiplatform and Compose Multiplatform, as confirmed by the team.

JoinMe helps adult Erasmus and international students find people for activities in their residence. A newcomer may want to play table tennis but not know who to ask. A group may want to play football but need more people.

## Run the Android MVP

Open the project in your IDE, start an emulator, select the `androidApp` run configuration and press **Run**. For SDK setup, commands and APK installation, see [DEPLOY.md](DEPLOY.md).

The main task is **find Table Tennis → open details → join → find it in My Activities**. The app uses fictional people, places and activities. Creations, joins, leaves and cancellations stay on that device. Outdoor activities can show current weather for the demo city. The Android share menu can send the details as plain text.

## Project documents

- [M2 report](M2.md): works checklist, architecture, limitations and next fixes.
- [Demo checklist](docs/m2/demo.md): how to show the main flow and a network failure.
- [AI use and checks](AI_USE.md): review of a small change.
- [Project notes](CONTEXT.md): code structure and event flow.
- [M1 brief](M1.md): earlier prototype and original scope.

Android is the required platform. iOS remains an optional prototype. Inter fonts are bundled under the [Inter licence](docs/licenses/Inter-OFL.txt). The app uses Phosphor icons.
