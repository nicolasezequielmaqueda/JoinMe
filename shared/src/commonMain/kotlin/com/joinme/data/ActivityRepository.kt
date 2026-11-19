package com.joinme.data

import com.joinme.domain.ActivityResult
import com.joinme.model.Activity
import com.joinme.model.NewActivityForm
import kotlinx.coroutines.flow.StateFlow

/** The ViewModels use this interface to read and change activities. */
interface ActivityRepository {

    /** The current list of activities. It emits a new list after every change. */
    val activities: StateFlow<List<Activity>>

    /** When the list was last downloaded, as text for the user. Null if it never was. */
    val lastUpdated: StateFlow<String?>

    /** Message of the last refresh if it failed, or null if it worked. */
    val refreshError: StateFlow<String?>

    fun getActivity(id: String): Activity?

    /** Downloads the list again. Returns null if it worked, or a message for the user. */
    suspend fun refresh(): String?

    /** Removes the activities saved on this device, e.g. before signing out. */
    suspend fun clearLocalData()

    suspend fun createActivity(form: NewActivityForm, userId: String): ActivityResult

    suspend fun joinActivity(activityId: String, userId: String): ActivityResult

    suspend fun leaveActivity(activityId: String, userId: String): ActivityResult

    suspend fun cancelActivity(activityId: String, userId: String): ActivityResult
}
