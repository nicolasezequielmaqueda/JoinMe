package com.joinme.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joinme.data.AppContainer
import com.joinme.domain.ActivityRules
import com.joinme.model.ActivityStatus
import com.joinme.ui.components.ActivityIconTile
import com.joinme.ui.components.ActivityItem
import com.joinme.ui.components.DestructiveButton
import com.joinme.ui.components.EmptyState
import com.joinme.ui.components.GroupedSection
import com.joinme.ui.components.InfoRow
import com.joinme.ui.components.JoinedPill
import com.joinme.ui.components.NavBar
import com.joinme.ui.components.ParticipantsBar
import com.joinme.ui.components.PrimaryButton
import com.joinme.ui.components.SecondaryButton
import com.joinme.ui.components.SectionHeader
import com.joinme.ui.components.StatusPill
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.icons.icon
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.ui.theme.destructiveColor
import com.joinme.util.DateTimeUtils

/**
 * Shows all the information of one activity and the Join / Leave / Cancel action.
 */
@Composable
fun ActivityDetailScreen(
    onBack: () -> Unit,
    viewModel: ActivityDetailViewModel = viewModel {
        ActivityDetailViewModel(createSavedStateHandle(), AppContainer.activityRepository)
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show the feedback message once, then tell the ViewModel it was shown.
    LaunchedEffect(uiState.message) {
        val message = uiState.message
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.onMessageShown()
        }
    }

    val item = uiState.item
    Scaffold(
        containerColor = JoinMeTheme.colors.background,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { NavBar(title = "", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // Keep the main action at the bottom of the screen.
        bottomBar = {
            if (item != null) {
                ActionArea(
                    item = item,
                    isWorking = uiState.isWorking,
                    onJoin = viewModel::onJoinClick,
                    onLeave = viewModel::onLeaveClick,
                    onCancel = viewModel::onCancelConfirmed,
                )
            }
        },
    ) { innerPadding ->
        if (item == null) {
            EmptyState(
                icon = AppIcons.Discover,
                title = "Activity not found",
                message = "It may have been removed.",
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            DetailContent(item = item, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun DetailContent(item: ActivityItem, modifier: Modifier = Modifier) {
    val activity = item.activity
    val colors = JoinMeTheme.colors
    val endTime = ActivityRules.endOf(activity).time

    // Allow scrolling when the content is taller than the screen.
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        // Header: icon, name, when and status.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            ActivityIconTile(activity.type, size = 80.dp)
            Text(
                text = activity.title,
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = "${DateTimeUtils.formatDate(activity.date)} · ${DateTimeUtils.formatTime(activity.startTime)}",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary,
                modifier = Modifier.padding(top = 4.dp),
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 12.dp),
            ) {
                StatusPill(item.status)
                if (item.isJoined && !item.isCreator) JoinedPill()
            }
        }

        SectionHeader("People")
        GroupedSection {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${activity.participantCount} of ${activity.maxParticipants} people",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                )
                Spacer(Modifier.height(10.dp))
                ParticipantsBar(activity)
                Text(
                    text = spotsText(item),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }

        SectionHeader("Details")
        GroupedSection {
            InfoRow(AppIcons.Date, "Date", DateTimeUtils.formatDate(activity.date))
            InfoRow(
                AppIcons.Time,
                "Time",
                "${DateTimeUtils.formatTime(activity.startTime)} – ${DateTimeUtils.formatTime(endTime)}",
            )
            InfoRow(AppIcons.Duration, "Duration", DateTimeUtils.formatDuration(activity.durationMinutes))
            InfoRow(activity.location.icon, "Place", activity.location.displayName)
            InfoRow(AppIcons.Category, "Category", activity.type.category.displayName, showDivider = false)
        }

        if (activity.description.isNotBlank()) {
            SectionHeader("Notes")
            GroupedSection {
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        if (item.isCreator) {
            Text(
                text = "You created this activity.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

/** Short sentence that explains the free places in a friendly way. */
private fun spotsText(item: ActivityItem): String {
    val free = item.activity.maxParticipants - item.activity.participantCount
    return when {
        item.status == ActivityStatus.CANCELLED -> "This activity was cancelled."
        item.status == ActivityStatus.COMPLETED -> "This activity has finished."
        free <= 0 -> "No places left."
        free == 1 -> "1 place left."
        else -> "$free places left."
    }
}

/**
 * Chooses which button to show:
 * - creator: Cancel (with confirmation)
 * - participant: Leave
 * - OPEN activity: Join
 * - otherwise: an explanation instead of a button.
 */
@Composable
private fun ActionArea(
    item: ActivityItem,
    isWorking: Boolean,
    onJoin: () -> Unit,
    onLeave: () -> Unit,
    onCancel: () -> Unit,
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    val colors = JoinMeTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        when {
            item.status == ActivityStatus.CANCELLED -> ActionInfo("This activity was cancelled by its creator.")
            item.status == ActivityStatus.COMPLETED -> ActionInfo("This activity has already finished.")
            item.isCreator -> DestructiveButton("Cancel activity", onClick = { showCancelDialog = true }, enabled = !isWorking)
            item.isJoined -> SecondaryButton("Leave activity", onClick = onLeave, enabled = !isWorking)
            item.status == ActivityStatus.OPEN -> PrimaryButton("Join activity", onClick = onJoin, enabled = !isWorking)
            else -> ActionInfo("This activity is full. Try another one or create your own.")
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel this activity?") },
            text = { Text("Participants will see it as cancelled. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    onCancel()
                }) { Text("Cancel activity", color = destructiveColor()) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Keep it") }
            },
        )
    }
}

@Composable
private fun ActionInfo(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = JoinMeTheme.colors.textSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
    )
}
