package com.joinme.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joinme.ui.components.GlassKind
import com.joinme.ui.components.GlassSurface
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
 * iOS style tab bar: a floating glass pill with icon + label. The selected tab sits
 * on a soft capsule, uses a filled icon and the accent colour.
 */
@Composable
fun TabBar(currentRoute: String?, modifier: Modifier = Modifier, onTabSelected: (BottomTab) -> Unit) {
    val colors = JoinMeTheme.colors
    GlassSurface(
        kind = GlassKind.FloatingBar,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(6.dp).selectableGroup(),
        ) {
            BottomTab.entries.forEach { tab ->
                val selected = currentRoute == tab.route
                val tint = if (selected) colors.accent else colors.textSecondary
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp)
                        .clip(CircleShape)
                        .background(if (selected) colors.accentSoft else Color.Transparent)
                        .selectable(selected = selected, role = Role.Tab, onClick = { onTabSelected(tab) })
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.icon,
                        contentDescription = null, // the label below is read instead
                        tint = tint,
                        modifier = Modifier.size(26.dp),
                    )
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = tint,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}
