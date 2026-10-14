package com.joinme.data

import com.joinme.domain.ActivityResult
import com.joinme.model.Activity
import com.joinme.model.NewActivityForm
import kotlinx.coroutines.flow.StateFlow

/**
 * Single entry point to the activity data for the ViewModels.
 *
 * M1: FakeActivityRepository keeps everything in memory.
 * M2: a new implementation will use Room (local copy) and Supabase (remote),
 * and the ViewModels will not need to change.
 *
 * The functions are "suspend" because in M2 they will make network calls.
 */
interface ActivityRepository {

    /** All activities. The UI observes it and redraws when it changes. */
    val activities: StateFlow<List<Activity>>

    fun getActivity(id: String): Activity?

    suspend fun createActivity(form: NewActivityForm, userId: String): ActivityResult

    suspend fun joinActivity(activityId: String, userId: String): ActivityResult

    suspend fun leaveActivity(activityId: String, userId: String): ActivityResult

    suspend fun cancelActivity(activityId: String, userId: String): ActivityResult
}
