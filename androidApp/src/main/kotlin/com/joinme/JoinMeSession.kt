
package com.joinme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.joinme.data.LocalActivityRepository
import com.joinme.data.SampleActivities
import com.joinme.util.DateTimeUtils
import org.json.JSONException

class JoinMeSession(application: Application) : AndroidViewModel(application) {
    private val settings = ActivitySettings(application)
    private var readFailed = false
    private val saved = readActivities()

    val repository = LocalActivityRepository(
        initialActivities = saved ?: SampleActivities.create(DateTimeUtils.today()),
        save = { !readFailed && settings.save(it) },
    )
    val weather = OpenMeteoWeather(application)
    val storageMessage = if (readFailed) "Saved activities could not be read. Showing demo data." else null

    private fun readActivities() = try {
        settings.read()
    } catch (_: JSONException) {
        readFailed = true
        null
    } catch (_: IllegalArgumentException) {
        readFailed = true
        null
    }
}
