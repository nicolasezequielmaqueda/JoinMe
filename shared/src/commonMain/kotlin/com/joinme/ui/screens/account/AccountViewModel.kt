package com.joinme.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.ActivityRepository
import com.joinme.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * @property email email of the signed in user.
 * @property error problem of the last action, or null.
 * @property isWorking true while an action runs, to avoid double taps.
 */
data class AccountUiState(
    val email: String = "",
    val error: String? = null,
    val isWorking: Boolean = false,
)

/**
 * Logic of the Account screen: sign out and delete the account.
 * The session is closed first and the activities saved on the device are removed afterwards,
 * so the next user does not see them. When the session ends, App shows the Login screen by itself.
 */
class AccountViewModel(
    private val auth: AuthRepository,
    private val repository: ActivityRepository,
) : ViewModel() {

    private val error = MutableStateFlow<String?>(null)
    private val isWorking = MutableStateFlow(false)

    val uiState: StateFlow<AccountUiState> =
        combine(auth.email, error, isWorking) { email, error, working ->
            AccountUiState(email = email ?: "", error = error, isWorking = working)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AccountUiState(),
        )

    fun onSignOutClick() = runAction {
        auth.signOut()
        repository.clearLocalData()
        null
    }

    // If the account could not be deleted nothing is removed, so the user can try again.
    fun onDeleteConfirmed() = runAction {
        val error = auth.deleteAccount()
        if (error == null) repository.clearLocalData()
        error
    }

    private fun runAction(action: suspend () -> String?) {
        if (isWorking.value) return

        viewModelScope.launch {
            isWorking.value = true
            error.value = null
            error.value = action()
            isWorking.value = false
        }
    }
}
