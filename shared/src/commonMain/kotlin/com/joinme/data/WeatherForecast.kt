package com.joinme.data

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** The forecast has one value per hour, so 17:45 uses the one of "2026-11-21T17:00". */
internal fun forecastHour(date: LocalDate, time: LocalTime): String =
    "${date}T${time.hour.toString().padStart(2, '0')}:00"

/**
 * Picks the values of the hour of [time] from the hourly response of Open-Meteo.
 * Returns null if the response does not include that hour. Throws if the response is malformed.
 */
fun parseForecast(body: String, date: LocalDate, time: LocalTime, updated: String): Weather? {
    val hourly = Json.parseToJsonElement(body).jsonObject["hourly"]?.jsonObject ?: return null
    val hour = forecastHour(date, time)
    val index = hourly["time"]?.jsonArray?.indexOfFirst { it.jsonPrimitive.content == hour } ?: -1
    if (index < 0) return null

    val temperature = hourly.valueAt("temperature_2m", index)
    val rainChance = hourly.valueAt("precipitation_probability", index)
    val precipitation = hourly.valueAt("precipitation", index)
    require(temperature.isFinite() && precipitation.isFinite() && precipitation >= 0 && rainChance in 0.0..100.0)

    return Weather(temperature, rainChance.toInt(), precipitation, updated)
}

private fun JsonObject.valueAt(name: String, index: Int): Double =
    this[name]?.jsonArray?.getOrNull(index)?.jsonPrimitive?.doubleOrNull
        ?: throw IllegalArgumentException("The forecast has no value for $name")
