package com.joinme.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Small helpers to get the current date and to show dates in a readable way.
 */
@OptIn(ExperimentalTime::class)
object DateTimeUtils {

    /** Current date and time in the time zone of the phone. */
    fun now(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    fun today(): LocalDate = now().date

    /** "Today", "Tomorrow", "Yesterday" or e.g. "Sat 17 Oct". */
    fun formatDate(date: LocalDate, today: LocalDate = today()): String = when (date) {
        today -> "Today"
        today.plus(1, DateTimeUnit.DAY) -> "Tomorrow"
        today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
        else -> {
            val dayName = date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            val monthName = date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            "$dayName ${date.day} $monthName"
        }
    }

    /** Long date for headers, e.g. "Wednesday, 14 October". */
    fun formatLongDate(date: LocalDate): String {
        val dayName = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
        return "$dayName, ${date.day} $monthName"
    }

    /** Always two digits, e.g. "09:05". */
    fun formatTime(time: LocalTime): String =
        "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"

    /** "45 min", "1 h" or "1 h 30 min". */
    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val rest = minutes % 60
        return when {
            hours == 0 -> "$rest min"
            rest == 0 -> "$hours h"
            else -> "$hours h $rest min"
        }
    }

    /**
     * The Material date picker works with milliseconds in UTC.
     * These two functions convert between those milliseconds and a LocalDate.
     */
    fun dateToPickerMillis(date: LocalDate): Long =
        date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

    fun pickerMillisToDate(millis: Long): LocalDate =
        Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
}
