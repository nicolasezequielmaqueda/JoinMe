package com.joinme.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.ActivityRepository
import com.joinme.data.CurrentUser
import com.joinme.domain.ActivityResult
import com.joinme.ui.components.ActivityItem
import com.joinme.ui.components.toItem
import com.joinme.ui.navigation.Routes
import com.joinme.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * @property item null if the activity does not exist (e.g. wrong id).
 * @property isWorking true while an action is running, to avoid double taps.
 * @property message short feedback for the user, shown in a snackbar.
 */
data class ActivityDetailUiState(
    val item: ActivityItem? = null,
    val isWorking: Boolean = false,
    val message: String? = null,
)

/**
 * Logic of the Activity Detail screen (US2 – Join): join, leave and cancel.
 * The activity id comes from the navigation route.
 */
class ActivityDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ActivityRepository,
) : ViewModel() {

    private val activityId: String = savedStateHandle.get<String>(Routes.ARG_ACTIVITY_ID) ?: ""

    private val isWorking = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ActivityDetailUiState> =
        combine(repository.activities, isWorking, message) { activities, working, msg ->
            val activity = activities.find { it.id == activityId }
            ActivityDetailUiState(
                item = activity?.toItem(DateTimeUtils.now(), CurrentUser.ID),
                isWorking = working,
                message = msg,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActivityDetailUiState(),
        )

    fun onJoinClick() = runAction(successMessage = "You joined this activity!") {
        repository.joinActivity(activityId, CurrentUser.ID)
    }

    fun onLeaveClick() = runAction(successMessage = "You left this activity.") {
        repository.leaveActivity(activityId, CurrentUser.ID)
    }

    fun onCancelConfirmed() = runAction(successMessage = "The activity was cancelled.") {
        repository.cancelActivity(activityId, CurrentUser.ID)
    }

    /** Called by the screen once the snackbar has been shown. */
    fun onMessageShown() {
        message.value = null
    }

    /** Runs one action and turns its result into a message for the user. */
    private fun runAction(successMessage: String, action: suspend () -> ActivityResult) {
        if (isWorking.value) return // an action is already running

        viewModelScope.launch {
            isWorking.value = true
            val result = action()
            message.value = when (result) {
                is ActivityResult.Success -> successMessage
                is ActivityResult.Error -> result.message
            }
            isWorking.value = false
        }
    }
}
