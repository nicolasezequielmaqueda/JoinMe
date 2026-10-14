package com.joinme.data

/**
 * Creates the objects shared by the whole app (simple manual dependency injection).
 * All screens use the same repository, so a change in one screen is seen in the others.
 */
object AppContainer {
    val activityRepository: ActivityRepository by lazy { FakeActivityRepository() }
}
