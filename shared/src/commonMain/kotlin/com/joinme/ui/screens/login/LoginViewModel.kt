package com.joinme.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.AuthRepository
import com.joinme.domain.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @property isCreatingAccount false = "Sign in", true = "Create account".
 * @property error problem to show under the fields, or null.
 * @property isWorking true while the request runs, to avoid double taps.
 */
data class LoginUiState(
    val isCreatingAccount: Boolean = false,
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val error: String? = null,
    val isWorking: Boolean = false,
)

/**
 * Logic of the Login screen: checks the fields and asks AuthRepository to sign in
 * or create the account. When it works, App shows the main screens by itself.
 */
class LoginViewModel(private val auth: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onModeSelected(isCreatingAccount: Boolean) =
        _uiState.update { it.copy(isCreatingAccount = isCreatingAccount, error = null) }

    fun onEmailChange(email: String) = _uiState.update { it.copy(email = email, error = null) }

    fun onPasswordChange(password: String) = _uiState.update { it.copy(password = password, error = null) }

    fun onShowPasswordClick() = _uiState.update { it.copy(showPassword = !it.showPassword) }

    fun onSubmitClick() {
        val state = _uiState.value
        if (state.isWorking) return

        // A space added by the keyboard at the end of the email is not an error.
        val email = state.email.trim()
        val validationError = AuthValidator.validate(email, state.password)
        if (validationError != null) {
            _uiState.update { it.copy(error = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true, error = null) }
            val error = if (state.isCreatingAccount) {
                auth.signUp(email, state.password)
            } else {
                auth.signIn(email, state.password)
            }
            // After a success the screen disappears, so the next login starts with empty fields.
            _uiState.update { if (error == null) LoginUiState() else it.copy(isWorking = false, error = error) }
        }
    }
}
