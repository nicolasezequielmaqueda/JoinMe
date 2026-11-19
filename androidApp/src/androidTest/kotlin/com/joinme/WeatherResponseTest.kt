package com.joinme

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** The parsing is tested in the shared module. This checks what is saved on the device. */
@RunWith(AndroidJUnit4::class)
class WeatherResponseTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val source = OpenMeteoWeather(context)

    @Before
    fun saveForecastOf21November() {
        context.getSharedPreferences("joinme_weather", Context.MODE_PRIVATE).edit()
            .putString(
                "last_forecast",
                """{"hourly":{"time":["2026-11-21T17:00"],"temperature_2m":[2.7],""" +
                    """"precipitation_probability":[5],"precipitation":[0.0]}}""",
            )
            .putString("last_forecast_updated", "2026-11-19T14:32")
            .commit()
    }

    @Test
    fun savedForecastIsShownForTheSameDayAndHour() {
        val reading = source.saved(LocalDate(2026, 11, 21), LocalTime(17, 15))

        assertEquals(2.7, reading!!.temperature, 0.01)
        assertEquals("2026-11-19T14:32", reading.updated)
    }

    @Test
    fun savedForecastOfAnotherActivityIsNotShown() {
        assertNull(source.saved(LocalDate(2026, 11, 22), LocalTime(17, 0)))
        assertNull(source.saved(LocalDate(2026, 11, 21), LocalTime(18, 0)))
    }
}
