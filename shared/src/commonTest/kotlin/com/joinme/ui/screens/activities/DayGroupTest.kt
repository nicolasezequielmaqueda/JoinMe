package com.joinme.ui.screens.activities

import com.joinme.model.Activity
import com.joinme.model.ActivityStatus
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import com.joinme.ui.components.ActivityItem
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class DayGroupTest {

    private val today = LocalDate(2026, 11, 18)

    private fun itemOn(date: LocalDate, id: String) = ActivityItem(
        activity = Activity(
            id = id,
            type = ActivityType.FOOTBALL,
            date = date,
            startTime = LocalTime(17, 0),
            durationMinutes = 60,
            location = ResidenceLocation.SPORTS_COURT,
            maxParticipants = 10,
            creatorId = "creator",
            participantIds = listOf("creator"),
        ),
        status = ActivityStatus.OPEN,
        isJoined = false,
        isCreator = false,
    )

    @Test
    fun groupsByHowSoonTheDayIs() {
        assertEquals(DayGroup.TODAY, dayGroupOf(LocalDate(2026, 11, 18), today))
        assertEquals(DayGroup.TOMORROW, dayGroupOf(LocalDate(2026, 11, 19), today))
        assertEquals(DayGroup.THIS_WEEK, dayGroupOf(LocalDate(2026, 11, 20), today))
        assertEquals(DayGroup.THIS_WEEK, dayGroupOf(LocalDate(2026, 11, 24), today))
        assertEquals(DayGroup.LATER, dayGroupOf(LocalDate(2026, 11, 25), today))
    }

    @Test
    fun anActivityThatStartedYesterdayStillCountsAsToday() {
        assertEquals(DayGroup.TODAY, dayGroupOf(LocalDate(2026, 11, 17), today))
    }

    @Test
    fun theDayChangesAtMidnight() {
        val tomorrow = LocalDate(2026, 11, 19)
        val beforeMidnight = LocalDateTime(2026, 11, 18, 23, 59).date
        val afterMidnight = LocalDateTime(2026, 11, 19, 0, 0).date

        assertEquals(DayGroup.TOMORROW, dayGroupOf(tomorrow, beforeMidnight))
        assertEquals(DayGroup.TODAY, dayGroupOf(tomorrow, afterMidnight))
    }

    @Test
    fun sectionsKeepTheOrderAndSkipEmptyGroups() {
        val items = listOf(
            itemOn(LocalDate(2026, 11, 18), "a"),
            itemOn(LocalDate(2026, 11, 19), "b"),
            itemOn(LocalDate(2026, 11, 19), "c"),
            itemOn(LocalDate(2026, 12, 20), "d"),
        )

        val sections = groupByDay(items, today)

        assertEquals(listOf(DayGroup.TODAY, DayGroup.TOMORROW, DayGroup.LATER), sections.map { it.group })
        assertEquals(listOf("b", "c"), sections[1].items.map { it.activity.id })
    }
}
