package com.joinme.domain

import com.joinme.model.Activity
import com.joinme.model.ActivityStatus
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ActivityRulesTest {

    // Fixed "now" so the tests do not depend on the real date.
    private val now = LocalDateTime(2026, 10, 14, 12, 0)

    private fun tableTennis(participants: List<String> = listOf("creator")) = Activity(
        id = "1",
        type = ActivityType.TABLE_TENNIS,
        date = LocalDate(2026, 10, 14),
        startTime = LocalTime(19, 0),
        durationMinutes = 60,
        location = ResidenceLocation.GAMES_ROOM,
        maxParticipants = 2,
        creatorId = "creator",
        participantIds = participants,
    )

    @Test
    fun newActivityWithFreePlacesIsOpen() {
        assertEquals(ActivityStatus.OPEN, ActivityRules.statusOf(tableTennis(), now))
    }

    @Test
    fun joiningLastPlaceMakesActivityFull() {
        val result = ActivityRules.join(tableTennis(), "me", now)

        assertIs<ActivityResult.Success>(result)
        assertEquals(2, result.activity.participantCount)
        assertEquals(ActivityStatus.FULL, ActivityRules.statusOf(result.activity, now))
    }

    @Test
    fun cannotJoinFullActivity() {
        val full = tableTennis(participants = listOf("creator", "other"))

        val result = ActivityRules.join(full, "me", now)

        assertIs<ActivityResult.Error>(result)
    }

    @Test
    fun cannotJoinTwice() {
        val result = ActivityRules.join(tableTennis(listOf("creator", "me")), "me", now)

        assertIs<ActivityResult.Error>(result)
    }

    @Test
    fun leavingReducesParticipantsAndReopens() {
        val full = tableTennis(participants = listOf("creator", "me"))

        val result = ActivityRules.leave(full, "me", now)

        assertIs<ActivityResult.Success>(result)
        assertEquals(1, result.activity.participantCount)
        assertEquals(ActivityStatus.OPEN, ActivityRules.statusOf(result.activity, now))
    }

    @Test
    fun creatorCannotLeave() {
        assertIs<ActivityResult.Error>(ActivityRules.leave(tableTennis(), "creator", now))
    }

    @Test
    fun onlyCreatorCanCancel() {
        assertIs<ActivityResult.Error>(ActivityRules.cancel(tableTennis(), "someone", now))

        val result = ActivityRules.cancel(tableTennis(), "creator", now)
        assertIs<ActivityResult.Success>(result)
        assertEquals(ActivityStatus.CANCELLED, ActivityRules.statusOf(result.activity, now))
    }

    @Test
    fun activityIsCompletedAfterItEnds() {
        val afterEnd = LocalDateTime(2026, 10, 14, 20, 0) // 19:00 + 60 min

        assertEquals(ActivityStatus.COMPLETED, ActivityRules.statusOf(tableTennis(), afterEnd))
    }

    @Test
    fun activityEndingAfterMidnightEndsNextDay() {
        val lateActivity = tableTennis().copy(startTime = LocalTime(23, 30), durationMinutes = 90)

        val end = ActivityRules.endOf(lateActivity)

        assertEquals(LocalDateTime(2026, 10, 15, 1, 0), end)
        assertTrue(ActivityRules.statusOf(lateActivity, LocalDateTime(2026, 10, 15, 0, 30)) != ActivityStatus.COMPLETED)
    }
}
