package com.joinme.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * An activity that students can join.
 *
 * The status (OPEN, FULL...) is not stored here. It is calculated from the
 * other fields in ActivityRules, so it can never be out of date.
 *
 * @property customName only used when [type] is [ActivityType.OTHER].
 * @property participantIds technical user ids. The creator is always the first one.
 */
data class Activity(
    val id: String,
    val type: ActivityType,
    val customName: String = "",
    val date: LocalDate,
    val startTime: LocalTime,
    val durationMinutes: Int,
    val location: ResidenceLocation,
    val maxParticipants: Int,
    val creatorId: String,
    val participantIds: List<String>,
    val description: String = "",
    val isCancelled: Boolean = false,
) {
    /** Name shown on screen: the custom name for "Other", otherwise the type name. */
    val title: String
        get() = if (type == ActivityType.OTHER && customName.isNotBlank()) customName else type.displayName

    val participantCount: Int
        get() = participantIds.size
}
