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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

/**
 * Keeps the activity list in memory and saves each change before showing it.
 *
 * @param now gives the current time. It can be replaced in tests.
 */
class LocalActivityRepository(
    initialActivities: List<Activity> = SampleActivities.create(DateTimeUtils.today()),
    private val save: suspend (List<Activity>) -> Boolean = { true },
    private val now: () -> LocalDateTime = { DateTimeUtils.now() },
) : ActivityRepository {

    private val _activities = MutableStateFlow(initialActivities)
    override val activities: StateFlow<List<Activity>> = _activities.asStateFlow()

    // Simple counter to give each new activity a different id.
    private var nextId = (initialActivities.mapNotNull {
        it.id.removePrefix("new-").toIntOrNull()
    }.maxOrNull() ?: 0) + 1
    private val changes = Mutex()

    override fun getActivity(id: String): Activity? =
        _activities.value.find { it.id == id }

    override suspend fun createActivity(form: NewActivityForm, userId: String): ActivityResult = changes.withLock {
        val error = ActivityValidator.validate(form, now())
        if (error != null) return@withLock ActivityResult.Error(error)

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
        saveChange(_activities.value + activity, activity)
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
    private suspend fun applyChange(activityId: String, rule: (Activity) -> ActivityResult): ActivityResult = changes.withLock {
        val activity = getActivity(activityId)
            ?: return@withLock ActivityResult.Error("This activity no longer exists.")

        when (val result = rule(activity)) {
            is ActivityResult.Error -> result
            is ActivityResult.Success -> {
                val updated = _activities.value.map { if (it.id == activityId) result.activity else it }
                saveChange(updated, result.activity)
            }
        }
    }

    private suspend fun saveChange(updated: List<Activity>, activity: Activity): ActivityResult =
        withContext(NonCancellable) {
            if (save(updated)) {
                _activities.value = updated
                ActivityResult.Success(activity)
            } else {
                ActivityResult.Error("Could not save this change. Please try again.")
            }
        }
}
