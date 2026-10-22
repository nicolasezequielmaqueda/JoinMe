# JoinMe — Project notes

These notes explain the project and help us prepare for the code discussion. The short submission document is [M1.md](M1.md).

## Goal and scope

We are Nicolás Maqueda and Jaime Criado. We are building JoinMe for adult Erasmus and international students who want to find people for activities in their residence.

Our main task is **Activities → Activity Detail → Join → My Activities**. A second task lets the user create an activity. We use four screens and focus on making these tasks work well.

Android is the required platform. The instructors have approved Kotlin Multiplatform and Compose Multiplatform, as confirmed by the team. The project includes an iOS target, but building and testing it is optional and needs macOS and Xcode.

## What works in M1

The prototype supports browsing, filters, details, joining, leaving, creating, cancelling your own activities, and Upcoming/Past lists. It uses made-up activity data and a fixed test user called `user-me`.

Activities are stored in memory. Changes disappear when the app process restarts. Moving the app to the background does not always restart the process. There is no database, server request, shared data between devices or reminder system yet.

## How the code is organised

| Part | Purpose | Example |
|---|---|---|
| `model` | Classes that describe the data | `Activity` stores the date, place and participants. |
| `domain` | Rules that do not depend on the screen | `ActivityRules` checks whether someone can join. |
| `data` | Stores activities and provides access to them | `FakeActivityRepository` keeps a list in memory. |
| `ui/screens` | Screens and their ViewModels | `ActivityDetailViewModel` handles Join and Leave. |
| `ui/components` | Interface parts used by several screens | Buttons, cards and status labels. |
| `ui/navigation` | Moves between screens | A detail route includes the activity ID. |
| `ui/theme` | Shared colours and text styles | Light and dark themes. |
| `util` | Date and time helpers | Display a date as Today or Tomorrow. |

A **ViewModel** holds the data and actions for a screen. A **repository** is the place the ViewModels use to read or change activities. `AppContainer` creates one repository shared by all screens.

`StateFlow` holds a value that screens can observe. When the repository changes its list, the ViewModels prepare new screen data and Compose updates the interface. Repository actions use `suspend` functions so they can later include database or network work.

## What happens when someone joins

1. The user presses Join activity.
2. `ActivityDetailViewModel` calls `joinActivity` on the repository.
3. The repository finds the activity and calls `ActivityRules.join`.
4. The rule checks membership and status. It returns an updated copy or an error message.
5. If successful, the repository replaces the old activity in its list.
6. The screens receive the updated data. The count increases and the activity appears in My Activities.

The creator counts as the first participant. Users cannot join twice or join a full, cancelled or completed activity. Creators cancel their activities instead of leaving them.

## Dates and status

The sample dates are based on the current day when the repository is created. Table Tennis starts today at 23:00 with one free place. Pool is full, Padel is cancelled, and Darts is completed. The cancelled Padel example does not appear in the main list or this user's activities. To show cancellation, cancel Board Games, which belongs to the test user.

Status is calculated from the activity and a supplied time: cancelled first, then completed if its end time has passed, then full if no places remain, otherwise open. In this prototype, joining after the start is allowed until the end time. The UI recalculates status when its data updates; it does not have a timer that refreshes status every minute.

## Next steps

- **Saved data:** use Room so joined activities survive a restart.
- **Shared data:** use Supabase and show loading, success and failure states.
- **Offline use:** show the last saved activities and explain that they may be out of date. Define what happens to join requests without a connection before implementing them.
- **Reminders:** add local notifications. Denied permission must not block the main task.
- **Verification:** test restart and network failure, finish accessibility checks, and run two voluntary usability sessions with consenting adult peers outside the team.

A server will also need to check who may change an activity and how to prevent two users from taking the last place at the same time. The in-memory rules are not enough for shared use.

Chat, profiles, GPS, payments, AI matching, activity editing and time-conflict warnings are outside the current scope. Each student has 25 hours of independent work, so the main task comes first.

## Tests and honest reporting

There are 14 automated tests for activity rules and form validation. They use a fixed time, so the result does not depend on the day they run. These tests do not prove that the interface or accessibility works.

The [M1 supporting notes](docs/m1/M1-details.md) record the peer review and pending checks. We must only report tests we have actually completed. The two-classmate review was informal and does not prove wider social benefits.

AI has helped review documentation, explain the code and check the project in this working session. We must read and verify the changes, understand the main flow and be able to modify it ourselves. This note does not describe how all earlier code was written.

### AI verification record — 22 October 2026

| AI-supported change | Check and result |
|---|---|
| Rewrite the documents in clear English and explain the code. | Compared the descriptions with the repository. Removed claims that reminders, saved data and shared data already work. |
| Review comments and short interface messages. | Corrected comments about status updates, layout and unverified accessibility. The app's rules were not changed. |
| Check the edited project. | Android debug build succeeded and all 14 logic tests passed. Local Markdown links were checked. |

Update from the team: the maximum-font check passed on the Medium Phone API 36.1 emulator (22 October 2026). The team also confirmed the manual label check on the same date and emulator: OPEN, FULL, JOINED, CANCELLED and COMPLETED and the main actions are understandable from their text without relying on colour. A greyscale filter was not required. The exact screens covered by the font check still need to be recorded. The team's final reading, full TalkBack/control-size checks and optional iOS testing also remain pending. Automated tests do not replace these checks.

## Team work

Nicolás and Jaime worked together on the design, implementation, documentation and checks, with AI support. Both share responsibility for understanding the full main flow and preparing the demonstration. Individual contribution notes will describe their work together and each member's contributions.
