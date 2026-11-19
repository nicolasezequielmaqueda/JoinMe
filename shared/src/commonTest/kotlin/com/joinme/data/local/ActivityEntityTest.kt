package com.joinme.data.local

import com.joinme.model.Activity
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ActivityEntityTest {

    private val activity = Activity(
        id = "a-1",
        type = ActivityType.FOOTBALL,
        date = LocalDate(2026, 11, 20),
        startTime = LocalTime(18, 30),
        durationMinutes = 90,
        location = ResidenceLocation.SPORTS_COURT,
        maxParticipants = 8,
        creatorId = "user-a",
        participantIds = listOf("user-a", "user-b"),
        description = "Bring water.",
    )

    private fun roundTrip(activity: Activity) = activity.toEntity().toActivity()

    @Test
    fun activityKeepsAllFields() {
        assertEquals(activity, roundTrip(activity))
    }

    @Test
    fun emptyParticipantListStaysEmpty() {
        val empty = activity.copy(participantIds = emptyList())

        assertEquals("", empty.toEntity().participantIds)
        assertEquals(empty, roundTrip(empty))
    }

    @Test
    fun cancelledActivityStaysCancelled() {
        val cancelled = activity.copy(isCancelled = true)

        assertEquals(cancelled, roundTrip(cancelled))
    }

    @Test
    fun customNameWithSpecialCharactersIsKept() {
        val other = activity.copy(
            type = ActivityType.OTHER,
            customName = "Pizza, \"film\" & ñandú 🍕; it's 100%\nnew line",
        )

        assertEquals(other, roundTrip(other))
    }

    @Test
    fun participantsAreSavedAsCommaSeparatedText() {
        assertEquals("user-a,user-b", activity.toEntity().participantIds)
    }
}
