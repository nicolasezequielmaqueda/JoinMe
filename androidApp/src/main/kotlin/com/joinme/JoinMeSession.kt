package com.joinme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.joinme.data.AuthRepository
import com.joinme.data.SupabaseActivityRepository
import com.joinme.data.createSupabase
import com.joinme.data.local.JoinMeDatabase
import kotlinx.coroutines.Dispatchers

class JoinMeSession(application: Application) : AndroidViewModel(application) {
    private val database = Room.databaseBuilder<JoinMeDatabase>(
        application,
        application.getDatabasePath("joinme.db").absolutePath,
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

    private val supabase = createSupabase(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_PUBLISHABLE_KEY)

    private val reminders = WorkReminderScheduler(application)

    val repository = SupabaseActivityRepository(supabase, database.activityDao(), reminders, viewModelScope)
    val weather = OpenMeteoWeather(application)
    val auth = AuthRepository(supabase, viewModelScope)
}
