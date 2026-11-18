
# AI use in JoinMe M2

## Small change reviewed: saved weather after a failed refresh

- **Proposal:** add a weather panel with a refresh button and a loading indicator.
- **Correction:** keep the last good reading when a new request fails or returns no data. Show the reading time, warn that it may be old, and leave Refresh available. Joining and creating activities still use local data.
- **What to inspect:** `WeatherViewModel.refresh` keeps the previous reading in both cases. The Android source saves a reading only after it can read its fields.
- **Result:** the Android emulator showed a live reading, kept it when Wi-Fi and mobile data were off, and loaded a reading again when the connection returned. We could still join an activity offline. The response tests passed for valid, empty and incomplete JSON. The [demo notes](docs/m2/demo.md) explain the test setup.

We used AI to suggest and implement small changes, and checked them against the running Android app and source. Each team member should be able to explain the event path and reproduce the checks.
