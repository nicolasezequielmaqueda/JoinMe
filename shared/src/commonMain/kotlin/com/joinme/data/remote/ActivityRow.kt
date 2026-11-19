package com.joinme.data.remote

import com.joinme.model.Activity
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One row of the "participants" table, as embedded in an activity. */
@Serializable
data class ParticipantRow(
    @SerialName("user_id") val userId: String,
)

/** One row of the "activities" table with its participants, as sent by Supabase. */
@Serializable
data class ActivityRow(
    val id: String,
    val type: String,
    @SerialName("custom_name") val customName: String = "",
    val date: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    val location: String,
    @SerialName("max_participants") val maxParticipants: Int,
    val description: String = "",
    @SerialName("creator_id") val creatorId: String,
    @SerialName("is_cancelled") val isCancelled: Boolean = false,
    val participants: List<ParticipantRow> = emptyList(),
)

/** Columns to ask for in the select, so the list does not depend on extra columns of the table. */
const val ACTIVITY_COLUMNS =
    "id,type,custom_name,date,start_time,duration_minutes,location,max_participants,description," +
        "creator_id,is_cancelled,participants(user_id)"

/** Returns null if the type or the location is unknown, so one bad row does not break the whole list. */
fun ActivityRow.toActivityOrNull(): Activity? {
    val activityType = ActivityType.entries.find { it.name == type } ?: return null
    val residenceLocation = ResidenceLocation.entries.find { it.name == location } ?: return null

    // The rest of the app expects the creator to be the first participant.
    val userIds = participants.map { it.userId }
    val orderedIds = if (creatorId in userIds) listOf(creatorId) + (userIds - creatorId) else userIds

    return Activity(
        id = id,
        type = activityType,
        customName = customName,
        date = LocalDate.parse(date),
        startTime = LocalTime.parse(startTime),
        durationMinutes = durationMinutes,
        location = residenceLocation,
        maxParticipants = maxParticipants,
        creatorId = creatorId,
        participantIds = orderedIds,
        description = description,
        isCancelled = isCancelled,
    )
}
