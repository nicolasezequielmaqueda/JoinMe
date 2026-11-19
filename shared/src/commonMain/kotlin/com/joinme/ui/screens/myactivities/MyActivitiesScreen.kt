package com.joinme.ui.screens.myactivities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joinme.data.ActivityRepository
import com.joinme.ui.components.ActivityCard
import com.joinme.ui.components.ActivityItem
import com.joinme.ui.components.EmptyState
import com.joinme.ui.components.LargeTitleHeader
import com.joinme.ui.components.LocalTabBarHeight
import com.joinme.ui.components.NavBar
import com.joinme.ui.components.NavBarLayout
import com.joinme.ui.components.RefreshStatusLine
import com.joinme.ui.components.SectionHeader
import com.joinme.ui.components.SegmentedControl
import com.joinme.ui.components.glassSource
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

private val TABS = listOf("Upcoming", "Past")
private const val TITLE = "My Activities"

/**
 * Activities created or joined by the user, split in Upcoming and Past.
 */
@Composable
fun MyActivitiesScreen(
    onActivityClick: (String) -> Unit,
    repository: ActivityRepository,
    userId: String,
    notificationsAllowed: Boolean,
    onBrowseClick: () -> Unit,
    onAccountClick: (() -> Unit)? = null,
    viewModel: MyActivitiesViewModel = viewModel { MyActivitiesViewModel(repository, userId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    val scrolled by remember { derivedStateOf { listState.canScrollBackward } }
    val pullState = rememberPullToRefreshState()

    val created = if (selectedTab == 0) uiState.upcomingCreated else uiState.pastCreated
    val joined = if (selectedTab == 0) uiState.upcomingJoined else uiState.pastJoined

    val gutter = Modifier.padding(horizontal = JoinMeDimens.screenGutter)
    val nextUpId = if (selectedTab == 0) uiState.nextUpId else null

    NavBarLayout(
        navBar = {
            NavBar(
                title = if (scrolled) TITLE else "",
                scrolled = scrolled,
                action = if (onAccountClick == null) null else {
                    {
                        IconButton(onClick = onAccountClick) {
                            Icon(
                                imageVector = AppIcons.Account,
                                contentDescription = "Account",
                                tint = JoinMeTheme.colors.accent,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                },
            )
        },
        modifier = Modifier.background(JoinMeTheme.colors.background),
    ) { barHeight ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::onRefresh,
            state = pullState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullState,
                    isRefreshing = uiState.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = barHeight),
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = barHeight, bottom = LocalTabBarHeight.current + JoinMeDimens.spaceL),
                verticalArrangement = Arrangement.spacedBy(JoinMeDimens.cardGap),
                modifier = Modifier.fillMaxSize().glassSource(),
            ) {
                item(key = "title") { LargeTitleHeader(title = TITLE, modifier = gutter) }
                if (uiState.refreshError != null) {
                    item(key = "status") { RefreshStatusLine(uiState.refreshError, uiState.lastUpdated, modifier = gutter) }
                }
                if (!notificationsAllowed) {
                    item(key = "reminders") {
                        Text(
                            text = "Reminders are off. Allow notifications in Settings to get a reminder 30 minutes before.",
                            style = MaterialTheme.typography.bodySmall,
                            color = JoinMeTheme.colors.textSecondary,
                            modifier = gutter.padding(horizontal = 4.dp),
                        )
                    }
                }
                item(key = "tabs") {
                    SegmentedControl(
                        options = TABS,
                        selectedIndex = selectedTab,
                        onSelect = { selectedTab = it },
                        modifier = gutter,
                    )
                }

                if (created.isEmpty() && joined.isEmpty()) {
                    item(key = "empty") { MyActivitiesEmptyState(uiState, selectedTab, onBrowseClick) }
                } else {
                    activitySection("Created by you", created, nextUpId, gutter, onActivityClick)
                    activitySection("Joined", joined, nextUpId, gutter, onActivityClick)
                }
            }
        }
    }
}

@Composable
private fun MyActivitiesEmptyState(uiState: MyActivitiesUiState, selectedTab: Int, onBrowseClick: () -> Unit) {
    if (uiState.lastUpdated == null) {
        if (uiState.refreshError == null) {
            EmptyState(
                icon = AppIcons.MyActivities,
                title = "Loading activities",
                message = "Just a moment.",
                isLoading = true,
            )
        } else {
            EmptyState(
                icon = AppIcons.Warning,
                title = "Couldn't load activities",
                message = "Connect to the internet and pull down to try again.",
            )
        }
    } else if (selectedTab == 0) {
        EmptyState(
            icon = AppIcons.MyActivities,
            title = "No plans yet",
            message = "Join an activity from the Activities tab and it will appear here.",
            actionText = "Browse activities",
            onAction = onBrowseClick,
        )
    } else {
        EmptyState(
            icon = AppIcons.MyActivities,
            title = "No past activities",
            message = "Activities you took part in will be listed here.",
            actionText = "Browse activities",
            onAction = onBrowseClick,
        )
    }
}

/** Adds a title with the number of cards and the cards. Empty sections are not shown. */
private fun LazyListScope.activitySection(
    title: String,
    items: List<ActivityItem>,
    nextUpId: String?,
    cardModifier: Modifier,
    onActivityClick: (String) -> Unit,
) {
    if (items.isEmpty()) return

    item(key = "title-$title") { SectionHeader(title, count = items.size) }
    items(items, key = { it.activity.id }) { item ->
        ActivityCard(
            item = item,
            highlighted = item.activity.id == nextUpId,
            modifier = cardModifier,
            onClick = { onActivityClick(item.activity.id) },
        )
    }
}
