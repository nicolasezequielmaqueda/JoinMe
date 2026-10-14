package com.joinme.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeTheme

/** The two tabs of the bottom bar. */
enum class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    ACTIVITIES(Routes.ACTIVITIES, "Activities", AppIcons.Discover, AppIcons.DiscoverSelected),
    MY_ACTIVITIES(Routes.MY_ACTIVITIES, "My Activities", AppIcons.MyActivities, AppIcons.MyActivitiesSelected),
}

/**
 * iOS style tab bar: thin line on top, icon + small label,
 * the selected tab uses a filled icon and the accent colour.
 */
@Composable
fun TabBar(currentRoute: String?, onTabSelected: (BottomTab) -> Unit) {
    val colors = JoinMeTheme.colors
    Column(modifier = Modifier.background(colors.surface)) {
        HorizontalDivider(color = colors.separator)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .selectableGroup(),
        ) {
            BottomTab.entries.forEach { tab ->
                val selected = currentRoute == tab.route
                val tint = if (selected) colors.accent else colors.textSecondary
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp)
                        .selectable(selected = selected, role = Role.Tab, onClick = { onTabSelected(tab) })
                        .padding(top = 8.dp, bottom = 6.dp),
                ) {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.icon,
                        contentDescription = null, // the label below is read instead
                        tint = tint,
                        modifier = Modifier.size(26.dp),
                    )
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = tint,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}
