package com.joinme.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joinme.model.Activity
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Row of the "activities" table. Dates, times and enums are saved as text.
 *
 * [participantIds] is a comma-separated list. User ids are technical ids, so they never contain commas.
 */
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val type: String,
    val customName: String,
    val date: String,
    val startTime: String,
    val durationMinutes: Int,
    val location: String,
    val maxParticipants: Int,
    val creatorId: String,
    val participantIds: String,
    val description: String,
    val isCancelled: Boolean,
)

fun ActivityEntity.toActivity(): Activity = Activity(
    id = id,
    type = ActivityType.valueOf(type),
    customName = customName,
    date = LocalDate.parse(date),
    startTime = LocalTime.parse(startTime),
    durationMinutes = durationMinutes,
    location = ResidenceLocation.valueOf(location),
    maxParticipants = maxParticipants,
    creatorId = creatorId,
    participantIds = if (participantIds.isEmpty()) emptyList() else participantIds.split(","),
    description = description,
    isCancelled = isCancelled,
)

fun Activity.toEntity(): ActivityEntity = ActivityEntity(
    id = id,
    type = type.name,
    customName = customName,
    date = date.toString(),
    startTime = startTime.toString(),
    durationMinutes = durationMinutes,
    location = location.name,
    maxParticipants = maxParticipants,
    creatorId = creatorId,
    participantIds = participantIds.joinToString(","),
    description = description,
    isCancelled = isCancelled,
)
