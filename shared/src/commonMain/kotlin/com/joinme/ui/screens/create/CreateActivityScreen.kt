package com.joinme.ui.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joinme.data.ActivityRepository
import com.joinme.domain.ActivityValidator
import com.joinme.model.ActivityType
import com.joinme.model.NewActivityForm
import com.joinme.model.ResidenceLocation
import com.joinme.ui.components.ActivityCard
import com.joinme.ui.components.FieldError
import com.joinme.ui.components.FormTextField
import com.joinme.ui.components.GlassKind
import com.joinme.ui.components.GlassSurface
import com.joinme.ui.components.GroupedSection
import com.joinme.ui.components.LocalToastState
import com.joinme.ui.components.NavBar
import com.joinme.ui.components.NavBarLayout
import com.joinme.ui.components.PrimaryButton
import com.joinme.ui.components.RowDivider
import com.joinme.ui.components.SectionFooter
import com.joinme.ui.components.SectionHeader
import com.joinme.ui.components.SelectablePill
import com.joinme.ui.components.glassSource
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.icons.icon
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.util.DateTimeUtils
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

/**
 * Form to create a new activity (US3 – Create).
 * When the activity is created, [onActivityCreated] receives its id.
 */
@Composable
fun CreateActivityScreen(
    onBack: () -> Unit,
    onActivityCreated: (String) -> Unit,
    repository: ActivityRepository,
    userId: String,
    viewModel: CreateActivityViewModel = viewModel { CreateActivityViewModel(repository, userId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val form = uiState.form
    val colors = JoinMeTheme.colors
    val toast = LocalToastState.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // The toast is shown by the Detail screen, which opens right after this one closes.
    LaunchedEffect(uiState.createdActivityId) {
        uiState.createdActivityId?.let {
            toast.show("Activity created")
            onActivityCreated(it)
        }
    }

    val scrollState = rememberScrollState()
    val scrolled by remember { derivedStateOf { scrollState.value > 0 } }
    val density = LocalDensity.current
    var bottomBarHeight by remember { mutableStateOf(0.dp) }

    // The whole screen moves above the keyboard.
    Box(modifier = Modifier.fillMaxSize().background(colors.background).imePadding()) {
        NavBarLayout(
            navBar = { NavBar(title = "New activity", scrolled = scrolled, onBack = onBack) },
        ) { barHeight ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .glassSource()
                    .verticalScroll(scrollState)
                    .padding(
                        start = JoinMeDimens.screenGutter,
                        end = JoinMeDimens.screenGutter,
                        top = barHeight,
                        bottom = bottomBarHeight + JoinMeDimens.spaceL,
                    ),
            ) {
                CreateForm(
                    form = form,
                    userId = userId,
                    viewModel = viewModel,
                    onDateClick = { showDatePicker = true },
                    onTimeClick = { showTimePicker = true },
                )
            }
        }

        // The reason why Create is disabled and the button stay visible at the bottom, over the form.
        GlassSurface(
            kind = GlassKind.BottomBar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .onSizeChanged { bottomBarHeight = with(density) { it.height.toDp() } },
        ) {
            Column(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = JoinMeDimens.spaceL, vertical = JoinMeDimens.spaceM),
            ) {
                // Screen readers announce the message when it changes.
                val announce = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
                val saveError = uiState.saveError
                val validationError = uiState.validationError
                if (saveError != null) {
                    FieldError(saveError, modifier = announce)
                } else if (validationError != null) {
                    Text(
                        text = validationError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = announce,
                    )
                }
                PrimaryButton(
                    text = "Create activity",
                    loadingText = "Creating…",
                    onClick = viewModel::onCreateClick,
                    enabled = uiState.canCreate,
                    isLoading = uiState.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (showDatePicker) {
        ActivityDatePickerDialog(
            initialDate = form.date,
            onDateSelected = viewModel::onDateSelected,
            onDismiss = { showDatePicker = false },
        )
    }
    if (showTimePicker) {
        ActivityTimePickerDialog(
            initialTime = form.startTime,
            onTimeSelected = viewModel::onTimeSelected,
            onDismiss = { showTimePicker = false },
        )
    }
}

/** The sections of the form, from the type of activity to the notes, and the preview. */
@Composable
private fun CreateForm(
    form: NewActivityForm,
    userId: String,
    viewModel: CreateActivityViewModel,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
) {
    val colors = JoinMeTheme.colors

    SectionHeader("What")
    GroupedSection {
        ChipGroup {
            ActivityType.entries.forEach { type ->
                SelectablePill(
                    text = type.displayName,
                    icon = type.icon,
                    selected = form.type == type,
                    onClick = { viewModel.onTypeSelected(type) },
                )
            }
        }
        if (form.type == ActivityType.OTHER) {
            RowDivider()
            FormTextField(
                value = form.customName,
                onValueChange = viewModel::onCustomNameChange,
                label = "Name",
                placeholder = "Name of the activity",
                singleLine = true,
            )
        }
    }

    SectionHeader("When")
    GroupedSection {
        PickerRow(
            label = "Date",
            value = form.date?.let { DateTimeUtils.formatDate(it) },
            onClick = onDateClick,
        )
        RowDivider()
        PickerRow(
            label = "Start time",
            value = form.startTime?.let { DateTimeUtils.formatTime(it) },
            onClick = onTimeClick,
        )
        RowDivider()
        Text(
            text = "Duration",
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
            modifier = Modifier.padding(start = JoinMeDimens.spaceL, top = JoinMeDimens.spaceM),
        )
        ChipGroup {
            CreateActivityViewModel.DURATION_OPTIONS.forEach { minutes ->
                SelectablePill(
                    text = DateTimeUtils.formatDuration(minutes),
                    selected = form.durationMinutes == minutes,
                    onClick = { viewModel.onDurationSelected(minutes) },
                )
            }
        }
    }

    SectionHeader("Where")
    GroupedSection {
        ChipGroup {
            ResidenceLocation.entries.forEach { location ->
                SelectablePill(
                    text = location.displayName,
                    icon = location.icon,
                    selected = form.location == location,
                    onClick = { viewModel.onLocationSelected(location) },
                )
            }
        }
    }

    SectionHeader("People")
    GroupedSection {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = JoinMeDimens.spaceL, vertical = JoinMeDimens.spaceS),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Maximum people", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
                Text("Including you", style = MaterialTheme.typography.bodyLarge, color = colors.textPrimary)
            }
            Stepper(
                value = form.maxParticipants,
                canDecrease = form.maxParticipants > ActivityValidator.MIN_PARTICIPANTS,
                canIncrease = form.maxParticipants < ActivityValidator.MAX_PARTICIPANTS,
                onDecrease = viewModel::onDecreaseParticipants,
                onIncrease = viewModel::onIncreaseParticipants,
            )
        }
    }

    SectionHeader("Notes")
    GroupedSection {
        FormTextField(
            value = form.description,
            onValueChange = viewModel::onDescriptionChange,
            label = "Notes (optional)",
            placeholder = "Any level is welcome. Do not include personal information.",
            singleLine = false,
        )
    }
    SectionFooter("${form.description.length} / ${ActivityValidator.MAX_DESCRIPTION_LENGTH}")

    previewItem(form, userId)?.let { preview ->
        SectionHeader("Preview")
        ActivityCard(item = preview)
    }
}

/** Chips of a section, one after another and wrapping to the next line. */
@Composable
private fun ChipGroup(content: @Composable () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(JoinMeDimens.spaceS),
        modifier = Modifier.padding(horizontal = JoinMeDimens.spaceL, vertical = JoinMeDimens.spaceXS),
    ) {
        content()
    }
}

