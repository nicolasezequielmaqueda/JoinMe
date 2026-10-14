package com.joinme.data

import com.joinme.domain.ActivityResult
import com.joinme.domain.ActivityRules
import com.joinme.domain.ActivityValidator
import com.joinme.model.Activity
import com.joinme.model.NewActivityForm
import com.joinme.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime

/**
 * In-memory repository for the M1 prototype. Data is lost when the app closes.
 *
 * @param now gives the current time. It can be replaced in tests.
 */
class FakeActivityRepository(
    initialActivities: List<Activity> = SampleActivities.create(DateTimeUtils.today()),
    private val now: () -> LocalDateTime = { DateTimeUtils.now() },
) : ActivityRepository {

    private val _activities = MutableStateFlow(initialActivities)
    override val activities: StateFlow<List<Activity>> = _activities.asStateFlow()

    // Simple counter to give each new activity a different id.
    private var nextId = 1

    override fun getActivity(id: String): Activity? =
        _activities.value.find { it.id == id }

    override suspend fun createActivity(form: NewActivityForm, userId: String): ActivityResult {
        val error = ActivityValidator.validate(form, now())
        if (error != null) return ActivityResult.Error(error)

        // The validator already checked these fields, so "!!" cannot fail here.
        val activity = Activity(
            id = "new-${nextId++}",
            type = form.type!!,
            customName = form.customName.trim(),
            date = form.date!!,
            startTime = form.startTime!!,
            durationMinutes = form.durationMinutes,
            location = form.location!!,
            maxParticipants = form.maxParticipants,
            creatorId = userId,
            participantIds = listOf(userId), // the creator is the first participant
            description = form.description.trim(),
        )
        _activities.update { it + activity }
        return ActivityResult.Success(activity)
    }

    override suspend fun joinActivity(activityId: String, userId: String): ActivityResult =
        applyChange(activityId) { ActivityRules.join(it, userId, now()) }

    override suspend fun leaveActivity(activityId: String, userId: String): ActivityResult =
        applyChange(activityId) { ActivityRules.leave(it, userId, now()) }

    override suspend fun cancelActivity(activityId: String, userId: String): ActivityResult =
        applyChange(activityId) { ActivityRules.cancel(it, userId, now()) }

    /**
     * Finds the activity, applies a rule and, if it succeeds,
     * replaces the old activity with the new one in the list.
     */
    private fun applyChange(activityId: String, rule: (Activity) -> ActivityResult): ActivityResult {
        val activity = getActivity(activityId)
            ?: return ActivityResult.Error("This activity no longer exists.")

        val result = rule(activity)
        if (result is ActivityResult.Success) {
            _activities.update { list ->
                list.map { if (it.id == activityId) result.activity else it }
            }
        }
        return result
    }
}
