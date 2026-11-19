package com.joinme.ui.screens.myactivities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.ActivityRepository
import com.joinme.model.ActivityStatus
import com.joinme.ui.components.ActivityItem
import com.joinme.ui.components.toItem
import com.joinme.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * "Upcoming" = OPEN or FULL. "Past" = COMPLETED or CANCELLED.
 * Each group is split into activities created by the user and activities joined.
 */
data class MyActivitiesUiState(
    val upcomingCreated: List<ActivityItem> = emptyList(),
    val upcomingJoined: List<ActivityItem> = emptyList(),
    val pastCreated: List<ActivityItem> = emptyList(),
    val pastJoined: List<ActivityItem> = emptyList(),
    val nextUpId: String? = null,
    val lastUpdated: String? = null,
    val refreshError: String? = null,
    val isRefreshing: Boolean = false,
)

class MyActivitiesViewModel(
    private val repository: ActivityRepository,
    private val userId: String,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<MyActivitiesUiState> =
        combine(
            repository.activities.combine(DateTimeUtils.nowEveryMinute()) { activities, now -> activities to now },
            repository.lastUpdated,
            repository.refreshError,
            isRefreshing,
        ) { (activities, now), lastUpdated, refreshError, refreshing ->
            // Only the activities where the user takes part (the creator is also a participant).
            val myItems = activities
                .map { it.toItem(now, userId) }
                .filter { it.isJoined || it.isCreator }
                .sortedWith(compareBy({ it.activity.date }, { it.activity.startTime }))

            val (upcoming, past) = myItems.partition {
                it.status == ActivityStatus.OPEN || it.status == ActivityStatus.FULL
            }

            MyActivitiesUiState(
                upcomingCreated = upcoming.filter { it.isCreator },
                upcomingJoined = upcoming.filter { !it.isCreator },
                // Most recent first for past activities.
                pastCreated = past.filter { it.isCreator }.reversed(),
                pastJoined = past.filter { !it.isCreator }.reversed(),
                // The first upcoming activity (the list is sorted by date) is highlighted.
                nextUpId = upcoming.firstOrNull()?.activity?.id,
                lastUpdated = lastUpdated,
                refreshError = refreshError,
                isRefreshing = refreshing,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MyActivitiesUiState(),
        )

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
