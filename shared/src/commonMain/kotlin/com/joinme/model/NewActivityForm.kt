package com.joinme.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Values typed by the user in the Create Activity screen.
 * Nullable fields mean "not chosen yet".
 */
data class NewActivityForm(
    val type: ActivityType? = null,
    val customName: String = "",
    val date: LocalDate? = null,
    val startTime: LocalTime? = null,
    val durationMinutes: Int = 60,
    val location: ResidenceLocation? = null,
    val maxParticipants: Int = 2,
    val description: String = "",
)
