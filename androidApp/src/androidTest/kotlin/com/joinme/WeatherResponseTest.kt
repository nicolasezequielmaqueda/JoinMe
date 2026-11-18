
package com.joinme

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherResponseTest {
    private val source = OpenMeteoWeather(InstrumentationRegistry.getInstrumentation().targetContext)

    @Test
    fun readsCurrentWeather() {
        val reading = source.parseWeatherResponse(
            """{"current":{"time":"2026-11-18T16:30","temperature_2m":8.5,"precipitation":0.0}}""",
        )
        assertEquals(8.5, reading!!.temperature, 0.01)
    }

    @Test
    fun emptyReadingReturnsNoData() {
        assertNull(source.parseWeatherResponse("""{"current":null}"""))
    }

    @Test(expected = JSONException::class)
    fun rejectsResponseWithMissingFields() {
        source.parseWeatherResponse("""{"current":{"time":"2026-11-18T16:30"}}""")
    }
}
