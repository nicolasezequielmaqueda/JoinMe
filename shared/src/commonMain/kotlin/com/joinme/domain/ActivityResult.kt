package com.joinme.domain

import com.joinme.model.Activity

/**
 * Result of an action on an activity (join, leave, cancel, create).
 * Using a result instead of exceptions forces the caller to handle the error case.
 */
sealed class ActivityResult {
    data class Success(val activity: Activity) : ActivityResult()
    data class Error(val message: String) : ActivityResult()
}
