
package com.joinme

import android.content.Context
import com.joinme.model.Activity
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.json.JSONArray
import org.json.JSONObject

class ActivitySettings(context: Context) {
    private val settings = context.getSharedPreferences("joinme_activities", Context.MODE_PRIVATE)

    fun read(): List<Activity>? {
        val saved = settings.getString("activities", null) ?: return null
        val rows = JSONArray(saved)
        return List(rows.length()) { index ->
            val row = rows.getJSONObject(index)
            val people = row.getJSONArray("participantIds")
            Activity(
                id = row.getString("id"),
                type = ActivityType.valueOf(row.getString("type")),
                customName = row.getString("customName"),
                date = LocalDate.parse(row.getString("date")),
                startTime = LocalTime.parse(row.getString("startTime")),
                durationMinutes = row.getInt("durationMinutes"),
                location = ResidenceLocation.valueOf(row.getString("location")),
                maxParticipants = row.getInt("maxParticipants"),
                creatorId = row.getString("creatorId"),
                participantIds = List(people.length()) { people.getString(it) },
                description = row.getString("description"),
                isCancelled = row.getBoolean("isCancelled"),
            )
        }
    }

    suspend fun save(activities: List<Activity>): Boolean = withContext(Dispatchers.IO) {
        val rows = JSONArray()
        for (activity in activities) {
            rows.put(JSONObject().apply {
                put("id", activity.id)
                put("type", activity.type.name)
                put("customName", activity.customName)
                put("date", activity.date.toString())
                put("startTime", activity.startTime.toString())
                put("durationMinutes", activity.durationMinutes)
                put("location", activity.location.name)
                put("maxParticipants", activity.maxParticipants)
                put("creatorId", activity.creatorId)
                put("participantIds", JSONArray(activity.participantIds))
                put("description", activity.description)
                put("isCancelled", activity.isCancelled)
            })
        }
        settings.edit().putString("activities", rows.toString()).commit()
    }
}
