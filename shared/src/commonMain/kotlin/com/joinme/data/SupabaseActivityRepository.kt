package com.joinme.data

import com.joinme.data.local.ActivityDao
import com.joinme.data.local.SyncInfoEntity
import com.joinme.data.local.toActivity
import com.joinme.data.local.toEntity
import com.joinme.data.remote.ACTIVITY_COLUMNS
import com.joinme.data.remote.ActivityRow
import com.joinme.data.remote.toActivityOrNull
import com.joinme.domain.ActivityResult
import com.joinme.domain.ActivityRules
import com.joinme.domain.ActivityValidator
import com.joinme.model.Activity
import com.joinme.model.ActivityStatus
import com.joinme.model.NewActivityForm
import com.joinme.util.DateTimeUtils
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Activities of the Supabase database. The screens always read from the Room copy on the device.
 * Every change is checked here with the same rules as the database, sent to Supabase and then
 * the list is downloaded again, so the local copy never invents changes.
 *
 * @param now gives the current time. It can be replaced in tests.
 */
class SupabaseActivityRepository(
    private val supabase: SupabaseClient,
    private val dao: ActivityDao,
    private val reminders: ReminderScheduler,
    scope: CoroutineScope,
    private val now: () -> LocalDateTime = { DateTimeUtils.now() },
) : ActivityRepository {

    override val activities: StateFlow<List<Activity>> =
        dao.observeAll()
            .map { rows -> rows.map { it.toActivity() } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val lastUpdated: StateFlow<String?> =
        dao.observeSyncInfo()
            .map { it?.lastUpdated }
            .stateIn(scope, SharingStarted.Eagerly, null)

    private val _refreshError = MutableStateFlow<String?>(null)
    override val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    private val changes = Mutex()

    override fun getActivity(id: String): Activity? =
        activities.value.find { it.id == id }

    override suspend fun refresh(): String? = changes.withLock { downloadActivities() }

    override suspend fun clearLocalData() = changes.withLock {
        withContext(NonCancellable) {
            removeDeviceData(dao, reminders)
            _refreshError.value = null
        }
    }

    override suspend fun createActivity(form: NewActivityForm, userId: String): ActivityResult = changes.withLock {
        val error = ActivityValidator.validate(form, now())
        if (error != null) return@withLock ActivityResult.Error(error)

        sendChange("Creating an activity", userId) {
            val id = supabase.postgrest.rpc("create_activity", createParameters(form)).decodeAs<String>()

            // The validator already checked these fields, so "!!" cannot fail here.
            Activity(
                id = id,
                type = form.type!!,
                customName = form.customName.trim(),
                date = form.date!!,
                startTime = form.startTime!!,
                durationMinutes = form.durationMinutes,
                location = form.location!!,
                maxParticipants = form.maxParticipants,
                creatorId = userId,
                participantIds = listOf(userId),
                description = form.description.trim(),
            )
        }
    }

    override suspend fun joinActivity(activityId: String, userId: String): ActivityResult =
        applyChange(activityId, userId, "Joining", "join_activity") { ActivityRules.join(it, userId, now()) }

    override suspend fun leaveActivity(activityId: String, userId: String): ActivityResult =
        applyChange(activityId, userId, "Leaving", "leave_activity") { ActivityRules.leave(it, userId, now()) }

    override suspend fun cancelActivity(activityId: String, userId: String): ActivityResult =
        applyChange(activityId, userId, "Cancelling", "cancel_activity") { ActivityRules.cancel(it, userId, now()) }

    /** Checks the rule on the local copy, so a wrong tap gets an answer without network. */
    private suspend fun applyChange(
        activityId: String,
        userId: String,
        actionName: String,
        function: String,
        rule: (Activity) -> ActivityResult,
    ): ActivityResult = changes.withLock {
        val activity = getActivity(activityId)
            ?: return@withLock ActivityResult.Error("This activity no longer exists.")

        when (val result = rule(activity)) {
            is ActivityResult.Error -> result
            is ActivityResult.Success -> sendChange(actionName, userId) {
                supabase.postgrest.rpc(function, buildJsonObject { put("p_activity_id", activityId) })
                result.activity
            }
        }
    }

    /**
     * Runs [send] (the call to Supabase, which returns the changed activity) and saves the result.
     * It is not cancelled halfway, so the device never forgets a change the server already accepted.
     */
    private suspend fun sendChange(
        actionName: String,
        userId: String,
        send: suspend () -> Activity,
    ): ActivityResult = withContext(NonCancellable) {
        val changed = try {
            send()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // A rejection (e.g. "full") means the card was out of date, so it is downloaded again.
            if (SupabaseErrors.shouldRefreshAfter(e)) downloadActivities()
            return@withContext ActivityResult.Error(SupabaseErrors.messageForRpc(e, actionName))
        }

        // Shown even if the download below fails (e.g. the connection dropped in between).
        dao.insertAll(listOf(changed.toEntity()))
        updateReminder(reminders, changed, userId, now())
        downloadActivities()
        ActivityResult.Success(changed)
    }

    /** Replaces the local copy with the server list. If it fails the local copy is not touched. */
    private suspend fun downloadActivities(): String? {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return failRefresh(SupabaseErrors.messageForCode("NOT_AUTHENTICATED"))

        // Old activities are not downloaded, so the list does not grow forever.
        val since = now().date.minus(DAYS_OF_HISTORY, DateTimeUnit.DAY)

        try {
            val downloaded = supabase.postgrest["activities"]
                .select(Columns.raw(ACTIVITY_COLUMNS)) {
                    filter { gte("date", since.toString()) }
                    order("joined_at", Order.ASCENDING, referencedTable = "participants")
                }
                .decodeList<ActivityRow>()
                .mapNotNull { it.toActivityOrNull() }

            dao.replaceAll(downloaded.map { it.toEntity() })
            dao.saveSyncInfo(SyncInfoEntity(lastUpdated = DateTimeUtils.formatShortDateTime(now())))
            syncReminders(reminders, downloaded, userId, now())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return failRefresh(SupabaseErrors.messageForRpc(e))
        }

        _refreshError.value = null
        return null
    }

    private fun failRefresh(message: String): String {
        _refreshError.value = message
        return message
    }

    private fun createParameters(form: NewActivityForm): JsonObject = buildJsonObject {
        // The validator already checked these fields, so "!!" cannot fail here.
        put("p_type", form.type!!.name)
        put("p_date", form.date.toString())
        put("p_start_time", form.startTime.toString())
        put("p_duration_minutes", form.durationMinutes)
        put("p_location", form.location!!.name)
        put("p_max_participants", form.maxParticipants)
        put("p_custom_name", form.customName.trim())
        put("p_description", form.description.trim())
    }

    private companion object {
        const val DAYS_OF_HISTORY = 7
    }
}

/** Removes everything of the previous user that is saved on the device. */
internal suspend fun removeDeviceData(dao: ActivityDao, reminders: ReminderScheduler) {
    dao.deleteAll()
    dao.deleteSyncInfo()
    reminders.cancelAll()
}

/** Reminder only for activities where the user takes part, that can still happen and have not started. */
internal fun updateReminder(reminders: ReminderScheduler, activity: Activity, userId: String, now: LocalDateTime) {
    val status = ActivityRules.statusOf(activity, now)
    val canStillHappen = status == ActivityStatus.OPEN || status == ActivityStatus.FULL
    val notStarted = ActivityRules.startOf(activity) > now

    if (ActivityRules.isParticipant(activity, userId) && canStillHappen && notStarted) {
        reminders.schedule(activity)
    } else {
        reminders.cancel(activity.id)
    }
}

internal fun syncReminders(reminders: ReminderScheduler, activities: List<Activity>, userId: String, now: LocalDateTime) {
    // WorkManager replaces jobs with the same id, so scheduling again is safe.
    // Cancelling first also removes reminders of activities the server no longer returns.
    reminders.cancelAll()
    activities.forEach { updateReminder(reminders, it, userId, now) }
}
