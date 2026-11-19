package com.joinme.data.remote

import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ActivityRowTest {

    private val row = ActivityRow(
        id = "5f1c0000-0000-0000-0000-000000000001",
        type = "FOOTBALL",
        date = "2026-11-20",
        startTime = "18:30:00",
        durationMinutes = 90,
        location = "SPORTS_COURT",
        maxParticipants = 8,
        description = "Bring water.",
        creatorId = "creator",
        participants = listOf(ParticipantRow("user-b"), ParticipantRow("creator"), ParticipantRow("user-c")),
    )

    @Test
    fun rowIsConvertedToActivity() {
        val activity = row.toActivityOrNull()!!

        assertEquals(row.id, activity.id)
        assertEquals(ActivityType.FOOTBALL, activity.type)
        assertEquals(ResidenceLocation.SPORTS_COURT, activity.location)
        assertEquals(90, activity.durationMinutes)
        assertEquals(8, activity.maxParticipants)
        assertEquals("Bring water.", activity.description)
        assertEquals("creator", activity.creatorId)
        assertEquals(false, activity.isCancelled)
    }

    @Test
    fun dateAndTimeAreParsed() {
        val activity = row.toActivityOrNull()!!

        assertEquals(LocalDate(2026, 11, 20), activity.date)
        assertEquals(LocalTime(18, 30), activity.startTime)
    }

    @Test
    fun creatorIsTheFirstParticipantAndTheOthersKeepTheirOrder() {
        assertEquals(listOf("creator", "user-b", "user-c"), row.toActivityOrNull()!!.participantIds)
    }

    @Test
    fun activityWithoutParticipantsHasNoParticipants() {
        assertTrue(row.copy(participants = emptyList()).toActivityOrNull()!!.participantIds.isEmpty())
    }

    @Test
    fun customNameAndCancelledFlagAreKept() {
        val activity = row.copy(type = "OTHER", customName = "Pizza night", isCancelled = true).toActivityOrNull()!!

        assertEquals(ActivityType.OTHER, activity.type)
        assertEquals("Pizza night", activity.title)
        assertEquals(true, activity.isCancelled)
    }

    @Test
    fun rowWithUnknownTypeIsSkipped() {
        assertNull(row.copy(type = "CURLING").toActivityOrNull())
    }

    @Test
    fun rowWithUnknownLocationIsSkipped() {
        assertNull(row.copy(location = "ROOFTOP").toActivityOrNull())
    }

    @Test
    fun oneBadRowDoesNotRemoveTheOthers() {
        val rows = listOf(row, row.copy(id = "bad", type = "CURLING"), row.copy(id = "other"))

        assertEquals(listOf(row.id, "other"), rows.mapNotNull { it.toActivityOrNull() }.map { it.id })
    }

    @Test
    fun jsonOfSupabaseIsDecoded() {
        val json = """
            {"id":"a-1","type":"DARTS","custom_name":"","date":"2026-11-17","start_time":"18:00:00",
             "duration_minutes":60,"location":"COMMON_ROOM","max_participants":4,"description":"",
             "creator_id":"u-1","is_cancelled":false,"participants":[{"user_id":"u-2"},{"user_id":"u-1"}]}
        """.trimIndent()

        val activity = Json.decodeFromString<ActivityRow>(json).toActivityOrNull()!!

        assertEquals(ActivityType.DARTS, activity.type)
        assertEquals(listOf("u-1", "u-2"), activity.participantIds)
    }
}
