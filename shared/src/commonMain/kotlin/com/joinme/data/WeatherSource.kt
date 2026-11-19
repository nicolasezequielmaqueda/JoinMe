package com.joinme.data

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** Forecast for one hour. [updated] is when it was downloaded, e.g. "2026-11-19T14:32". */
data class Weather(
    val temperature: Double,
    val rainChance: Int,
    val precipitation: Double,
    val updated: String,
)

interface WeatherSource {
    /** The last saved forecast, only if it covers this date and hour. */
    fun saved(date: LocalDate, time: LocalTime): Weather?

    /** Downloads the forecast for the hour of [time]. Null if the service has no data for it. */
    suspend fun fetch(date: LocalDate, time: LocalTime): Weather?
}
