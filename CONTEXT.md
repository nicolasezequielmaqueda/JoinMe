
# JoinMe project notes

The [M2 report](M2.md) gives the short version for the demonstration.

## Where to look

| File or folder | What it does |
|---|---|
| `androidApp/.../MainActivity.kt` | Starts Compose, opens Android's share menu, asks for the notification permission after sign-in and decides if the glass look can be used. |
| `androidApp/.../JoinMeSession.kt` | Opens the Room database, creates the Supabase client and gives the screens one repository, one auth repository and one weather source. It survives a rotation. |
| `androidApp/.../WorkReminderScheduler.kt`, `ReminderWorker.kt` | Schedule the local reminder with WorkManager and show the notification. |
| `shared/.../data/local` | Room database: activity table, sync info (time of the last download), DAO and conversions to and from `Activity`. |
| `shared/schemas` | Room schema exported at build time. It is committed so schema changes show up in review. |
| `shared/.../data/AuthRepository.kt` | Sign in, sign up, sign out, delete account and the saved session. |
| `androidApp/.../OpenMeteoWeather.kt` | Makes the HTTPS request for the forecast of one day and saves the most recent successful response. |
| `shared/.../data/WeatherForecast.kt` | Picks the hour of the activity from the response. It is plain Kotlin, so it is tested without a phone. |
| `shared/.../ui/navigation` | Connects the screens (login, Activities, My Activities, create, detail, account) and the bottom tab bar. |
| `shared/.../ui/screens` | Screens display data. ViewModels handle events and prepare display state. |
| `shared/.../ui/theme`, `ui/components` | See "UI structure" below. |
| `shared/.../domain` | Rules and form validation. |
| `shared/.../data/SupabaseActivityRepository.kt` | The real repository. Checks each change, sends it to Supabase, downloads the list again and keeps the Room copy and the reminders up to date. |
| `shared/.../data/remote` | Row of Supabase as sent over the network (`ActivityRow`) and its conversion to `Activity`. |
| `shared/.../data/ReminderScheduler.kt` | What the repository needs to schedule or cancel the reminder of an activity. |
| `shared/.../data/LocalActivityRepository.kt` | In-memory repository for previews, iOS and tests. Needs no network. |
| `shared/.../util/DateTimeUtils.kt` | Current time in the residence time zone and date formatting. |
| `supabase/` | Database schema, functions and demo seed. |

## Follow a Join tap

The detail screen calls `ActivityDetailViewModel`, which has the id of the signed-in user. It asks `SupabaseActivityRepository` to join the activity. The repository first checks the local copy with `ActivityRules`, so a duplicate or a full activity is answered without network. If allowed, it calls the `join_activity` function in Supabase, which checks the same rules again. Then it saves the change in Room, downloads the list again and updates the reminder. The screens always read from Room through StateFlow, so they update by themselves. If the call fails, nothing changes and the message is shown. If the server rejected it (for example the activity became full), the list is downloaded again so the card stops showing old places. Without a connection the message says that joining needs one, and the action button is disabled while the last refresh failed for lack of connection.

Activity data must survive a restart. The current filter, form draft, dialogs and feedback are interface state. The ViewModels keep the active state during configuration changes such as rotation. Unsaved drafts can be lost after process death.

## Follow a weather refresh

The panel shows the forecast for the hour the activity starts (Open-Meteo, hourly, Europe/Warsaw). Its ViewModel keeps the display state and calls the Android source, which fetches the JSON of that day on an I/O thread. `parseForecast` picks the hour (17:45 uses the 17:00 value). A good response is saved with the time of the download. The saved forecast is only shown if it contains the date and hour of this activity. An empty result or a request error leaves the old reading available. If the activity has already started or is more than 15 days away there is no request and the panel says so. The weather panel is separate from the activity repository, so losing the connection does not block the main flow.

## Follow a refresh and sign out

When the user signs in, `App` asks the repository to refresh, and asks again every time the app comes back to the foreground. Pull to refresh does the same. The repository downloads the activities of the last 7 days and the future ones, then replaces the Room table and saves the time. If the download fails, Room is not touched and the screens show "Offline · Last updated ..." above the list. The detail screen reads the same error: while it says offline, Join, Leave and Cancel are disabled with an explanation, and they work again after a refresh succeeds.

Sign out (`AccountViewModel`) first closes the session (locally too if the server cannot be reached) and then calls `clearLocalData`, which empties Room and cancels the reminders. Deleting the account does the same after the server confirms. If the session ends by itself (for example a revoked token), `SignInGate` in `App` also calls `clearLocalData` when it sees no user. A new user id creates the navigation and the ViewModels again, so nothing of the previous user stays.

## Starting without a network

Room is shown first. The saved session is loaded from the device; if the token cannot be renewed, `AuthRepository` keeps using the saved user so the saved activities stay visible. The refresh then fails and the "Offline" line appears. If the saved session has already expired, the app may ask for sign-in until the network is back.

## Time zone

Dates and times of activities have no zone. The app (`DateTimeUtils`) and the database functions and seed all use Europe/Warsaw, so "has it started" and "today" give the same answer in both. The phone's zone is ignored.

## UI structure

- `ui/theme`: colors (light and dark), typography (Inter) and `JoinMeDimens` with the shared spacing and corner sizes.
- `ui/components`: shared pieces (cards, buttons, headers, pills, toasts, dialogs). Screens use these instead of their own styling.
- `GlassSurface` draws the top bar, bottom bars and floating tab bar with a blur from Haze. Content under it uses `glassSource()`.
- `MainActivity` turns glass off (`LocalGlassEnabled`) below Android 12, with high text contrast or high contrast theme, or with animations removed. The surfaces are then opaque with a separator line.

## Demo data and risks

Demo activities come from Supabase and their participants are fictional. Sample activities in the code are only used by previews, iOS and tests. Avoid personal information in activity notes. Sharing only includes the title, date, start time and shared place. The recipient and destination app are chosen in Android. The weather request uses a fixed demo location and a saved forecast may be outdated.
