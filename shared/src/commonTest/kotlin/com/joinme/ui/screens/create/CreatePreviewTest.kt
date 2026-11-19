package com.joinme.ui.screens.create

import com.joinme.model.ActivityStatus
import com.joinme.model.ActivityType
import com.joinme.model.NewActivityForm
import com.joinme.model.ResidenceLocation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CreatePreviewTest {

    private val complete = NewActivityForm(
        type = ActivityType.PADEL,
        date = LocalDate(2026, 11, 20),
        startTime = LocalTime(18, 0),
        location = ResidenceLocation.SPORTS_COURT,
        maxParticipants = 4,
    )

    @Test
    fun noPreviewUntilTypeDateTimeAndPlaceAreChosen() {
        assertNull(previewItem(NewActivityForm(), "me"))
        assertNull(previewItem(complete.copy(type = null), "me"))
        assertNull(previewItem(complete.copy(date = null), "me"))
        assertNull(previewItem(complete.copy(startTime = null), "me"))
        assertNull(previewItem(complete.copy(location = null), "me"))
    }

    @Test
    fun previewTakesTheValuesOfTheFormWithTheCreatorAsOnlyParticipant() {
        val item = assertNotNull(previewItem(complete, "me"))

        assertEquals(ActivityStatus.OPEN, item.status)
        assertEquals(4, item.activity.maxParticipants)
        assertEquals(listOf("me"), item.activity.participantIds)
    }
}
