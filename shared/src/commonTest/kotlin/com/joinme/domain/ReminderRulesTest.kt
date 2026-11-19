package com.joinme.domain

import com.joinme.model.Activity
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReminderRulesTest {

    private fun activity(
        date: LocalDate = LocalDate(2026, 10, 14),
        isCancelled: Boolean = false,
    ) = Activity(
        id = "1",
        type = ActivityType.TABLE_TENNIS,
        date = date,
        startTime = LocalTime(19, 0),
        durationMinutes = 60,
        location = ResidenceLocation.GAMES_ROOM,
        maxParticipants = 2,
        creatorId = "creator",
        participantIds = listOf("creator"),
        isCancelled = isCancelled,
    )

    @Test
    fun reminderIsDelayedUntilThirtyMinutesBeforeStart() {
        val now = LocalDateTime(2026, 10, 14, 12, 0)

        // 19:00 - 30 min = 18:30, which is 6 h 30 min after 12:00.
        assertEquals(390L, delayMinutes(activity(), now))
    }

    @Test
    fun reminderIsImmediateExactlyThirtyMinutesBefore() {
        val now = LocalDateTime(2026, 10, 14, 18, 30)

        assertEquals(0L, delayMinutes(activity(), now))
    }

    @Test
    fun noReminderTwentyNineMinutesBefore() {
        val now = LocalDateTime(2026, 10, 14, 18, 31)

        assertNull(delayMinutes(activity(), now))
    }

    @Test
    fun noReminderWhenActivityAlreadyStarted() {
        val now = LocalDateTime(2026, 10, 14, 19, 15)

        assertNull(delayMinutes(activity(), now))
    }

    @Test
    fun noReminderWhenActivityIsCancelled() {
        val now = LocalDateTime(2026, 10, 14, 12, 0)

        assertNull(delayMinutes(activity(isCancelled = true), now))
    }

    @Test
    fun reminderForTomorrowCountsTheMidnightCrossing() {
        val now = LocalDateTime(2026, 10, 14, 12, 0)
        val tomorrow = activity(date = LocalDate(2026, 10, 15))

        // 24 h + 6 h 30 min.
        assertEquals(1830L, delayMinutes(tomorrow, now))
    }
}
