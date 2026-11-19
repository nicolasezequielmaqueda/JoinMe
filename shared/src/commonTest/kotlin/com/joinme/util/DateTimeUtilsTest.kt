package com.joinme.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DateTimeUtilsTest {

    @Test
    fun shortDateTimeHasDayMonthAndTwoDigitTime() {
        assertEquals("18 Nov 14:32", DateTimeUtils.formatShortDateTime(LocalDateTime(2026, 11, 18, 14, 32)))
        assertEquals("3 Jan 09:05", DateTimeUtils.formatShortDateTime(LocalDateTime(2026, 1, 3, 9, 5)))
    }

    @Test
    fun todayUsesTheTimeZoneOfTheResidence() {
        val warsawToday = Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Warsaw")).date

        // Both calls can straddle midnight, so one of the two neighbouring days is accepted.
        assertTrue(DateTimeUtils.today() in listOf(warsawToday, warsawToday.plus(1, DateTimeUnit.DAY)))
    }

    @Test
    fun nowUsesTheTimeZoneOfTheResidence() {
        val asInstant = DateTimeUtils.now().toInstant(TimeZone.of("Europe/Warsaw"))

        assertTrue((Clock.System.now() - asInstant).absoluteValue < 5.seconds)
    }
}
