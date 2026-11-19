package com.joinme.data

import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.HttpRequestBuilder
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SupabaseErrorsTest {

    @Test
    fun databaseCodesHaveTheirOwnMessage() {
        assertEquals("This activity is full.", SupabaseErrors.messageForCode("FULL"))
        assertEquals("You have already joined this activity.", SupabaseErrors.messageForCode("ALREADY_JOINED"))
        assertEquals("You created this activity. Cancel it instead.", SupabaseErrors.messageForCode("CREATOR_CANNOT_LEAVE"))
        assertEquals("Only the creator can cancel this activity.", SupabaseErrors.messageForCode("NOT_CREATOR"))
        assertEquals("The activity must start in the future.", SupabaseErrors.messageForCode("PAST_START"))
    }

    @Test
    fun everyKnownDatabaseCodeHasAMessage() {
        val codes = listOf(
            "NOT_AUTHENTICATED", "NOT_FOUND", "ALREADY_JOINED", "CANCELLED", "COMPLETED", "FULL",
            "NOT_JOINED", "CREATOR_CANNOT_LEAVE", "NOT_CREATOR", "ALREADY_CANCELLED", "PAST_START",
            "INVALID_START", "INVALID_TYPE", "NAME_REQUIRED", "NAME_TOO_LONG", "INVALID_DURATION",
            "INVALID_LOCATION", "INVALID_PARTICIPANTS", "DESCRIPTION_TOO_LONG",
        )

        codes.forEach { assertNotEquals(SupabaseErrors.GENERIC, SupabaseErrors.messageForCode(it), it) }
    }

    @Test
    fun unknownDatabaseCodeGivesGenericMessage() {
        assertEquals(SupabaseErrors.GENERIC, SupabaseErrors.messageForCode("SOMETHING_ELSE"))
    }

    @Test
    fun authCodesHaveTheirOwnMessage() {
        assertEquals("Incorrect email or password.", SupabaseErrors.messageForAuthCode(AuthErrorCode.InvalidCredentials))
        assertEquals(
            "This email is already registered. Try signing in.",
            SupabaseErrors.messageForAuthCode(AuthErrorCode.UserAlreadyExists),
        )
        assertEquals(
            "This email is already registered. Try signing in.",
            SupabaseErrors.messageForAuthCode(AuthErrorCode.EmailExists),
        )
        assertEquals(
            "The password is too weak. Use at least 8 characters.",
            SupabaseErrors.messageForAuthCode(AuthErrorCode.WeakPassword),
        )
        assertEquals(SupabaseErrors.GENERIC, SupabaseErrors.messageForAuthCode(AuthErrorCode.UnexpectedFailure))
    }

    @Test
    fun connectionErrorsSayTheUserIsOffline() {
        assertEquals(SupabaseErrors.OFFLINE, SupabaseErrors.messageForAuth(IOException("no route to host")))
        assertEquals(SupabaseErrors.OFFLINE, SupabaseErrors.messageForRpc(IOException("no route to host")))
        assertEquals(
            SupabaseErrors.OFFLINE,
            SupabaseErrors.messageForAuth(HttpRequestTimeoutException(HttpRequestBuilder())),
        )
    }

    @Test
    fun offlineActionMessageSaysTheActionNeedsAConnection() {
        val offline = IOException("no route to host")

        assertEquals(
            "You're offline. Joining needs a connection.",
            SupabaseErrors.messageForRpc(offline, action = "Joining"),
        )
        assertEquals(SupabaseErrors.OFFLINE, SupabaseErrors.messageForRpc(offline))
    }

    @Test
    fun actionNameDoesNotChangeOtherErrors() {
        assertEquals(SupabaseErrors.GENERIC, SupabaseErrors.messageForRpc(IllegalStateException("boom"), "Joining"))
    }

    @Test
    fun listIsNotDownloadedAgainAfterAConnectionError() {
        assertFalse(SupabaseErrors.shouldRefreshAfter(IOException("no route to host")))
        assertFalse(SupabaseErrors.shouldRefreshAfter(HttpRequestTimeoutException(HttpRequestBuilder())))
    }

    @Test
    fun listIsDownloadedAgainAfterAnErrorFromTheServer() {
        // A rejection from the database (FULL, ALREADY_JOINED...) is a PostgrestRestException, which is not a connection error.
        assertTrue(SupabaseErrors.shouldRefreshAfter(IllegalStateException("rejected")))
    }

    @Test
    fun otherErrorsGiveGenericMessage() {
        assertEquals(SupabaseErrors.GENERIC, SupabaseErrors.messageForAuth(IllegalStateException("boom")))
        assertEquals(SupabaseErrors.GENERIC, SupabaseErrors.messageForRpc(IllegalStateException("boom")))
    }
}
