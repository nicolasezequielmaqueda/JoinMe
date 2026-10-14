package com.joinme.ui.navigation

/**
 * Names of the screens used by the NavHost.
 */
object Routes {
    const val ACTIVITIES = "activities"
    const val MY_ACTIVITIES = "my_activities"
    const val CREATE = "create"

    const val ARG_ACTIVITY_ID = "activityId"
    const val DETAIL = "detail/{$ARG_ACTIVITY_ID}"

    fun detail(activityId: String) = "detail/$activityId"
}
