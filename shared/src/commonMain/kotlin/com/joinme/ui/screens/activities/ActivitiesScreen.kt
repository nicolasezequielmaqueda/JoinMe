package com.joinme.ui.screens.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joinme.data.ActivityRepository
import com.joinme.ui.components.ActivityCard
import com.joinme.ui.components.CompactButton
import com.joinme.ui.components.EmptyState
import com.joinme.ui.components.LargeTitleHeader
import com.joinme.ui.components.LocalTabBarHeight
import com.joinme.ui.components.NavBar
import com.joinme.ui.components.NavBarLayout
import com.joinme.ui.components.RefreshStatusLine
import com.joinme.ui.components.SectionHeader
import com.joinme.ui.components.SelectablePill
import com.joinme.ui.components.glassSource
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.util.DateTimeUtils
import androidx.compose.runtime.getValue

private const val TITLE = "Activities"

/**
 * Main screen (US1 – Discover): list of available activities with filters.
 */
@Composable
fun ActivitiesScreen(
    onActivityClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    repository: ActivityRepository,
    userId: String,
    viewModel: ActivitiesViewModel = viewModel { ActivitiesViewModel(repository, userId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scrolled by remember { derivedStateOf { listState.canScrollBackward } }
    val pullState = rememberPullToRefreshState()

    val gutter = Modifier.padding(horizontal = JoinMeDimens.screenGutter)

    NavBarLayout(
        navBar = {
            // The small title and the New button appear when the large ones have scrolled away.
            NavBar(
                title = if (scrolled) TITLE else "",
                scrolled = scrolled,
                action = if (scrolled) {
                    { NewButton(onClick = onCreateClick) }
                } else null,
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
            // No side padding here: the filter chips scroll all the way to the screen edge.
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = barHeight, bottom = LocalTabBarHeight.current + JoinMeDimens.spaceL),
                verticalArrangement = Arrangement.spacedBy(JoinMeDimens.cardGap),
                modifier = Modifier.fillMaxSize().glassSource(),
            ) {
                item(key = "title") {
                    LargeTitleHeader(
                        title = TITLE,
                        overline = DateTimeUtils.formatLongDate(DateTimeUtils.today()),
                        modifier = gutter,
                        action = { NewButton(onClick = onCreateClick) },
                    )
                }
                if (uiState.refreshError != null) {
                    item(key = "status") {
                        RefreshStatusLine(uiState.refreshError, uiState.lastUpdated, modifier = gutter)
                    }
                }
                item(key = "filters") {
                    FilterRow(
                        selected = uiState.selectedFilter,
                        onFilterSelected = viewModel::onFilterSelected,
                    )
                }

                if (uiState.sections.isEmpty()) {
                    item(key = "empty") {
                        ActivitiesEmptyState(
                            uiState = uiState,
                            onShowAll = { viewModel.onFilterSelected(ActivityFilter.ALL) },
                            onCreateClick = onCreateClick,
                            onRetry = viewModel::onRefresh,
                        )
                    }
                }
                uiState.sections.forEach { section ->
                    item(key = "day-${section.group}") {
                        SectionHeader(section.group.label, large = true, count = section.items.size)
                    }
                    items(section.items, key = { it.activity.id }) { item ->
                        ActivityCard(
                            item = item,
                            showDate = false,
                            onClick = { onActivityClick(item.activity.id) },
                            modifier = gutter,
                        )
                    }
                }
                item(key = "demo-note") {
                    Text(
                        text = "Demo activities · Fictional people and places",
                        style = MaterialTheme.typography.bodySmall,
                        color = JoinMeTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = gutter.fillMaxWidth().padding(top = JoinMeDimens.spaceL),
                    )
                }
            }
        }
    }
}

/** Explains why the list is empty: never downloaded (yet), or nothing matches the filter. */
@Composable
private fun ActivitiesEmptyState(
    uiState: ActivitiesUiState,
    onShowAll: () -> Unit,
    onCreateClick: () -> Unit,
    onRetry: () -> Unit,
) {
    if (uiState.lastUpdated != null && uiState.selectedFilter != ActivityFilter.ALL) {
        EmptyState(
            icon = AppIcons.Discover,
            title = "Nothing here yet",
            message = "No activities match this filter.",
            actionText = "Show all",
            onAction = onShowAll,
        )
    } else if (uiState.lastUpdated != null) {
        EmptyState(
            icon = AppIcons.Discover,
            title = "No activities yet",
            message = "Why not create the first one?",
            actionText = "New activity",
            onAction = onCreateClick,
        )
    } else if (uiState.refreshError != null) {
        EmptyState(
            icon = AppIcons.Warning,
            title = "Couldn't load activities",
            message = "Check your connection and try again.",
            actionText = "Try again",
            onAction = onRetry,
        )
    } else {
        EmptyState(
            icon = AppIcons.Discover,
            title = "Loading activities",
            message = "Just a moment.",
            isLoading = true,
        )
    }
}

/** "+ New" button to create an activity. */
@Composable
private fun NewButton(onClick: () -> Unit) {
    CompactButton(text = "New", onClick = onClick, icon = AppIcons.Plus)
}

/** Horizontal list of filters. Users can scroll to reach more options. */
@Composable
private fun FilterRow(selected: ActivityFilter, onFilterSelected: (ActivityFilter) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = JoinMeDimens.screenGutter),
        horizontalArrangement = Arrangement.spacedBy(JoinMeDimens.spaceS),
    ) {
        items(ActivityFilter.entries) { filter ->
            SelectablePill(
                text = filter.label,
                selected = filter == selected,
                onClick = { onFilterSelected(filter) },
            )
        }
    }
}
