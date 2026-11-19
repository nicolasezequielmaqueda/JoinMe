package com.joinme.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joinme.data.AuthRepository
import com.joinme.ui.components.FieldError
import com.joinme.ui.components.FormTextField
import com.joinme.ui.components.GroupedSection
import com.joinme.ui.components.LargeTitleHeader
import com.joinme.ui.components.PrimaryButton
import com.joinme.ui.components.RowDivider
import com.joinme.ui.components.SegmentedControl
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.icons.AppLogo
import com.joinme.ui.theme.JoinMeTheme
import androidx.compose.runtime.getValue

private val MODES = listOf("Sign in", "Create account")

/**
 * First screen when there is no session: sign in or create an account
 * with an email and a password.
 */
@Composable
fun LoginScreen(
    auth: AuthRepository,
    viewModel: LoginViewModel = viewModel { LoginViewModel(auth) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = JoinMeTheme.colors

    // The block is centred; when it does not fit (keyboard, large font) the screen scrolls.
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            // Stays above the keyboard and the system bars.
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            AppMark(modifier = Modifier.align(Alignment.CenterHorizontally))
            LargeTitleHeader(title = "Welcome to JoinMe", centered = true)

            SegmentedControl(
                options = MODES,
                selectedIndex = if (uiState.isCreatingAccount) 1 else 0,
                onSelect = { viewModel.onModeSelected(it == 1) },
            )

            GroupedSection {
                FormTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "Email",
                    placeholder = "you@example.com",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                RowDivider(start = 16.dp)
                FormTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = "Password",
                    placeholder = "At least 8 characters",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (uiState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = viewModel::onShowPasswordClick) {
                            Icon(
                                imageVector = if (uiState.showPassword) AppIcons.HidePassword else AppIcons.ShowPassword,
                                contentDescription = if (uiState.showPassword) "Hide password" else "Show password",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    },
                )
            }

            uiState.error?.let { error ->
                FieldError(
                    text = error,
                    // Screen readers announce the message when it changes.
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                )
            }

            PrimaryButton(
                text = if (uiState.isCreatingAccount) "Create account" else "Sign in",
                loadingText = if (uiState.isCreatingAccount) "Creating account…" else "Signing in…",
                onClick = viewModel::onSubmitClick,
                isLoading = uiState.isWorking,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = "This is a demo app. Do not use real personal data: " +
                    "choose a test email and a password you do not use anywhere else.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
        }
    }
}

/** Logo of the app above the title. */
@Composable
private fun AppMark(modifier: Modifier = Modifier) {
    Image(
        imageVector = AppLogo,
        contentDescription = null,
        modifier = modifier
            .size(64.dp)
            .clip(RoundedCornerShape(18.dp)),
    )
}
