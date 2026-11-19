package com.joinme.ui.screens.create

import com.joinme.model.Activity
import com.joinme.model.ActivityStatus
import com.joinme.model.NewActivityForm
import com.joinme.ui.components.ActivityItem

/** The activity as it would look in the lists, or null while type, date, time or place are missing. */
fun previewItem(form: NewActivityForm, userId: String): ActivityItem? {
    val type = form.type ?: return null
    val date = form.date ?: return null
    val startTime = form.startTime ?: return null
    val location = form.location ?: return null

    val activity = Activity(
        id = "preview",
        type = type,
        customName = form.customName,
        date = date,
        startTime = startTime,
        durationMinutes = form.durationMinutes,
        location = location,
        maxParticipants = form.maxParticipants,
        creatorId = userId,
        participantIds = listOf(userId),
        description = form.description,
    )
    return ActivityItem(activity, ActivityStatus.OPEN, isJoined = true, isCreator = true)
}
