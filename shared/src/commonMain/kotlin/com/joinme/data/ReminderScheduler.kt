package com.joinme.data

import com.joinme.model.Activity

/** Schedules a local reminder 30 minutes before an activity. */
interface ReminderScheduler {
    fun schedule(activity: Activity)
    fun cancel(activityId: String)
    fun cancelAll()
}
