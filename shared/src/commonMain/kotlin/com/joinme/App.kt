package com.joinme

import androidx.compose.runtime.Composable
import com.joinme.ui.navigation.AppNavigation
import com.joinme.ui.theme.JoinMeTheme

/**
 * Entry point of the shared UI. Used by Android (MainActivity) and iOS (MainViewController).
 */
@Composable
fun App() {
    JoinMeTheme {
        AppNavigation()
    }
}
