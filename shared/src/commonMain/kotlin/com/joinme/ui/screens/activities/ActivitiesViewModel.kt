package com.joinme.ui.screens.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.ActivityRepository
import com.joinme.data.CurrentUser
import com.joinme.model.ActivityStatus
import com.joinme.ui.components.ActivityItem
import com.joinme.ui.components.toItem
import com.joinme.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ActivitiesUiState(
    val selectedFilter: ActivityFilter = ActivityFilter.ALL,
    val items: List<ActivityItem> = emptyList(),
)

/**
 * Prepares the list of the Activities screen.
 * Only activities that can still happen (OPEN or FULL) are shown, sorted by date.
 */
class ActivitiesViewModel(repository: ActivityRepository) : ViewModel() {

    private val selectedFilter = MutableStateFlow(ActivityFilter.ALL)

    val uiState: StateFlow<ActivitiesUiState> =
        combine(repository.activities, selectedFilter) { activities, filter ->
            val now = DateTimeUtils.now()
            val items = activities
                .filter { filter.matches(it, now.date) }
                .map { it.toItem(now, CurrentUser.ID) }
                .filter { it.status == ActivityStatus.OPEN || it.status == ActivityStatus.FULL }
                .sortedWith(compareBy({ it.activity.date }, { it.activity.startTime }))
            ActivitiesUiState(selectedFilter = filter, items = items)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActivitiesUiState(),
        )

    fun onFilterSelected(filter: ActivityFilter) {
        selectedFilter.value = filter
    }
}
