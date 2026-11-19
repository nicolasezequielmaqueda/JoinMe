package com.joinme.ui.components

import com.joinme.model.ActivityStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class PlacesTextTest {

    @Test
    fun severalFreePlaces() {
        assertEquals("3 places left", placesText(ActivityStatus.OPEN, taken = 5, capacity = 8))
    }

    @Test
    fun lastFreePlaceIsHighlighted() {
        assertEquals("Only 1 place left", placesText(ActivityStatus.OPEN, taken = 1, capacity = 2))
    }

    @Test
    fun noFreePlacesOnAnOpenActivityShowsTheNumbers() {
        assertEquals("Full · 4/4", placesText(ActivityStatus.OPEN, taken = 4, capacity = 4))
    }

    @Test
    fun capacityLimits() {
        assertEquals("Only 1 place left", placesText(ActivityStatus.OPEN, taken = 1, capacity = 2))
        assertEquals("2 places left", placesText(ActivityStatus.OPEN, taken = 1, capacity = 3))
        assertEquals("Full · 2/2", placesText(ActivityStatus.OPEN, taken = 2, capacity = 2))
    }

    @Test
    fun fullActivityShowsTheNumbers() {
        assertEquals("Full · 4/4", placesText(ActivityStatus.FULL, taken = 4, capacity = 4))
    }

    @Test
    fun cancelledAndFinishedIgnoreThePlaces() {
        assertEquals("Cancelled", placesText(ActivityStatus.CANCELLED, taken = 1, capacity = 4))
        assertEquals("Finished", placesText(ActivityStatus.COMPLETED, taken = 4, capacity = 4))
    }
}
