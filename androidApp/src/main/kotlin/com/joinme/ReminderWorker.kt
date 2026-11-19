package com.joinme

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        if (!canNotify()) return Result.success()

        val activityId = inputData.getString(KEY_ACTIVITY_ID) ?: return Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val time = inputData.getString(KEY_TIME) ?: return Result.failure()
        val place = inputData.getString(KEY_PLACE) ?: return Result.failure()

        createChannel()

        val openApp = Intent(applicationContext, MainActivity::class.java)
        val tapIntent = PendingIntent.getActivity(
            applicationContext,
            activityId.hashCode(),
            openApp,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Activity reminder")
            .setContentText("$title starts at $time · $place")
            .setContentIntent(tapIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // The activity name is free text, so it is hidden on the lock screen.
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()

        // The permission was checked in canNotify().
        NotificationManagerCompat.from(applicationContext)
            .notify(activityId.hashCode(), notification)
        return Result.success()
    }

    private fun canNotify(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return false
        }
        return NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(CHANNEL_ID, "Activity reminders", NotificationManager.IMPORTANCE_DEFAULT)
        applicationContext.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val KEY_ACTIVITY_ID = "activity_id"
        const val KEY_TITLE = "title"
        const val KEY_TIME = "time"
        const val KEY_PLACE = "place"

        private const val CHANNEL_ID = "activity_reminders"
    }
}
