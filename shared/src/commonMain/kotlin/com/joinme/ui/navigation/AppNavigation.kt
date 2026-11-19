package com.joinme.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.joinme.data.ActivityRepository
import com.joinme.data.AuthRepository
import com.joinme.data.WeatherSource
import com.joinme.ui.components.LocalHazeState
import com.joinme.ui.components.LocalTabBarHeight
import com.joinme.ui.components.LocalToastState
import com.joinme.ui.components.ToastState
import com.joinme.ui.screens.account.AccountScreen
import com.joinme.ui.screens.activities.ActivitiesScreen
import com.joinme.ui.screens.create.CreateActivityScreen
import com.joinme.ui.screens.detail.ActivityDetailScreen
import com.joinme.ui.screens.myactivities.MyActivitiesScreen
import com.joinme.ui.theme.JoinMeTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

/**
 * Navigation of the whole app:
 * Activities / My Activities (tabs) → Activity Detail, Activities → Create
 * and My Activities → Account.
 */
@Composable
fun AppNavigation(
    repository: ActivityRepository,
    weather: WeatherSource?,
    shareText: (String) -> Boolean,
    auth: AuthRepository?,
    userId: String,
    notificationsAllowed: Boolean,
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    // Also used by the "Browse activities" buttons of empty states.
    val selectTab: (BottomTab) -> Unit = { tab ->
        navController.navigate(tab.route) {
            // Avoid piling up copies of the tabs in the back stack.
            popUpTo(Routes.ACTIVITIES) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    // The tab bar is only shown on the two main screens.
    val showTabBar = BottomTab.entries.any { it.route == currentRoute }

    val hazeState = remember { HazeState() }
    val toastState = remember { ToastState() }
    val density = LocalDensity.current
    var tabBarHeight by remember { mutableStateOf(0.dp) }

    CompositionLocalProvider(
        LocalHazeState provides hazeState,
        LocalToastState provides toastState,
        LocalTabBarHeight provides if (showTabBar) tabBarHeight else 0.dp,
    ) {
        Box(modifier = Modifier.fillMaxSize().background(JoinMeTheme.colors.background)) {
            NavHost(
                navController = navController,
                startDestination = Routes.ACTIVITIES,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable(Routes.ACTIVITIES) {
                    ActivitiesScreen(
                        repository = repository,
                        userId = userId,
                        onActivityClick = { id -> navController.navigate(Routes.detail(id)) },
                        onCreateClick = { navController.navigate(Routes.CREATE) },
                    )
                }

                composable(Routes.MY_ACTIVITIES) {
                    MyActivitiesScreen(
                        repository = repository,
                        userId = userId,
                        notificationsAllowed = notificationsAllowed,
                        onBrowseClick = { selectTab(BottomTab.ACTIVITIES) },
                        onActivityClick = { id -> navController.navigate(Routes.detail(id)) },
                        // Without sign-in (previews, iOS) there is no account screen.
                        onAccountClick = if (auth == null) null else { { navController.navigate(Routes.ACCOUNT) } },
                    )
                }

                composable(
                    route = Routes.DETAIL,
                    arguments = listOf(navArgument(Routes.ARG_ACTIVITY_ID) { type = NavType.StringType }),
                ) {
                    ActivityDetailScreen(
                        repository = repository,
                        userId = userId,
                        weather = weather,
                        shareText = shareText,
                        onBack = { navController.popBackStack() },
                    )
                }

                composable(Routes.CREATE) {
                    CreateActivityScreen(
                        repository = repository,
                        userId = userId,
                        onBack = { navController.popBackStack() },
                        onActivityCreated = { id ->
                            // Open the new activity and remove the form from the back stack,
                            // so "back" returns to the list and not to the form.
                            navController.navigate(Routes.detail(id)) {
                                popUpTo(Routes.CREATE) { inclusive = true }
                            }
                        },
                    )
                }

                if (auth != null) {
                    composable(Routes.ACCOUNT) {
                        AccountScreen(auth = auth, repository = repository, onBack = { navController.popBackStack() })
                    }
                }
            }

            if (showTabBar) {
                // The floating bar is measured with its margins, so lists know how much room to leave.
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .onSizeChanged { tabBarHeight = with(density) { it.height.toDp() } }
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(start = 24.dp, end = 24.dp, bottom = 8.dp, top = 8.dp),
                ) {
                    TabBar(currentRoute = currentRoute, onTabSelected = selectTab)
                }
            }
        }
    }
}

