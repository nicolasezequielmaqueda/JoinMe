package com.joinme.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.ActivityRepository
import com.joinme.data.SupabaseErrors
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
 * @property message short feedback for the user, shown in a toast.
 * @property messageIsError true if [message] explains a problem instead of a success.
 * @property isOffline true if the last refresh failed for lack of connection, so changes would fail too.
 * @property isRetrying true while the list is being refreshed from the offline notice.
 */
data class ActivityDetailUiState(
    val item: ActivityItem? = null,
    val isWorking: Boolean = false,
    val message: String? = null,
    val messageIsError: Boolean = false,
    val isOffline: Boolean = false,
    val isRetrying: Boolean = false,
)

/**
 * Logic of the Activity Detail screen (US2 – Join): join, leave and cancel.
 * The activity id comes from the navigation route.
 */
class ActivityDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ActivityRepository,
    private val userId: String,
) : ViewModel() {

    private val activityId: String = savedStateHandle.get<String>(Routes.ARG_ACTIVITY_ID) ?: ""

    private val isWorking = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)
    private val messageIsError = MutableStateFlow(false)
    private val isRetrying = MutableStateFlow(false)

    val uiState: StateFlow<ActivityDetailUiState> =
        combine(
            repository.activities.combine(DateTimeUtils.nowEveryMinute()) { activities, now -> activities to now },
            isWorking.combine(isRetrying) { working, retrying -> working to retrying },
            message,
            messageIsError,
            repository.refreshError,
        ) { (activities, now), (working, retrying), msg, isError, refreshError ->
            val activity = activities.find { it.id == activityId }
            ActivityDetailUiState(
                item = activity?.toItem(now, userId),
                isWorking = working,
                message = msg,
                messageIsError = isError,
                isOffline = refreshError == SupabaseErrors.OFFLINE,
                isRetrying = retrying,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActivityDetailUiState(),
        )

    fun onJoinClick() = runAction(successMessage = "You joined this activity!") {
        repository.joinActivity(activityId, userId)
    }

    fun onLeaveClick() = runAction(successMessage = "You left this activity.") {
        repository.leaveActivity(activityId, userId)
    }

    fun onCancelConfirmed() = runAction(successMessage = "The activity was cancelled.") {
        repository.cancelActivity(activityId, userId)
    }

    /** The detail cannot pull to refresh, so this is the way out of the offline state. */
    fun onRetryClick() {
        if (isRetrying.value) return

        viewModelScope.launch {
            isRetrying.value = true
            repository.refresh()
            isRetrying.value = false
        }
    }

    /** Called by the screen once the toast has been shown. */
    fun onMessageShown() {
        message.value = null
    }

    /** Runs one action and turns its result into a message for the user. */
    private fun runAction(successMessage: String, action: suspend () -> ActivityResult) {
        if (isWorking.value) return // an action is already running

        viewModelScope.launch {
            isWorking.value = true
            val result = action()
            messageIsError.value = result is ActivityResult.Error
            message.value = when (result) {
                is ActivityResult.Success -> successMessage
                is ActivityResult.Error -> result.message
            }
            isWorking.value = false
        }
    }
}
