package com.joinme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.joinme.data.ActivityRepository
import com.joinme.data.LocalActivityRepository
import com.joinme.data.WeatherSource
import com.joinme.ui.navigation.AppNavigation
import com.joinme.ui.theme.JoinMeTheme

@Composable
fun App(
    repository: ActivityRepository = remember { LocalActivityRepository() },
    weather: WeatherSource? = null,
    shareText: (String) -> Boolean = { false },
    storageMessage: String? = null,
) {
    JoinMeTheme {
        AppNavigation(repository, weather, shareText, storageMessage)
    }
}
