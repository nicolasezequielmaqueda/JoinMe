package com.joinme.ui.screens.myactivities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joinme.data.AppContainer
import com.joinme.ui.components.ActivityCard
import com.joinme.ui.components.ActivityItem
import com.joinme.ui.components.EmptyState
import com.joinme.ui.components.LargeTitleHeader
import com.joinme.ui.components.SectionHeader
import com.joinme.ui.components.SegmentedControl
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeTheme

private val TABS = listOf("Upcoming", "Past")

/**
 * Activities created or joined by the user, split in Upcoming and Past.
 */
@Composable
fun MyActivitiesScreen(
    onActivityClick: (String) -> Unit,
    viewModel: MyActivitiesViewModel = viewModel { MyActivitiesViewModel(AppContainer.activityRepository) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(JoinMeTheme.colors.background)) {
        LargeTitleHeader(title = "My Activities")

        SegmentedControl(
            options = TABS,
            selectedIndex = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        val created = if (selectedTab == 0) uiState.upcomingCreated else uiState.pastCreated
        val joined = if (selectedTab == 0) uiState.upcomingJoined else uiState.pastJoined

        if (created.isEmpty() && joined.isEmpty()) {
            if (selectedTab == 0) {
                EmptyState(
                    icon = AppIcons.MyActivities,
                    title = "No plans yet",
                    message = "Join an activity from the Activities tab and it will appear here.",
                )
            } else {
                EmptyState(
                    icon = AppIcons.MyActivities,
                    title = "No past activities",
                    message = "Activities you took part in will be listed here.",
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                activitySection("Created by you", created, onActivityClick)
                activitySection("Joined", joined, onActivityClick)
            }
        }
    }
}

/** Adds a title and its cards to the list. Empty sections are not shown. */
private fun LazyListScope.activitySection(
    title: String,
    items: List<ActivityItem>,
    onActivityClick: (String) -> Unit,
) {
    if (items.isEmpty()) return

    item(key = "title-$title") { SectionHeader(title) }
    items(items, key = { it.activity.id }) { item ->
        ActivityCard(item = item, onClick = { onActivityClick(item.activity.id) })
    }
}
