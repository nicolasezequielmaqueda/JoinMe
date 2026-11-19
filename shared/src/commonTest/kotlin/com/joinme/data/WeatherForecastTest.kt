package com.joinme.data

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class WeatherForecastTest {

    private val day = LocalDate(2026, 11, 21)

    // Real response of Open-Meteo for Łódź on 21 Nov 2026 (the same URL the app requests).
    private val response = """
        {"latitude":51.764503,"longitude":19.453964,"generationtime_ms":0.07,"utc_offset_seconds":3600,
        "timezone":"Europe/Warsaw","timezone_abbreviation":"GMT+1","elevation":215.0,
        "hourly_units":{"time":"iso8601","temperature_2m":"°C","precipitation_probability":"%","precipitation":"mm"},
        "hourly":{"time":["2026-11-21T00:00","2026-11-21T01:00","2026-11-21T02:00","2026-11-21T03:00","2026-11-21T04:00","2026-11-21T05:00","2026-11-21T06:00","2026-11-21T07:00","2026-11-21T08:00","2026-11-21T09:00","2026-11-21T10:00","2026-11-21T11:00","2026-11-21T12:00","2026-11-21T13:00","2026-11-21T14:00","2026-11-21T15:00","2026-11-21T16:00","2026-11-21T17:00","2026-11-21T18:00","2026-11-21T19:00","2026-11-21T20:00","2026-11-21T21:00","2026-11-21T22:00","2026-11-21T23:00"],
        "temperature_2m":[1.7,1.5,1.4,1.2,1.3,1.6,2.3,1.2,0.3,0.4,1.1,1.9,2.7,3.0,3.1,2.7,2.0,2.7,2.3,1.3,2.0,1.8,2.0,2.5],
        "precipitation_probability":[8,4,4,5,6,4,2,0,0,0,0,1,1,2,3,3,4,5,7,10,14,19,25,35],
        "precipitation":[0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00]}}
    """.trimIndent()

    @Test
    fun readsTheHourOfTheActivity() {
        val reading = parseForecast(response, day, LocalTime(17, 0), "2026-11-19T14:32")!!

        assertEquals(2.7, reading.temperature)
        assertEquals(5, reading.rainChance)
        assertEquals(0.0, reading.precipitation)
        assertEquals("2026-11-19T14:32", reading.updated)
    }

    @Test
    fun aStartTimeInsideTheHourUsesThatHour() {
        val reading = parseForecast(response, day, LocalTime(23, 45), "now")!!

        assertEquals(35, reading.rainChance)
    }

    @Test
    fun hourKeyHasTwoDigits() {
        assertEquals("2026-11-21T07:00", forecastHour(day, LocalTime(7, 30)))
    }

    @Test
    fun anotherDayIsNotFound() {
        assertNull(parseForecast(response, LocalDate(2026, 11, 22), LocalTime(17, 0), "now"))
    }

    @Test
    fun responseWithoutHourlyDataReturnsNoData() {
        assertNull(parseForecast("""{"error":true,"reason":"out of range"}""", day, LocalTime(17, 0), "now"))
    }

    @Test
    fun rejectsResponseWithMissingValues() {
        val withoutTemperature = """{"hourly":{"time":["2026-11-21T17:00"],"precipitation_probability":[5],"precipitation":[0.0]}}"""

        assertFailsWith<IllegalArgumentException> { parseForecast(withoutTemperature, day, LocalTime(17, 0), "now") }
    }

    @Test
    fun rejectsResponseThatIsNotJson() {
        assertFailsWith<IllegalArgumentException> { parseForecast("<html>", day, LocalTime(17, 0), "now") }
    }
}
