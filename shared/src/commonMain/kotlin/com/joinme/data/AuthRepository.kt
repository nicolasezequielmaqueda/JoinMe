package com.joinme.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sign in, sign up and sign out with Supabase Auth.
 * The saved session is loaded when the app starts, so the user stays signed in.
 * The functions return null if everything went well, or a message for the user.
 */
class AuthRepository(private val supabase: SupabaseClient, scope: CoroutineScope) {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _email = MutableStateFlow<String?>(null)
    val email: StateFlow<String?> = _email.asStateFlow()

    // True until we know whether there is a saved session.
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        scope.launch {
            supabase.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Initializing -> Unit
                    is SessionStatus.Authenticated -> {
                        _userId.value = status.session.user?.id
                        _email.value = status.session.user?.email
                        _isLoading.value = false
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _userId.value = null
                        _email.value = null
                        _isLoading.value = false
                    }
                    // The token could not be renewed (e.g. no connection): use the saved session so the
                    // user still sees the saved activities. currentSessionOrNull() is null in this state.
                    is SessionStatus.RefreshFailure -> {
                        val saved = supabase.auth.sessionManager.loadSessionOrNull()
                        _userId.value = saved?.user?.id ?: _userId.value
                        _email.value = saved?.user?.email ?: _email.value
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    suspend fun signUp(email: String, password: String): String? = tryAuth {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signIn(email: String, password: String): String? = tryAuth {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    /** Always closes the session on this device, even if the server cannot be reached. */
    suspend fun signOut() {
        try {
            supabase.auth.signOut()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Without network the logout request fails and the session would stay open.
            supabase.auth.clearSession()
        }
    }

    /** Deletes the account and its data in the database, then closes the session. */
    suspend fun deleteAccount(): String? {
        try {
            supabase.postgrest.rpc("delete_my_account")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return SupabaseErrors.messageForRpc(e)
        }

        signOut()
        return null
    }

    private suspend fun tryAuth(action: suspend () -> Unit): String? {
        try {
            action()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return SupabaseErrors.messageForAuth(e)
        }
        return null
    }
}
