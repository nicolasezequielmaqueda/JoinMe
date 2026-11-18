package com.joinme.data

import com.joinme.domain.ActivityResult
import com.joinme.model.Activity
import com.joinme.model.NewActivityForm
import kotlinx.coroutines.flow.StateFlow

/** The ViewModels use this interface to read and change activities. */
interface ActivityRepository {

    /** The ViewModels use this interface to read and change activities. */
    val activities: StateFlow<List<Activity>>

    fun getActivity(id: String): Activity?

    suspend fun createActivity(form: NewActivityForm, userId: String): ActivityResult

    suspend fun joinActivity(activityId: String, userId: String): ActivityResult

    suspend fun leaveActivity(activityId: String, userId: String): ActivityResult

    suspend fun cancelActivity(activityId: String, userId: String): ActivityResult
}
