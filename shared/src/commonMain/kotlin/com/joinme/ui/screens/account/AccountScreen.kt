package com.joinme.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joinme.data.ActivityRepository
import com.joinme.data.AuthRepository
import com.joinme.ui.components.ActionRow
import com.joinme.ui.components.ConfirmDialog
import com.joinme.ui.components.FieldError
import com.joinme.ui.components.GroupedSection
import com.joinme.ui.components.NavBar
import com.joinme.ui.components.NavBarLayout
import com.joinme.ui.components.SectionFooter
import com.joinme.ui.components.SectionHeader
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

/**
 * Shows the email of the signed in user and lets them sign out or delete the account.
 */
@Composable
fun AccountScreen(
    onBack: () -> Unit,
    auth: AuthRepository,
    repository: ActivityRepository,
    viewModel: AccountViewModel = viewModel { AccountViewModel(auth, repository) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = JoinMeTheme.colors
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scrolled by remember { derivedStateOf { scrollState.value > 0 } }

    NavBarLayout(
        navBar = { NavBar(title = "Account", scrolled = scrolled, onBack = onBack) },
        modifier = Modifier.background(colors.background),
    ) { barHeight ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(
                    start = JoinMeDimens.screenGutter,
                    end = JoinMeDimens.screenGutter,
                    top = barHeight + JoinMeDimens.spaceL,
                    bottom = JoinMeDimens.spaceXXL,
                ),
        ) {
            Avatar(email = uiState.email, modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                text = uiState.email,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = JoinMeDimens.spaceM),
            )

            uiState.error?.let { error ->
                FieldError(
                    text = error,
                    // Screen readers announce the message when it changes.
                    modifier = Modifier
                        .padding(start = 4.dp, top = JoinMeDimens.spaceL)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                )
            }

            SectionHeader("Account")
            GroupedSection {
                ActionRow(
                    text = "Sign out",
                    icon = AppIcons.SignOut,
                    color = colors.accent,
                    onClick = viewModel::onSignOutClick,
                    enabled = !uiState.isWorking,
                    showDivider = false,
                )
            }

            SectionHeader("Danger zone")
            GroupedSection {
                ActionRow(
                    text = "Delete account",
                    icon = AppIcons.Delete,
                    color = colors.destructive,
                    onClick = { showDeleteDialog = true },
                    enabled = !uiState.isWorking,
                    showDivider = false,
                )
            }
            SectionFooter("Deleting your account also deletes the activities you created and your participations.")
        }
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Delete your account?",
            text = "This deletes your account, the activities you created and your participations " +
                "in other activities. This cannot be undone.",
            confirmText = "Delete account",
            dismissText = "Keep it",
            onConfirm = {
                showDeleteDialog = false
                viewModel.onDeleteConfirmed()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

/** Round picture of the account with the initials of the email. */
@Composable
private fun Avatar(email: String, modifier: Modifier = Modifier) {
    val colors = JoinMeTheme.colors
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(64.dp).background(colors.accentSoft, CircleShape),
    ) {
        Text(
            text = initialsOf(email),
            style = MaterialTheme.typography.titleLarge,
            color = colors.accent,
        )
    }
}
