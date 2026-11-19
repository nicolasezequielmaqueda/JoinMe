package com.joinme.ui.screens.detail

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joinme.data.ActivityRepository
import com.joinme.data.SupabaseErrors
import com.joinme.data.WeatherSource
import com.joinme.domain.ActivityRules
import com.joinme.model.ActivityStatus
import com.joinme.model.ResidenceLocation
import com.joinme.ui.components.ActionRow
import com.joinme.ui.components.ActivityIconTile
import com.joinme.ui.components.ActivityItem
import com.joinme.ui.components.CompactButton
import com.joinme.ui.components.ConfirmDialog
import com.joinme.ui.components.EmptyState
import com.joinme.ui.components.GlassKind
import com.joinme.ui.components.GlassSurface
import com.joinme.ui.components.GroupedSection
import com.joinme.ui.components.InfoRow
import com.joinme.ui.components.JoinedPill
import com.joinme.ui.components.LocalToastState
import com.joinme.ui.components.NavBar
import com.joinme.ui.components.NavBarLayout
import com.joinme.ui.components.PlacesIndicator
import com.joinme.ui.components.PrimaryButton
import com.joinme.ui.components.SecondaryButton
import com.joinme.ui.components.SectionFooter
import com.joinme.ui.components.SectionHeader
import com.joinme.ui.components.StatusPill
import com.joinme.ui.components.ToastHost
import com.joinme.ui.components.glassSource
import com.joinme.ui.components.placesText
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.icons.icon
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.ui.theme.placesColor
import com.joinme.util.DateTimeUtils
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

/**
 * Shows all the information of one activity and the Join / Leave / Cancel action.
 */
@Composable
fun ActivityDetailScreen(
    onBack: () -> Unit,
    repository: ActivityRepository,
    userId: String,
    weather: WeatherSource?,
    shareText: (String) -> Boolean,
    viewModel: ActivityDetailViewModel = viewModel {
        ActivityDetailViewModel(createSavedStateHandle(), repository, userId)
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toast = LocalToastState.current

    // Show the feedback message once, then tell the ViewModel it was shown.
    LaunchedEffect(uiState.message) {
        val message = uiState.message
        if (message != null) {
            toast.show(message, isWarning = uiState.messageIsError)
            viewModel.onMessageShown()
        }
    }

    val item = uiState.item
    val scrollState = rememberScrollState()
    val scrolled by remember { derivedStateOf { scrollState.value > 0 } }
    val density = LocalDensity.current
    var actionHeight by remember { mutableStateOf(0.dp) }

    Box(modifier = Modifier.fillMaxSize().background(JoinMeTheme.colors.background)) {
        NavBarLayout(
            navBar = {
                // The activity name appears in the bar when its big version has scrolled away.
                NavBar(
                    title = if (scrolled && item != null) item.activity.title else "",
                    scrolled = scrolled,
                    onBack = onBack,
                )
            },
        ) { barHeight ->
            if (item == null) {
                EmptyState(
                    icon = AppIcons.Discover,
                    title = "Activity not found",
                    message = "It may have been removed.",
                    modifier = Modifier.padding(top = barHeight),
                )
            } else {
                DetailContent(
                    item = item,
                    isWorking = uiState.isWorking,
                    isOffline = uiState.isOffline,
                    isRetrying = uiState.isRetrying,
                    weather = weather,
                    shareText = shareText,
                    onCancel = viewModel::onCancelConfirmed,
                    onRetry = viewModel::onRetryClick,
                    scrollState = scrollState,
                    topPadding = barHeight,
                    bottomPadding = actionHeight,
                )
            }
        }

        if (item != null) {
            // Keeps the main action at the bottom of the screen, over the content.
            ActionArea(
                item = item,
                isWorking = uiState.isWorking,
                isOffline = uiState.isOffline,
                isRetrying = uiState.isRetrying,
                onRetry = viewModel::onRetryClick,
                onJoin = viewModel::onJoinClick,
                onLeave = viewModel::onLeaveClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onSizeChanged { actionHeight = with(density) { it.height.toDp() } },
            )
        }

        ToastHost(
            state = toast,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = JoinMeDimens.spaceXXL, end = JoinMeDimens.spaceXXL, bottom = actionHeight + JoinMeDimens.spaceM),
        )
    }
}

