package com.joinme.ui.components

import com.joinme.model.ActivityStatus

/** Short sentence about the free places, e.g. "3 places left" or "Full · 4/4". */
fun placesText(status: ActivityStatus, taken: Int, capacity: Int): String {
    val free = capacity - taken
    return when {
        status == ActivityStatus.CANCELLED -> "Cancelled"
        status == ActivityStatus.COMPLETED -> "Finished"
        free <= 0 -> "Full · $taken/$capacity"
        free == 1 -> "Only 1 place left"
        else -> "$free places left"
    }
}
