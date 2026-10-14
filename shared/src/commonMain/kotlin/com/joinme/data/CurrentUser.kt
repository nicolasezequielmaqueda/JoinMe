package com.joinme.data

/**
 * Technical id of the person using the app.
 *
 * JoinMe has no profiles. In M2 this id will be generated randomly the first
 * time the app starts and saved on the device. For the prototype it is fixed.
 */
object CurrentUser {
    const val ID = "user-me"
}
