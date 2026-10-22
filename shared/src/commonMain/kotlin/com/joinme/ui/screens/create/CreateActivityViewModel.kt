package com.joinme.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.ActivityRepository
import com.joinme.data.CurrentUser
import com.joinme.domain.ActivityResult
import com.joinme.domain.ActivityValidator
import com.joinme.model.ActivityType
import com.joinme.model.NewActivityForm
import com.joinme.model.ResidenceLocation
import com.joinme.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * @property validationError first problem of the form, or null if it can be created.
 * @property createdActivityId set after a successful creation, so the screen can navigate.
 */
data class CreateActivityUiState(
    val form: NewActivityForm = NewActivityForm(),
    val validationError: String? = null,
    val isSaving: Boolean = false,
    val createdActivityId: String? = null,
) {
    val canCreate: Boolean
        get() = validationError == null && !isSaving
}

/**
 * Logic of the Create Activity screen (US3 – Create).
 * Check the form after every change to decide whether to enable Create.
 * The repository checks it again when saving, including the current time.
 */
class CreateActivityViewModel(private val repository: ActivityRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(stateFor(NewActivityForm()))
    val uiState: StateFlow<CreateActivityUiState> = _uiState.asStateFlow()

    fun onTypeSelected(type: ActivityType) = updateForm {
        // Suggest the usual number of people for this type (e.g. 2 for table tennis).
        it.copy(type = type, maxParticipants = type.defaultParticipants)
    }

    fun onCustomNameChange(name: String) = updateForm {
        it.copy(customName = name.take(ActivityValidator.MAX_CUSTOM_NAME_LENGTH))
    }

    fun onDateSelected(date: LocalDate) = updateForm { it.copy(date = date) }

    fun onTimeSelected(time: LocalTime) = updateForm { it.copy(startTime = time) }

    fun onDurationSelected(minutes: Int) = updateForm { it.copy(durationMinutes = minutes) }

    fun onLocationSelected(location: ResidenceLocation) = updateForm { it.copy(location = location) }

    fun onIncreaseParticipants() = updateForm {
        it.copy(maxParticipants = (it.maxParticipants + 1).coerceAtMost(ActivityValidator.MAX_PARTICIPANTS))
    }

    fun onDecreaseParticipants() = updateForm {
        it.copy(maxParticipants = (it.maxParticipants - 1).coerceAtLeast(ActivityValidator.MIN_PARTICIPANTS))
    }

    fun onDescriptionChange(text: String) = updateForm {
        it.copy(description = text.take(ActivityValidator.MAX_DESCRIPTION_LENGTH))
    }

    fun onCreateClick() {
        val state = _uiState.value
        if (!state.canCreate) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (val result = repository.createActivity(state.form, CurrentUser.ID)) {
                is ActivityResult.Success ->
                    _uiState.update { it.copy(isSaving = false, createdActivityId = result.activity.id) }
                is ActivityResult.Error ->
                    _uiState.update { it.copy(isSaving = false, validationError = result.message) }
            }
        }
    }

    private fun updateForm(change: (NewActivityForm) -> NewActivityForm) {
        _uiState.update { stateFor(change(it.form)).copy(isSaving = it.isSaving) }
    }

    private fun stateFor(form: NewActivityForm) = CreateActivityUiState(
        form = form,
        validationError = ActivityValidator.validate(form, DateTimeUtils.now()),
    )

    companion object {
        /** Duration options shown as chips, in minutes. */
        val DURATION_OPTIONS = listOf(30, 60, 90, 120, 180)
    }
}
