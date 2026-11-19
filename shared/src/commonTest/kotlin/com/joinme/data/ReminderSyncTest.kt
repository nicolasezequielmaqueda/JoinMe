package com.joinme.data

import com.joinme.model.Activity
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ReminderSyncTest {

    private val now = LocalDateTime(2026, 11, 18, 12, 0)

    private fun activity(
        id: String,
        date: LocalDate = LocalDate(2026, 11, 20),
        startTime: LocalTime = LocalTime(18, 0),
        participants: List<String> = listOf("creator", "me"),
        max: Int = 8,
        cancelled: Boolean = false,
    ) = Activity(
        id = id,
        type = ActivityType.FOOTBALL,
        date = date,
        startTime = startTime,
        durationMinutes = 60,
        location = ResidenceLocation.SPORTS_COURT,
        maxParticipants = max,
        creatorId = "creator",
        participantIds = participants,
        isCancelled = cancelled,
    )

    @Test
    fun joinedUpcomingActivityGetsAReminder() {
        val reminders = FakeReminderScheduler()

        updateReminder(reminders, activity("a"), "me", now)

        assertEquals(listOf("a"), reminders.scheduled)
        assertEquals(emptyList(), reminders.cancelled)
    }

    @Test
    fun fullActivityStillGetsAReminder() {
        val reminders = FakeReminderScheduler()

        updateReminder(reminders, activity("a", max = 2), "me", now)

        assertEquals(listOf("a"), reminders.scheduled)
    }

    @Test
    fun activityWhereTheUserDoesNotTakePartIsCancelled() {
        val reminders = FakeReminderScheduler()

        updateReminder(reminders, activity("a", participants = listOf("creator")), "me", now)

        assertEquals(emptyList(), reminders.scheduled)
        assertEquals(listOf("a"), reminders.cancelled)
    }

    @Test
    fun cancelledActivityLosesItsReminder() {
        val reminders = FakeReminderScheduler()

        updateReminder(reminders, activity("a", cancelled = true), "me", now)

        assertEquals(emptyList(), reminders.scheduled)
        assertEquals(listOf("a"), reminders.cancelled)
    }

    @Test
    fun activityThatAlreadyStartedHasNoReminder() {
        val reminders = FakeReminderScheduler()
        val started = activity("a", date = LocalDate(2026, 11, 18), startTime = LocalTime(11, 30))

        updateReminder(reminders, started, "me", now)

        assertEquals(emptyList(), reminders.scheduled)
        assertEquals(listOf("a"), reminders.cancelled)
    }

    @Test
    fun syncChecksEveryActivity() {
        val reminders = FakeReminderScheduler()
        val list = listOf(activity("a"), activity("b", cancelled = true), activity("c"))

        syncReminders(reminders, list, "me", now)

        assertEquals(listOf("a", "c"), reminders.scheduled)
        assertEquals(listOf("b"), reminders.cancelled)
    }

    @Test
    fun activityThatTheServerNoLongerReturnsLosesItsReminder() {
        val reminders = FakeReminderScheduler()

        // The first list has "a" and "b"; the next one only has "a".
        syncReminders(reminders, listOf(activity("a"), activity("b")), "me", now)
        syncReminders(reminders, listOf(activity("a")), "me", now)

        assertEquals(setOf("a"), reminders.active)
    }
}
