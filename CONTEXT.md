
# JoinMe project notes

The [M2 report](M2.md) gives the short version for the demonstration.

## Where to look

| File or folder | What it does |
|---|---|
| `androidApp/.../MainActivity.kt` | Starts Compose and opens Android's share menu. |
| `androidApp/.../JoinMeSession.kt` | Gives the screens one repository and one weather source. It survives a rotation. |
| `androidApp/.../ActivitySettings.kt` | Reads and saves the activity list as JSON using SharedPreferences. |
| `androidApp/.../OpenMeteoWeather.kt` | Makes the HTTPS request and saves the most recent successful reading. |
| `shared/.../ui/navigation` | Connects the four screens. |
| `shared/.../ui/screens` | Screens display data. ViewModels handle events and prepare display state. |
| `shared/.../domain` | Rules and form validation. |
| `shared/.../data/LocalActivityRepository.kt` | Holds the shared activity list and saves changes before showing them. |

## Follow a Join tap

The detail screen calls `ActivityDetailViewModel`. It asks the repository to join the activity. The repository checks the activity with `ActivityRules`. The rule checks for duplicate participation and available places. If allowed, it returns a copy with the new participant. The repository saves that copy as part of the updated list. The ViewModels receive the list through StateFlow and Compose updates the detail and My Activities. If saving fails, the app keeps showing the previous list and reports the error.

Activity data must survive a restart. The current filter, form draft, dialogs and feedback are interface state. The ViewModels keep the active state during configuration changes such as rotation. Unsaved drafts can be lost after process death.

## Follow a weather refresh

The panel's ViewModel keeps the current display state. It calls the Android source, which fetches and parses the JSON on an I/O thread. A good reading is saved for future visits. An empty result or a request error leaves the old reading available. The weather panel is separate from the activity repository, so losing the connection does not block the main flow.

## Demo data and risks

Sample activities fill the list until there is a saved list. The participant IDs are fictional. There is no sign-in and the list is only on this device. Avoid personal information in activity notes. Sharing only includes the title, date, start time and shared place. The recipient and destination app are chosen in Android. The weather request uses a fixed demo location and the reading may be outdated.
