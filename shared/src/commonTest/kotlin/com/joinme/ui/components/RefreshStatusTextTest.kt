package com.joinme.ui.components

import com.joinme.data.SupabaseErrors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RefreshStatusTextTest {

    @Test
    fun noLineWhenTheLastRefreshWorked() {
        assertNull(refreshStatusText(refreshError = null, lastUpdated = "18 Nov 14:32"))
    }

    @Test
    fun offlineShowsTheLastUpdate() {
        assertEquals(
            "Offline · Last updated 18 Nov 14:32",
            refreshStatusText(SupabaseErrors.OFFLINE, "18 Nov 14:32"),
        )
    }

    @Test
    fun otherErrorsShowTheSavedActivities() {
        assertEquals(
            "Could not refresh. Showing saved activities from 18 Nov 14:32",
            refreshStatusText(SupabaseErrors.GENERIC, "18 Nov 14:32"),
        )
    }

    @Test
    fun neverUpdatedHasNoDate() {
        assertEquals("Offline", refreshStatusText(SupabaseErrors.OFFLINE, null))
        assertEquals("Could not refresh.", refreshStatusText(SupabaseErrors.GENERIC, null))
    }
}
