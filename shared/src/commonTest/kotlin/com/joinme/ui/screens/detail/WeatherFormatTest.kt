package com.joinme.ui.screens.detail

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WeatherFormatTest {

    private val now = LocalDateTime(2026, 11, 18, 23, 30)

    @Test
    fun readingOfThreeHoursIsStillFresh() {
        assertFalse(isOutdated("2026-11-18T20:30", now))
        assertFalse(isOutdated("2026-11-18T23:00", now))
    }

    @Test
    fun readingOlderThanThreeHoursIsOutdated() {
        assertTrue(isOutdated("2026-11-18T20:15", now))
        assertTrue(isOutdated("2026-11-17T23:30", now))
    }

    @Test
    fun readingThatCannotBeReadIsOutdated() {
        assertTrue(isOutdated("yesterday", now))
    }

    @Test
    fun rainText() {
        assertEquals("No rain", rainText(0, 0.0))
        assertEquals("5% chance of rain", rainText(5, 0.0))
        assertEquals("60% chance of rain · 1.2 mm", rainText(60, 1.2))
    }

    @Test
    fun titleOnlyHasTheDateWhenItIsNotToday() {
        val today = LocalDate(2026, 11, 18)

        assertEquals("Weather at 17:00", weatherTitle(today, LocalTime(17, 0), today))
        assertEquals("Weather on Sat 21 Nov, 09:30", weatherTitle(LocalDate(2026, 11, 21), LocalTime(9, 30), today))
    }

    @Test
    fun forecastExistsForTheNext15Days() {
        assertNull(forecastUnavailableText(LocalDate(2026, 11, 18), LocalTime(23, 45), now))
        assertNull(forecastUnavailableText(LocalDate(2026, 12, 3), LocalTime(18, 0), now))
        assertEquals(
            "Forecast not available yet for this date.",
            forecastUnavailableText(LocalDate(2026, 12, 4), LocalTime(18, 0), now),
        )
    }

    @Test
    fun noForecastOnceTheActivityHasStarted() {
        assertEquals(
            "No forecast: the activity has already started.",
            forecastUnavailableText(LocalDate(2026, 11, 18), LocalTime(23, 30), now),
        )
    }

    @Test
    fun readingClockKeepsOnlyTheTime() {
        assertEquals("23:30", readingClock("2026-11-18T23:30"))
        assertEquals("23:30", readingClock("2026-11-18T23:30:05.123"))
    }
}
