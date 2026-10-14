package com.joinme.ui.components

import com.joinme.domain.ActivityRules
import com.joinme.model.Activity
import com.joinme.model.ActivityStatus
import kotlinx.datetime.LocalDateTime

/**
 * An activity plus the information the screens need to draw it.
 * The status is calculated once here instead of in every composable.
 */
data class ActivityItem(
    val activity: Activity,
    val status: ActivityStatus,
    val isJoined: Boolean,
    val isCreator: Boolean,
)

fun Activity.toItem(now: LocalDateTime, userId: String): ActivityItem =
    ActivityItem(
        activity = this,
        status = ActivityRules.statusOf(this, now),
        isJoined = ActivityRules.isParticipant(this, userId),
        isCreator = ActivityRules.isCreator(this, userId),
    )
