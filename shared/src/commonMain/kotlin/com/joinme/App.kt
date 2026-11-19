package com.joinme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.joinme.data.ActivityRepository
import com.joinme.data.AuthRepository
import com.joinme.data.LocalActivityRepository
import com.joinme.data.SampleActivities
import com.joinme.data.WeatherSource
import com.joinme.ui.components.LocalGlassEnabled
import com.joinme.ui.navigation.AppNavigation
import com.joinme.ui.screens.login.LoginScreen
import com.joinme.ui.theme.JoinMeTheme

/**
 * If [auth] is null (previews, iOS) the app opens directly without sign-in.
 * [glassEnabled] turns the blurred "glass" bars on; when false they are opaque.
 */
@Composable
fun App(
    repository: ActivityRepository = remember { LocalActivityRepository() },
    weather: WeatherSource? = null,
    shareText: (String) -> Boolean = { false },
    auth: AuthRepository? = null,
    notificationsAllowed: Boolean = true,
    glassEnabled: Boolean = true,
) {
    JoinMeTheme {
        CompositionLocalProvider(LocalGlassEnabled provides glassEnabled) {
            if (auth == null) {
                AppNavigation(repository, weather, shareText, auth = null, userId = SampleActivities.DEMO_USER_ID, notificationsAllowed = notificationsAllowed)
            } else {
                SignInGate(auth, repository) { userId ->
                    // A new key creates the navigation and all ViewModels again, so nothing of the previous user stays.
                    key(userId) {
                        // Refreshes at sign-in and every time the app comes back to the foreground.
                        val lifecycle = LocalLifecycleOwner.current.lifecycle
                        LaunchedEffect(lifecycle) {
                            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) { repository.refresh() }
                        }
                        AppNavigation(repository, weather, shareText, auth, userId, notificationsAllowed)
                    }
                }
            }
        }
    }
}

/**
 * Waits for the saved session, then shows the login screen or [content] with the user id.
 * Whenever there is no session, the data of the previous user is removed from the device.
 */
@Composable
private fun SignInGate(
    auth: AuthRepository,
    repository: ActivityRepository,
    content: @Composable (userId: String) -> Unit,
) {
    val isLoading by auth.isLoading.collectAsStateWithLifecycle()
    val userId by auth.userId.collectAsStateWithLifecycle()

    val signedInUserId = userId

    // Also covers a session that ended by itself (e.g. revoked token), not only "Sign out".
    LaunchedEffect(signedInUserId, isLoading) {
        if (!isLoading && signedInUserId == null) repository.clearLocalData()
    }

    when {
        isLoading -> LoadingScreen()
        signedInUserId == null -> LoginScreen(auth)
        else -> content(signedInUserId)
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(JoinMeTheme.colors.background),
    ) {
        CircularProgressIndicator(color = JoinMeTheme.colors.accent)
    }
}
