package com.joinme.domain

import com.joinme.model.Activity
import kotlinx.datetime.LocalDateTime

const val REMINDER_MINUTES_BEFORE = 30L

private const val SECONDS_PER_MINUTE = 60L
private const val SECONDS_PER_DAY = 24 * 60 * 60L

/**
 * Minutes from [now] until the reminder must be shown (30 minutes before the start).
 * Returns null when there is nothing to schedule: the activity is cancelled
 * or it starts in less than 30 minutes (or has already started).
 */
fun delayMinutes(activity: Activity, now: LocalDateTime): Long? {
    if (activity.isCancelled) return null

    val secondsUntilStart = secondsSinceEpochDay(ActivityRules.startOf(activity)) - secondsSinceEpochDay(now)
    val secondsUntilReminder = secondsUntilStart - REMINDER_MINUTES_BEFORE * SECONDS_PER_MINUTE
    if (secondsUntilReminder < 0) return null

    return secondsUntilReminder / SECONDS_PER_MINUTE
}

// Both dates use the same clock, so the difference needs no time zone.
private fun secondsSinceEpochDay(dateTime: LocalDateTime): Long =
    dateTime.date.toEpochDays() * SECONDS_PER_DAY + dateTime.time.toSecondOfDay()
