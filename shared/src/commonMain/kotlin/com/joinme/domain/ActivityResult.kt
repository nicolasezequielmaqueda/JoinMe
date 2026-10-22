package com.joinme.domain

import com.joinme.model.Activity

/**
 * Result of an action on an activity (join, leave, cancel, create).
 * Success contains the updated activity. Error contains a message for the user.
 */
sealed class ActivityResult {
    data class Success(val activity: Activity) : ActivityResult()
    data class Error(val message: String) : ActivityResult()
}
