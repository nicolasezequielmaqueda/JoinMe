package com.joinme.ui.screens.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joinme.data.AppContainer
import com.joinme.ui.components.ActivityCard
import com.joinme.ui.components.EmptyState
import com.joinme.ui.components.LargeTitleHeader
import com.joinme.ui.components.SelectablePill
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.util.DateTimeUtils

/**
 * Main screen (US1 – Discover): list of available activities with filters.
 */
@Composable
fun ActivitiesScreen(
    onActivityClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: ActivitiesViewModel = viewModel { ActivitiesViewModel(AppContainer.activityRepository) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(JoinMeTheme.colors.background)) {
        LargeTitleHeader(
            title = "Activities",
            overline = DateTimeUtils.formatLongDate(DateTimeUtils.today()),
            action = { NewButton(onClick = onCreateClick) },
        )

        FilterRow(
            selected = uiState.selectedFilter,
            onFilterSelected = viewModel::onFilterSelected,
        )

        if (uiState.items.isEmpty()) {
            EmptyState(
                icon = AppIcons.Discover,
                title = "Nothing here yet",
                message = "No activities match this filter. Why not create one?",
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.items, key = { it.activity.id }) { item ->
                    ActivityCard(
                        item = item,
                        onClick = { onActivityClick(item.activity.id) },
                    )
                }
            }
        }
    }
}

/** "+ New" button in the header, to create an activity. */
@Composable
private fun NewButton(onClick: () -> Unit) {
    val colors = JoinMeTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .heightIn(min = 40.dp)
            .clip(CircleShape)
            .background(colors.accent)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Icon(AppIcons.Plus, contentDescription = null, tint = colors.onAccent, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text("New", style = MaterialTheme.typography.labelLarge, color = colors.onAccent)
    }
}

/** Horizontal list of filters. It scrolls, so it also works with big fonts. */
@Composable
private fun FilterRow(selected: ActivityFilter, onFilterSelected: (ActivityFilter) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
