package com.joinme.data

import com.joinme.model.Activity

/** Remembers the reminders asked by the repository, so tests can check them. */
class FakeReminderScheduler : ReminderScheduler {

    val scheduled = mutableListOf<String>()
    val cancelled = mutableListOf<String>()
    var cancelAllCount = 0

    /** The reminders that would still fire, like the pending jobs of the real scheduler. */
    val active = mutableSetOf<String>()

    override fun schedule(activity: Activity) {
        scheduled += activity.id
        active += activity.id
    }

    override fun cancel(activityId: String) {
        cancelled += activityId
        active -= activityId
    }

    override fun cancelAll() {
        cancelAllCount++
        active.clear()
    }
}