@Composable
private fun DetailContent(
    item: ActivityItem,
    isWorking: Boolean,
    isOffline: Boolean,
    isRetrying: Boolean,
    weather: WeatherSource?,
    shareText: (String) -> Boolean,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    scrollState: ScrollState,
    topPadding: Dp,
    bottomPadding: Dp,
) {
    val activity = item.activity
    val colors = JoinMeTheme.colors
    val endTime = ActivityRules.endOf(activity).time
    val isActive = item.status == ActivityStatus.OPEN || item.status == ActivityStatus.FULL
    val free = activity.maxParticipants - activity.participantCount
    var showCancelDialog by remember { mutableStateOf(false) }

    // Allow scrolling when the content is taller than the screen. It goes under the bars.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .glassSource()
            .verticalScroll(scrollState)
            .padding(
                start = JoinMeDimens.screenGutter,
                end = JoinMeDimens.screenGutter,
                top = topPadding + JoinMeDimens.spaceL,
                bottom = bottomPadding + JoinMeDimens.spaceXXL,
            ),
    ) {
        // Hero: icon, name, when, where and status.
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = JoinMeDimens.spaceXS)) {
            ActivityIconTile(activity.type, size = 64.dp)
            Spacer(Modifier.width(JoinMeDimens.spaceL))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                )
                Text(
                    text = "${DateTimeUtils.formatDate(activity.date)} · " +
                        "${DateTimeUtils.formatTime(activity.startTime)}–${DateTimeUtils.formatTime(endTime)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary,
                )
                Text(
                    text = activity.location.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(JoinMeDimens.spaceS),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = JoinMeDimens.spaceS),
                ) {
                    StatusPill(item.status)
                    if (item.isJoined && !item.isCreator) JoinedPill()
                }
            }
        }

        SectionHeader("Places")
        GroupedSection {
            Column(
                verticalArrangement = Arrangement.spacedBy(JoinMeDimens.spaceM),
                modifier = Modifier.padding(JoinMeDimens.spaceL),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = placesText(item.status, activity.participantCount, activity.maxParticipants),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    if (item.isJoined && isActive) {
                        Icon(AppIcons.Check, contentDescription = null, tint = colors.accent, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("You're going", style = MaterialTheme.typography.bodyMedium, color = colors.accent)
                    }
                }
                PlacesIndicator(activity, color = placesColor(free))
                Text(
                    text = "${activity.participantCount} of ${activity.maxParticipants} people",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
            }
        }

        SectionHeader("Details")
        GroupedSection {
            InfoRow("Date", DateTimeUtils.formatDate(activity.date), AppIcons.Date)
            InfoRow(
                "Time",
                "${DateTimeUtils.formatTime(activity.startTime)} – ${DateTimeUtils.formatTime(endTime)}",
                AppIcons.Time,
            )
            InfoRow("Place", activity.location.displayName, activity.location.icon)
            InfoRow(
                "Duration",
                "${DateTimeUtils.formatDuration(activity.durationMinutes)} · ${activity.type.category.displayName}",
                AppIcons.Duration,
                showDivider = false,
            )
        }

        if (isActive) {
            ActivityInvite(activity, shareText)
            if (weather != null &&
                activity.location in listOf(ResidenceLocation.SPORTS_COURT, ResidenceLocation.GARDEN)
            ) {
                WeatherPanel(weather, activity.date, activity.startTime)
            }
        }

        if (activity.description.isNotBlank()) {
            SectionHeader("Notes")
            GroupedSection {
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(JoinMeDimens.spaceL),
                )
            }
        }

        if (item.isCreator && isActive) {
            Spacer(Modifier.height(JoinMeDimens.sectionGap))
            GroupedSection {
                ActionRow(
                    text = "Cancel activity",
                    icon = AppIcons.Delete,
                    color = colors.destructive,
                    onClick = { showCancelDialog = true },
                    enabled = !isWorking && !isOffline,
                    showDivider = false,
                )
            }
            if (isOffline) {
                SectionFooter(SupabaseErrors.offlineFor("Cancelling"))
                RetryButton(isRetrying = isRetrying, onClick = onRetry, modifier = Modifier.padding(horizontal = JoinMeDimens.spaceL))
            }
        }
    }

    if (showCancelDialog) {
        ConfirmDialog(
            title = "Cancel this activity?",
            text = "Participants will see it as cancelled. This cannot be undone.",
            confirmText = "Cancel activity",
            dismissText = "Keep it",
            onConfirm = {
                showCancelDialog = false
                onCancel()
            },
            onDismiss = { showCancelDialog = false },
        )
    }
}

@Composable
private fun RetryButton(isRetrying: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    CompactButton(
        text = if (isRetrying) "Checking…" else "Try again",
        onClick = onClick,
        enabled = !isRetrying,
        modifier = modifier,
    )
}

/**
 * Bottom bar with the free places on the left and, on the right:
 * - participant: Leave
 * - OPEN activity: Join
 * - otherwise: no button, a short explanation under the places instead.
 * Without connection the button is disabled and the explanation says why.
 * The creator cancels at the end of the page.
 */
@Composable
private fun ActionArea(
    item: ActivityItem,
    isWorking: Boolean,
    isOffline: Boolean,
    isRetrying: Boolean,
    onRetry: () -> Unit,
    onJoin: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = JoinMeTheme.colors
    val activity = item.activity
    val reason = when {
        item.status == ActivityStatus.CANCELLED -> "Cancelled by its creator."
        item.status == ActivityStatus.COMPLETED -> "This activity has finished."
        item.isCreator -> "You created this activity."
        item.isJoined || item.status == ActivityStatus.OPEN -> null
        else -> "This activity is full."
    }
    val buttonModifier = Modifier.widthIn(min = 160.dp)

    GlassSurface(kind = GlassKind.BottomBar, modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = JoinMeDimens.spaceL, vertical = JoinMeDimens.spaceM),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = placesText(item.status, activity.participantCount, activity.maxParticipants),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textSecondary,
                )
                if (reason != null) {
                    Text(text = reason, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                } else if (isOffline) {
                    Text(
                        text = SupabaseErrors.offlineFor(if (item.isJoined) "Leaving" else "Joining"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                    )
                    RetryButton(isRetrying = isRetrying, onClick = onRetry)
                }
            }
            Spacer(Modifier.width(JoinMeDimens.spaceM))
            when {
                reason != null -> Unit
                item.isJoined -> SecondaryButton(
                    text = "Leave activity",
                    loadingText = "Leaving…",
                    onClick = onLeave,
                    enabled = !isOffline,
                    isLoading = isWorking,
                    modifier = buttonModifier,
                )
                else -> PrimaryButton(
                    text = "Join activity",
                    loadingText = "Joining…",
                    onClick = onJoin,
                    enabled = !isOffline,
                    isLoading = isWorking,
                    modifier = buttonModifier,
                )
            }
        }
    }
}
