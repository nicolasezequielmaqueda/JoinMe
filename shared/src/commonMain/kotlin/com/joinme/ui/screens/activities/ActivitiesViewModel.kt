package com.joinme.ui.screens.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.ActivityRepository
import com.joinme.model.ActivityStatus
import com.joinme.ui.components.toItem
import com.joinme.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActivitiesUiState(
    val selectedFilter: ActivityFilter = ActivityFilter.ALL,
    val sections: List<DaySection> = emptyList(),
    val lastUpdated: String? = null,
    val refreshError: String? = null,
    val isRefreshing: Boolean = false,
)

/**
 * Prepares the list of the Activities screen.
 * Only activities that can still happen (OPEN or FULL) are shown, sorted by date and split by day.
 */
class ActivitiesViewModel(
    private val repository: ActivityRepository,
    private val userId: String,
) : ViewModel() {

    private val selectedFilter = MutableStateFlow(ActivityFilter.ALL)
    private val isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<ActivitiesUiState> =
        combine(
            repository.activities.combine(DateTimeUtils.nowEveryMinute()) { activities, now -> activities to now },
            selectedFilter,
            repository.lastUpdated,
            repository.refreshError,
            isRefreshing,
        ) { (activities, now), filter, lastUpdated, refreshError, refreshing ->
            val items = activities
                .filter { filter.matches(it, now.date) }
                .map { it.toItem(now, userId) }
                .filter { it.status == ActivityStatus.OPEN || it.status == ActivityStatus.FULL }
                .sortedWith(compareBy({ it.activity.date }, { it.activity.startTime }))
            ActivitiesUiState(filter, groupByDay(items, now.date), lastUpdated, refreshError, refreshing)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActivitiesUiState(),
        )

    fun onFilterSelected(filter: ActivityFilter) {
        selectedFilter.value = filter
    }

    /** Pull to refresh. If it fails, the repository keeps the error for the status line. */
    fun onRefresh() {
        if (isRefreshing.value) return

        viewModelScope.launch {
            isRefreshing.value = true
            repository.refresh()
            isRefreshing.value = false
        }
    }
}
