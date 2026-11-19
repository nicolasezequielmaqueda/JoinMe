package com.joinme.data

import com.joinme.domain.ActivityValidator
import com.joinme.domain.AuthValidator
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.io.IOException

/**
 * Turns the errors of Supabase into short messages for the user.
 * The functions only read the exception, so they are easy to test.
 */
object SupabaseErrors {

    const val OFFLINE = "You're offline. Check your connection and try again."
    const val GENERIC = "Something went wrong. Please try again."

    /** Message for an error of sign in, sign up or sign out. */
    fun messageForAuth(error: Throwable): String {
        if (isConnectionError(error)) return OFFLINE
        if (error is AuthWeakPasswordException) return messageForAuthCode(AuthErrorCode.WeakPassword)
        if (error is AuthRestException) return messageForAuthCode(error.errorCode)
        return GENERIC
    }

    /**
     * Message for an error of a database function (join, leave, cancel...).
     * [action] is the name of what the user tried, e.g. "Joining", to explain that it needs a connection.
     */
    fun messageForRpc(error: Throwable, action: String? = null): String {
        if (isConnectionError(error)) return if (action == null) OFFLINE else offlineFor(action)
        // For these errors "error" holds only the text written by the database function.
        if (error is PostgrestRestException) return messageForCode(error.error)
        return GENERIC
    }

    /** After any error that is not about the connection the server did answer, so the local copy may be out of date. */
    fun shouldRefreshAfter(error: Throwable): Boolean = !isConnectionError(error)

    fun offlineFor(action: String): String = "You're offline. $action needs a connection."

    fun messageForAuthCode(code: AuthErrorCode?): String = when (code) {
        AuthErrorCode.InvalidCredentials -> "Incorrect email or password."
        AuthErrorCode.EmailExists,
        AuthErrorCode.UserAlreadyExists -> "This email is already registered. Try signing in."
        AuthErrorCode.WeakPassword ->
            "The password is too weak. Use at least ${AuthValidator.MIN_PASSWORD_LENGTH} characters."
        AuthErrorCode.EmailAddressInvalid -> "Enter a valid email address."
        AuthErrorCode.OverRequestRateLimit,
        AuthErrorCode.OverEmailSendRateLimit -> "Too many attempts. Wait a moment and try again."
        else -> GENERIC
    }

    /** Messages for the codes raised by the database functions, as in ActivityRules and ActivityValidator. */
    fun messageForCode(code: String): String = when (code) {
        "NOT_AUTHENTICATED" -> "Sign in to continue."
        "NOT_FOUND" -> "This activity no longer exists."
        "ALREADY_JOINED" -> "You have already joined this activity."
        "CANCELLED" -> "This activity was cancelled."
        "COMPLETED" -> "This activity has already finished."
        "FULL" -> "This activity is full."
        "NOT_JOINED" -> "You are not part of this activity."
        "CREATOR_CANNOT_LEAVE" -> "You created this activity. Cancel it instead."
        "NOT_CREATOR" -> "Only the creator can cancel this activity."
        "ALREADY_CANCELLED" -> "This activity is already cancelled."
        "PAST_START", "INVALID_START" -> "The activity must start in the future."
        "INVALID_TYPE" -> "Choose an activity type."
        "NAME_REQUIRED" -> "Write a name for your activity."
        "NAME_TOO_LONG" -> "The name can have at most ${ActivityValidator.MAX_CUSTOM_NAME_LENGTH} characters."
        "INVALID_DURATION" -> "Choose a duration."
        "INVALID_LOCATION" -> "Choose a location."
        "INVALID_PARTICIPANTS" ->
            "Participants must be between ${ActivityValidator.MIN_PARTICIPANTS} and ${ActivityValidator.MAX_PARTICIPANTS}."
        "DESCRIPTION_TOO_LONG" ->
            "The description can have at most ${ActivityValidator.MAX_DESCRIPTION_LENGTH} characters."
        else -> GENERIC
    }

    private fun isConnectionError(error: Throwable): Boolean =
        error is HttpRequestException || error is HttpRequestTimeoutException || error is IOException
}
