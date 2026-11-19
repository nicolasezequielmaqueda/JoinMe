
# AI use in JoinMe M2

## Small change reviewed: saved weather after a failed refresh

- **Proposal:** add a weather panel with a refresh button and a loading message.
- **Accepted:** keep the last good reading when a refresh fails or returns no data.
- **Corrected for this version:** show an hourly forecast for the start of the activity. Show a saved forecast only if it covers that date and hour. Display its update time and allow another refresh.
- **What to inspect:** `WeatherViewModel.refresh` keeps the previous reading on failure. `OpenMeteoWeather` only saves a response after it finds a matching forecast.
- **Verification:** the JVM forecast tests and both Android cache tests passed. The live service returned the expected hourly fields from Windows. The Android forecast and offline checks with a configured sign-in are still to be demonstrated.

The earlier prototype could join activities offline. In this build, users can read the Room copy offline and need a connection to change the shared list. The [demo checklist](docs/m2/demo.md) covers both paths.
