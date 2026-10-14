package com.joinme.domain

import com.joinme.model.ActivityType
import com.joinme.model.NewActivityForm
import kotlinx.datetime.LocalDateTime

/**
 * Checks the Create Activity form before an activity is created.
 */
object ActivityValidator {

    const val MIN_PARTICIPANTS = 2
    const val MAX_PARTICIPANTS = 30
    const val MAX_CUSTOM_NAME_LENGTH = 30
    const val MAX_DESCRIPTION_LENGTH = 150

    /**
     * Returns the first problem found as a message for the user,
     * or null if the form is valid.
     */
    fun validate(form: NewActivityForm, now: LocalDateTime): String? {
        if (form.type == null) return "Choose an activity type."
        if (form.type == ActivityType.OTHER && form.customName.isBlank()) {
            return "Write a name for your activity."
        }
        if (form.customName.length > MAX_CUSTOM_NAME_LENGTH) {
            return "The name can have at most $MAX_CUSTOM_NAME_LENGTH characters."
        }
        if (form.date == null) return "Choose a date."
        if (form.startTime == null) return "Choose a start time."
        if (LocalDateTime(form.date, form.startTime) <= now) {
            return "The activity must start in the future."
        }
        if (form.durationMinutes <= 0) return "Choose a duration."
        if (form.location == null) return "Choose a location."
        if (form.maxParticipants !in MIN_PARTICIPANTS..MAX_PARTICIPANTS) {
            return "Participants must be between $MIN_PARTICIPANTS and $MAX_PARTICIPANTS."
        }
        if (form.description.length > MAX_DESCRIPTION_LENGTH) {
            return "The description can have at most $MAX_DESCRIPTION_LENGTH characters."
        }
        return null
    }
}
