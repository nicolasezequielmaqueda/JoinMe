package com.joinme.domain

import com.joinme.model.ActivityType
import com.joinme.model.NewActivityForm
import com.joinme.model.ResidenceLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ActivityValidatorTest {

    private val now = LocalDateTime(2026, 10, 14, 12, 0)

    private val validForm = NewActivityForm(
        type = ActivityType.FOOTBALL,
        date = LocalDate(2026, 10, 15),
        startTime = LocalTime(17, 0),
        durationMinutes = 90,
        location = ResidenceLocation.SPORTS_COURT,
        maxParticipants = 8,
    )

    @Test
    fun completeFormIsValid() {
        assertNull(ActivityValidator.validate(validForm, now))
    }

    @Test
    fun missingTypeIsNotValid() {
        assertEquals("Choose an activity type.", ActivityValidator.validate(validForm.copy(type = null), now))
    }

    @Test
    fun activityInThePastIsNotValid() {
        val past = validForm.copy(date = LocalDate(2026, 10, 14), startTime = LocalTime(9, 0))

        assertNotNull(ActivityValidator.validate(past, now))
    }

    @Test
    fun otherTypeNeedsAName() {
        val other = validForm.copy(type = ActivityType.OTHER, customName = "  ")

        assertNotNull(ActivityValidator.validate(other, now))
    }

    @Test
    fun tooManyParticipantsIsNotValid() {
        val form = validForm.copy(maxParticipants = ActivityValidator.MAX_PARTICIPANTS + 1)

        assertNotNull(ActivityValidator.validate(form, now))
    }
}
