
package com.joinme.data

data class Weather(
    val time: String,
    val temperature: Double,
    val precipitation: Double,
)

interface WeatherSource {
    fun saved(): Weather?
    suspend fun fetch(): Weather?
}
