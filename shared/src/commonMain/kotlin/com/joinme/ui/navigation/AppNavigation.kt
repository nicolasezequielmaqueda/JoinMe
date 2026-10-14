package com.joinme.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.joinme.ui.screens.activities.ActivitiesScreen
import com.joinme.ui.screens.create.CreateActivityScreen
import com.joinme.ui.screens.detail.ActivityDetailScreen
import com.joinme.ui.screens.myactivities.MyActivitiesScreen
import com.joinme.ui.theme.JoinMeTheme

/**
 * Navigation of the whole app:
 * Activities / My Activities (tabs) → Activity Detail, and Activities → Create.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    // The tab bar is only shown on the two main screens.
    val showTabBar = BottomTab.entries.any { it.route == currentRoute }

    Scaffold(
        containerColor = JoinMeTheme.colors.background,
        // Each screen handles the status bar itself (header or nav bar).
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (showTabBar) {
                TabBar(currentRoute = currentRoute) { tab ->
                    navController.navigate(tab.route) {
                        // Avoid piling up copies of the tabs in the back stack.
                        popUpTo(Routes.ACTIVITIES) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.ACTIVITIES,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.ACTIVITIES) {
                ActivitiesScreen(
                    onActivityClick = { id -> navController.navigate(Routes.detail(id)) },
                    onCreateClick = { navController.navigate(Routes.CREATE) },
                )
            }

            composable(Routes.MY_ACTIVITIES) {
                MyActivitiesScreen(
                    onActivityClick = { id -> navController.navigate(Routes.detail(id)) },
                )
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument(Routes.ARG_ACTIVITY_ID) { type = NavType.StringType }),
            ) {
                ActivityDetailScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.CREATE) {
                CreateActivityScreen(
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
        }
    }
}