/** Row that opens a picker. The value sits in a grey capsule, "Choose" in accent colour until it is set. */
@Composable
private fun PickerRow(label: String, value: String?, onClick: () -> Unit) {
    val colors = JoinMeTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = JoinMeDimens.spaceL, vertical = JoinMeDimens.spaceS),
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = colors.textPrimary, modifier = Modifier.weight(1f))
        Text(
            text = value ?: "Choose",
            style = MaterialTheme.typography.bodyLarge,
            color = if (value == null) colors.accent else colors.textPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colors.fill)
                .padding(horizontal = JoinMeDimens.spaceM, vertical = 6.dp),
        )
    }
}

/** iOS style stepper: a grey capsule with "−", the number and "+". */
@Composable
private fun Stepper(
    value: Int,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    val colors = JoinMeTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(JoinMeDimens.radiusS)).background(colors.fill),
    ) {
        StepperButton(AppIcons.Minus, "Fewer people", canDecrease, onDecrease)
        Text(
            text = "$value",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(32.dp),
        )
        StepperButton(AppIcons.Plus, "More people", canIncrease, onIncrease)
    }
}

@Composable
private fun StepperButton(icon: ImageVector, description: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = JoinMeTheme.colors
    // 44 dp to look at, 48 dp to tap.
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .size(44.dp)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) colors.textPrimary else colors.textSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp),
        )
    }
}
