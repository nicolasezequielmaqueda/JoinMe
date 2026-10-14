package com.joinme.ui.screens.activities

import com.joinme.model.Activity
import com.joinme.model.ActivityCategory
import kotlinx.datetime.LocalDate

/** Filter chips shown at the top of the Activities screen. */
enum class ActivityFilter(val label: String) {
    ALL("All"),
    TODAY("Today"),
    SPORTS("Sports"),
    GAMES("Games"),
    SOCIAL("Social");

    fun matches(activity: Activity, today: LocalDate): Boolean = when (this) {
        ALL -> true
        TODAY -> activity.date == today
        SPORTS -> activity.type.category == ActivityCategory.SPORTS
        GAMES -> activity.type.category == ActivityCategory.GAMES
        SOCIAL -> activity.type.category == ActivityCategory.SOCIAL
    }
}
