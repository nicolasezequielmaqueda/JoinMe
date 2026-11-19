package com.joinme

import android.content.Context
import android.util.Log
import com.joinme.data.Weather
import com.joinme.data.WeatherSource
import com.joinme.data.parseForecast
import com.joinme.util.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI

class OpenMeteoWeather(context: Context) : WeatherSource {
    private val settings = context.getSharedPreferences("joinme_weather", Context.MODE_PRIVATE)

    // The saved response only has the hours of one day, so for another activity the hour is not found.
    override fun saved(date: LocalDate, time: LocalTime): Weather? {
        val body = settings.getString("last_forecast", null) ?: return null
        val updated = settings.getString("last_forecast_updated", null) ?: return null
        return try {
            parseForecast(body, date, time, updated)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    override suspend fun fetch(date: LocalDate, time: LocalTime): Weather? = withContext(Dispatchers.IO) {
        // The demo uses a fixed city. No device location is requested.
        val address = "https://api.open-meteo.com/v1/forecast?latitude=51.76&longitude=19.46" +
            "&hourly=temperature_2m,precipitation_probability,precipitation" +
            "&start_date=$date&end_date=$date&timezone=Europe%2FWarsaw"
        val request = URI(address).toURL().openConnection() as HttpURLConnection
        try {
            request.connectTimeout = 5000
            request.readTimeout = 5000
            if (request.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("Weather request failed")
            }
            val body = request.inputStream.bufferedReader().use { it.readText() }
            val updated = DateTimeUtils.now().toString()
            val weather = parseForecast(body, date, time, updated)
            if (weather != null) {
                settings.edit()
                    .putString("last_forecast", body)
                    .putString("last_forecast_updated", updated)
                    .apply()
            }
            weather
        } catch (error: IOException) {
            Log.w("JoinMeWeather", "Weather request failed", error)
            throw error
        } finally {
            request.disconnect()
        }
    }
}
