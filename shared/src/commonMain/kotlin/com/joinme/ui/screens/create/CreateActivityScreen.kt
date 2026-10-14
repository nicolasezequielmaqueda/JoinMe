package com.joinme.ui.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joinme.data.AppContainer
import com.joinme.domain.ActivityValidator
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import com.joinme.ui.components.GroupedSection
import com.joinme.ui.components.NavBar
import com.joinme.ui.components.PrimaryButton
import com.joinme.ui.components.SectionHeader
import com.joinme.ui.components.SelectablePill
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.icons.icon
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.ui.theme.categoryColors
import com.joinme.util.DateTimeUtils

/**
 * Form to create a new activity (US3 – Create).
 * When the activity is created, [onActivityCreated] receives its id.
 */
@Composable
fun CreateActivityScreen(
    onBack: () -> Unit,
    onActivityCreated: (String) -> Unit,
    viewModel: CreateActivityViewModel = viewModel { CreateActivityViewModel(AppContainer.activityRepository) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val form = uiState.form
    val colors = JoinMeTheme.colors

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.createdActivityId) {
        uiState.createdActivityId?.let(onActivityCreated)
    }

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { NavBar(title = "New activity", onBack = onBack) },
        // The hint and the Create button stay visible at the bottom.
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background)
                    // Stays above the keyboard and the system navigation bar.
                    .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                uiState.validationError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        // Screen readers announce the message when it changes.
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .semantics { liveRegion = LiveRegionMode.Polite },
                    )
                }
                PrimaryButton(
                    text = "Create activity",
                    onClick = viewModel::onCreateClick,
                    enabled = uiState.canCreate,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            SectionHeader("What")
            TypeGrid(selected = form.type, onSelect = viewModel::onTypeSelected)
            if (form.type == ActivityType.OTHER) {
                Spacer(Modifier.height(12.dp))
                FormTextField(
                    value = form.customName,
                    onValueChange = viewModel::onCustomNameChange,
                    placeholder = "Name of the activity",
                    singleLine = true,
                )
            }

            SectionHeader("When")
            GroupedSection {
                PickerRow(
                    icon = AppIcons.Date,
                    label = "Date",
                    value = form.date?.let { DateTimeUtils.formatDate(it) },
                    onClick = { showDatePicker = true },
                )
                HorizontalDivider(color = colors.separator, modifier = Modifier.padding(start = 48.dp))
                PickerRow(
                    icon = AppIcons.Time,
                    label = "Start time",
                    value = form.startTime?.let { DateTimeUtils.formatTime(it) },
                    onClick = { showTimePicker = true },
                )
            }

            SectionHeader("How long")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CreateActivityViewModel.DURATION_OPTIONS.forEach { minutes ->
                    SelectablePill(
                        text = DateTimeUtils.formatDuration(minutes),
                        selected = form.durationMinutes == minutes,
                        onClick = { viewModel.onDurationSelected(minutes) },
                    )
                }
            }

            SectionHeader("Where")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ResidenceLocation.entries.forEach { location ->
                    SelectablePill(
                        text = location.displayName,
                        icon = location.icon,
                        selected = form.location == location,
                        onClick = { viewModel.onLocationSelected(location) },
                    )
                }
            }

            SectionHeader("People")
            GroupedSection {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Icon(AppIcons.People, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${form.maxParticipants} people", style = MaterialTheme.typography.bodyLarge, color = colors.textPrimary)
                        Text("Including you", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    }
                    Stepper(
                        canDecrease = form.maxParticipants > ActivityValidator.MIN_PARTICIPANTS,
                        canIncrease = form.maxParticipants < ActivityValidator.MAX_PARTICIPANTS,
                        onDecrease = viewModel::onDecreaseParticipants,
                        onIncrease = viewModel::onIncreaseParticipants,
                    )
                }
            }

            SectionHeader("Notes (optional)")
            FormTextField(
                value = form.description,
                onValueChange = viewModel::onDescriptionChange,
                placeholder = "E.g. any level is welcome. No personal information.",
                singleLine = false,
            )
            Text(
                text = "${form.description.length} / ${ActivityValidator.MAX_DESCRIPTION_LENGTH}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp, end = 4.dp),
            )
            Spacer(Modifier.height(16.dp))
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

/**
 * Grid of activity types (4 per row). Unselected types are grey; the selected
 * one shows the colour of its category and a border, so the choice is obvious.
 */
@Composable
private fun TypeGrid(selected: ActivityType?, onSelect: (ActivityType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ActivityType.entries.chunked(TYPES_PER_ROW).forEach { rowTypes ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowTypes.forEach { type ->
                    TypeTile(
                        type = type,
                        isSelected = type == selected,
                        onClick = { onSelect(type) },
                        modifier = Modifier.weight(1f),
                    )
                }
                // Empty places keep all tiles the same width in the last row.
                repeat(TYPES_PER_ROW - rowTypes.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

private const val TYPES_PER_ROW = 4

@Composable
private fun TypeTile(type: ActivityType, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val colors = JoinMeTheme.colors
    val tone = categoryColors(type.category)
    val shape = RoundedCornerShape(16.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(shape)
            .background(if (isSelected) tone.background else colors.surface)
            .border(2.dp, if (isSelected) tone.content else Color.Transparent, shape)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
    ) {
        Icon(
            imageVector = type.icon,
            contentDescription = null,
            tint = if (isSelected) tone.content else colors.textSecondary,
            modifier = Modifier.size(26.dp),
        )
        Text(
            text = type.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

/** Row that opens a picker. Shows "Choose" in the accent colour until a value is set. */
@Composable
private fun PickerRow(icon: ImageVector, label: String, value: String?, onClick: () -> Unit) {
    val colors = JoinMeTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Icon(icon, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = colors.textPrimary, modifier = Modifier.weight(1f))
        Text(
            text = value ?: "Choose",
            style = MaterialTheme.typography.bodyLarge,
            color = if (value == null) colors.accent else colors.textSecondary,
        )
        Spacer(Modifier.width(8.dp))
        Icon(AppIcons.Chevron, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(14.dp))
    }
}

/** iOS style stepper: one grey capsule with "−" and "+". */
@Composable
private fun Stepper(canDecrease: Boolean, canIncrease: Boolean, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    val colors = JoinMeTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(colors.fill),
    ) {
        StepperButton(AppIcons.Minus, "Fewer people", canDecrease, onDecrease)
        Spacer(Modifier.width(1.dp).height(20.dp).background(colors.separator))
        StepperButton(AppIcons.Plus, "More people", canIncrease, onIncrease)
    }
}

@Composable
private fun StepperButton(icon: ImageVector, description: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = JoinMeTheme.colors
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (enabled) colors.textPrimary else colors.separator,
        modifier = Modifier
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .size(18.dp),
    )
}

/** Text field with a white rounded background and no underline. */
@Composable
private fun FormTextField(value: String, onValueChange: (String) -> Unit, placeholder: String, singleLine: Boolean) {
    val colors = JoinMeTheme.colors
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = colors.textSecondary) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(16.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = colors.accent,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}
