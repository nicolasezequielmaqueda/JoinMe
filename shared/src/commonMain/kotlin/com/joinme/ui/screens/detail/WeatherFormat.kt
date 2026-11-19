package com.joinme.ui.screens.detail

import com.joinme.util.DateTimeUtils
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

private const val OUTDATED_AFTER_HOURS = 3

// Open-Meteo answers for today and the next 15 days (checked against its error message).
private const val FORECAST_DAYS = 15

/** "No rain", "30% chance of rain" or, when some rain is expected, "60% chance of rain · 1.2 mm". */
fun rainText(chance: Int, precipitation: Double): String = when {
    chance <= 0 && precipitation <= 0.0 -> "No rain"
    precipitation <= 0.0 -> "$chance% chance of rain"
    else -> "$chance% chance of rain · $precipitation mm"
}

/** "Weather at 17:00" if the activity is today, otherwise "Weather on Fri 21 Nov, 17:00". */
fun weatherTitle(date: LocalDate, time: LocalTime, today: LocalDate): String =
    if (date == today) "Weather at ${DateTimeUtils.formatTime(time)}"
    else "Weather on ${DateTimeUtils.formatDayMonth(date)}, ${DateTimeUtils.formatTime(time)}"

/** Why there is no forecast for this start, or null if it can be requested. */
fun forecastUnavailableText(date: LocalDate, time: LocalTime, now: LocalDateTime): String? = when {
    LocalDateTime(date, time) <= now -> "No forecast: the activity has already started."
    date > now.date.plus(FORECAST_DAYS, DateTimeUnit.DAY) -> "Forecast not available yet for this date."
    else -> null
}

/** Time of the reading, e.g. "23:30", from the "2026-11-18T23:30" text saved with it. */
fun readingClock(time: String): String = time.substringAfter('T', time).take(5)

/**
 * True when the reading is more than 3 hours older than [now], or its time cannot be read.
 * Both times are local to Łódź, so any zone gives the same difference.
 */
@OptIn(ExperimentalTime::class)
fun isOutdated(readingTime: String, now: LocalDateTime): Boolean {
    val reading = try {
        LocalDateTime.parse(readingTime)
    } catch (_: IllegalArgumentException) {
        return true
    }
    val ageHours = (now.toInstant(TimeZone.UTC) - reading.toInstant(TimeZone.UTC)).inWholeMinutes / 60.0
    return ageHours > OUTDATED_AFTER_HOURS
}
