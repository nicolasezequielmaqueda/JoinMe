package com.joinme.ui.screens.myactivities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.ActivityRepository
import com.joinme.data.CurrentUser
import com.joinme.model.ActivityStatus
import com.joinme.ui.components.ActivityItem
import com.joinme.ui.components.toItem
import com.joinme.util.DateTimeUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * "Upcoming" = OPEN or FULL. "Past" = COMPLETED or CANCELLED.
 * Each group is split into activities created by the user and activities joined.
 */
data class MyActivitiesUiState(
    val upcomingCreated: List<ActivityItem> = emptyList(),
    val upcomingJoined: List<ActivityItem> = emptyList(),
    val pastCreated: List<ActivityItem> = emptyList(),
    val pastJoined: List<ActivityItem> = emptyList(),
)

class MyActivitiesViewModel(repository: ActivityRepository) : ViewModel() {

    val uiState: StateFlow<MyActivitiesUiState> =
        repository.activities.map { activities ->
            val now = DateTimeUtils.now()
            // Only the activities where the user takes part (the creator is also a participant).
            val myItems = activities
                .map { it.toItem(now, CurrentUser.ID) }
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
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MyActivitiesUiState(),
        )
}
