package com.joinme.domain

import com.joinme.model.Activity
import com.joinme.model.ActivityStatus
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

/**
 * Business rules of JoinMe: activity status, join, leave and cancel.
 *
 * These functions do not change anything: they receive an activity and return
 * a new copy. This makes them easy to test (see ActivityRulesTest).
 * "now" is a parameter so tests can use a fixed date.
 */
object ActivityRules {

    private const val SECONDS_PER_DAY = 24 * 60 * 60

    /** Date and time when the activity starts. */
    fun startOf(activity: Activity): LocalDateTime =
        LocalDateTime(activity.date, activity.startTime)

    /** Date and time when the activity ends (it may finish the next day). */
    fun endOf(activity: Activity): LocalDateTime {
        val endInSeconds = activity.startTime.toSecondOfDay() + activity.durationMinutes * 60
        val extraDays = endInSeconds / SECONDS_PER_DAY
        val endTime = LocalTime.fromSecondOfDay(endInSeconds % SECONDS_PER_DAY)
        val endDate = activity.date.plus(extraDays, DateTimeUnit.DAY)
        return LocalDateTime(endDate, endTime)
    }

    /**
     * Calculates the status. The order matters:
     * a cancelled activity stays CANCELLED even after its date has passed.
     */
    fun statusOf(activity: Activity, now: LocalDateTime): ActivityStatus = when {
        activity.isCancelled -> ActivityStatus.CANCELLED
        endOf(activity) <= now -> ActivityStatus.COMPLETED
        activity.participantCount >= activity.maxParticipants -> ActivityStatus.FULL
        else -> ActivityStatus.OPEN
    }

    fun isParticipant(activity: Activity, userId: String): Boolean =
        userId in activity.participantIds

    fun isCreator(activity: Activity, userId: String): Boolean =
        activity.creatorId == userId

    fun join(activity: Activity, userId: String, now: LocalDateTime): ActivityResult {
        if (isParticipant(activity, userId)) {
            return ActivityResult.Error("You have already joined this activity.")
        }
        return when (statusOf(activity, now)) {
            ActivityStatus.OPEN -> ActivityResult.Success(
                activity.copy(participantIds = activity.participantIds + userId)
            )
            ActivityStatus.FULL -> ActivityResult.Error("This activity is full.")
            ActivityStatus.CANCELLED -> ActivityResult.Error("This activity was cancelled.")
            ActivityStatus.COMPLETED -> ActivityResult.Error("This activity has already finished.")
        }
    }

    fun leave(activity: Activity, userId: String, now: LocalDateTime): ActivityResult {
        if (!isParticipant(activity, userId)) {
            return ActivityResult.Error("You are not part of this activity.")
        }
        // The creator cannot leave, otherwise the activity would have no owner.
        if (isCreator(activity, userId)) {
            return ActivityResult.Error("You created this activity. Cancel it instead.")
        }
        if (statusOf(activity, now) == ActivityStatus.COMPLETED) {
            return ActivityResult.Error("This activity has already finished.")
        }
        return ActivityResult.Success(
            activity.copy(participantIds = activity.participantIds - userId)
        )
    }

    fun cancel(activity: Activity, userId: String, now: LocalDateTime): ActivityResult {
        if (!isCreator(activity, userId)) {
            return ActivityResult.Error("Only the creator can cancel this activity.")
        }
        return when (statusOf(activity, now)) {
            ActivityStatus.CANCELLED -> ActivityResult.Error("This activity is already cancelled.")
            ActivityStatus.COMPLETED -> ActivityResult.Error("This activity has already finished.")
            else -> ActivityResult.Success(activity.copy(isCancelled = true))
        }
    }
}
