
package com.joinme

import android.content.Context
import android.util.Log
import com.joinme.data.Weather
import com.joinme.data.WeatherSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI

class OpenMeteoWeather(context: Context) : WeatherSource {
    private val settings = context.getSharedPreferences("joinme_weather", Context.MODE_PRIVATE)

    override fun saved(): Weather? {
        val json = settings.getString("last_weather", null) ?: return null
        return try {
            parseWeatherResponse(json)
        } catch (_: JSONException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    override suspend fun fetch(): Weather? = withContext(Dispatchers.IO) {
        // The demo uses a fixed city. No device location is requested.
        val address = "https://api.open-meteo.com/v1/forecast?latitude=51.76&longitude=19.46" +
            "&current=temperature_2m,precipitation&timezone=Europe%2FWarsaw"
        val request = URI(address).toURL().openConnection() as HttpURLConnection
        try {
            request.connectTimeout = 5000
            request.readTimeout = 5000
            if (request.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("Weather request failed")
            }
            val body = request.inputStream.bufferedReader().use { it.readText() }
            val weather = parseWeatherResponse(body)
            if (weather != null) {
                settings.edit().putString("last_weather", body).apply()
            }
            weather
        } catch (error: IOException) {
            Log.w("JoinMeWeather", "Weather request failed", error)
            throw error
        } finally {
            request.disconnect()
        }
    }

    internal fun parseWeatherResponse(body: String): Weather? {
        val response = JSONObject(body)
        if (response.has("current") && response.isNull("current")) return null
        val current = response.getJSONObject("current")
        if (current.length() == 0) return null
        val time = LocalDateTime.parse(current.getString("time"))
        val temperature = current.getDouble("temperature_2m")
        val precipitation = current.getDouble("precipitation")
        require(temperature.isFinite() && precipitation.isFinite() && precipitation >= 0)
        return Weather(time.toString(), temperature, precipitation)
    }
}
