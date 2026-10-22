package com.joinme.data

import com.joinme.domain.ActivityResult
import com.joinme.model.Activity
import com.joinme.model.NewActivityForm
import kotlinx.coroutines.flow.StateFlow

/**
 * The ViewModels use this interface to read and change activities.
 *
 * M1: FakeActivityRepository keeps everything in memory.
 * Plan for M2: use Room to save data on the phone and Supabase to share it.
 * Keeping this interface should reduce the changes needed in the ViewModels.
 *
 * The functions are "suspend" so they can later wait for database or network work.
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
