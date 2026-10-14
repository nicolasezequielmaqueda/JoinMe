package com.joinme.ui.screens.create

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import com.joinme.util.DateTimeUtils
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** Material date picker. Past days cannot be selected. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDatePickerDialog(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val todayMillis = DateTimeUtils.dateToPickerMillis(DateTimeUtils.today())
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.let { DateTimeUtils.dateToPickerMillis(it) },
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= todayMillis
        },
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let { onDateSelected(DateTimeUtils.pickerMillisToDate(it)) }
                    onDismiss()
                },
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    ) {
        DatePicker(state = state)
    }
}

/** Material time picker in 24 h format, shown inside a simple dialog. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTimePickerDialog(
    initialTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = initialTime?.hour ?: 18,
        initialMinute = initialTime?.minute ?: 0,
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start time") },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(LocalTime(state.hour, state.minute))
                    onDismiss()
                },
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
