package com.joinme

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.joinme.data.ReminderScheduler
import com.joinme.domain.delayMinutes
import com.joinme.model.Activity
import com.joinme.util.DateTimeUtils
import java.util.concurrent.TimeUnit

/**
 * WorkManager keeps the reminder after the app is closed or the phone restarts,
 * and it needs no exact-alarm permission.
 */
class WorkReminderScheduler(context: Context) : ReminderScheduler {
    private val workManager = WorkManager.getInstance(context.applicationContext)

    override fun schedule(activity: Activity) {
        val delay = delayMinutes(activity, DateTimeUtils.now())
        if (delay == null) {
            cancel(activity.id)
            return
        }

        // Only what the notification shows. No participants or user ids.
        val input = Data.Builder()
            .putString(ReminderWorker.KEY_ACTIVITY_ID, activity.id)
            .putString(ReminderWorker.KEY_TITLE, activity.title)
            .putString(ReminderWorker.KEY_TIME, DateTimeUtils.formatTime(activity.startTime))
            .putString(ReminderWorker.KEY_PLACE, activity.location.displayName)
            .build()
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .setInputData(input)
            .addTag(TAG)
            .build()

        workManager.enqueueUniqueWork(workName(activity.id), ExistingWorkPolicy.REPLACE, request)
    }

    override fun cancel(activityId: String) {
        workManager.cancelUniqueWork(workName(activityId))
    }

    override fun cancelAll() {
        workManager.cancelAllWorkByTag(TAG)
    }

    private fun workName(activityId: String) = "reminder-$activityId"

    private companion object {
        const val TAG = "reminder"
    }
}
