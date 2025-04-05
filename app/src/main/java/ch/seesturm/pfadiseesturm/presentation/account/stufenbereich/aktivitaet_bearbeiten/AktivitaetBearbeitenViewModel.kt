package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

import android.text.Html
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Publish
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.TimePickerState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.account.service.StufenbereichService
import ch.seesturm.pfadiseesturm.domain.fcf.model.CloudFunctionEventPayload
import ch.seesturm.pfadiseesturm.domain.fcf.model.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.StufenbereichSheetMode
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.StufenbereichState
import ch.seesturm.pfadiseesturm.presentation.common.rich_text_editor.getHTMLForGoogleCalendar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarType
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
class AktivitaetBearbeitenViewModel(
    private val selectedSheetMode: StufenbereichSheetMode,
    private val service: StufenbereichService,
    private val stufe: SeesturmStufe,
    private val onDismiss: () -> Unit
): ViewModel() {

    private val _state = MutableStateFlow(
        AktivitaetBearbeitenState.create(
            initialAktivitaetState = when (selectedSheetMode) {
                StufenbereichSheetMode.Insert -> {
                    UiState.Success(Unit)
                }

                is StufenbereichSheetMode.Update -> {
                    UiState.Loading
                }
            },
            onTitleChanged = { newTitle ->
                updateTitleString(newTitle)
            },
            initialStartDate = DateTimeUtil.shared.nextSaturdayInCHTime(14),
            initialEndDate = DateTimeUtil.shared.nextSaturdayInCHTime(16),
        )
    )
    val state = _state.asStateFlow()

    init {
        fetchAktivitaetIfNecessary()
    }

    private val aktivitaetForPublishing: CloudFunctionEventPayload
        get() = CloudFunctionEventPayload(
            summary = state.value.title.text.trim(),
            description = state.value.description.getHTMLForGoogleCalendar(),
            location = state.value.location.trim(),
            isAllDay = false,
            start = state.value.start,
            end = state.value.end
        )
    val aktivitaetForPreview: GoogleCalendarEvent?
        get() = try {
            aktivitaetForPublishing.toGoogleCalendarEvent()
        } catch (e: Exception) {
            null
        }
    private val publishingValidationStatus: AktivitaetValidationStatus
        get() {

            val aktivitaet = aktivitaetForPublishing

            // errors
            if (aktivitaet.end < aktivitaet.start) {
                return AktivitaetValidationStatus.Error(
                    type = AktivitaetValidationStatusErrorType.Snackbar(
                        message = "Das Enddatum darf nicht vor dem Startdatum sein."
                    )
                )
            }
            if (aktivitaet.summary.isEmpty()) {
                return AktivitaetValidationStatus.Error(
                    type = AktivitaetValidationStatusErrorType.TitleTextField(
                        message = "Der Titel darf nicht leer sein."
                    )
                )
            }

            // warnings
            if (Duration.between(aktivitaet.start, aktivitaet.end).abs().toHours() < 2) {
                return AktivitaetValidationStatus.Warning(
                    title = "Aktivität kürzer als 2h",
                    description = "Die Aktivität ist kürzer als 2 Stunden. Möchtest du die Aktivität trotzdem ${selectedSheetMode.verb}?"
                )
            }
            if (ZonedDateTime.now().isAfter(aktivitaet.start)) {
                return AktivitaetValidationStatus.Warning(
                    title = "Startdatum in der Vergangenheit",
                    description = "Das Startdatum ist in der Vergangenheit. Möchtest du die Aktivität trotzdem ${selectedSheetMode.verb}?"
                )
            }
            if (ZonedDateTime.now().isAfter(aktivitaet.end)) {
                return AktivitaetValidationStatus.Warning(
                    title = "Enddatum in der Vergangenheit",
                    description = "Das Enddatum ist in der Vergangenheit. Möchtest du die Aktivität trotzdem ${selectedSheetMode.verb}?"
                )
            }
            if (aktivitaet.description.isEmpty()) {
                return AktivitaetValidationStatus.Warning(
                    title = "Beschreibung leer",
                    description = "Die Beschreibung ist leer. Möchtest du die Aktivität trotzdem ${selectedSheetMode.verb}?"
                )
            }
            if (aktivitaet.location.isEmpty()) {
                return AktivitaetValidationStatus.Warning(
                    title = "Kein Treffpunkt",
                    description = "Der Treffpunkt ist leer. Möchtest du die Aktivität trotzdem ${selectedSheetMode.verb}?"
                )
            }

            return AktivitaetValidationStatus.Valid
        }
    val confirmationDialogTitle: String
        get() = when (val localState = publishingValidationStatus) {
            AktivitaetValidationStatus.Valid -> {
                "Aktivität ${selectedSheetMode.verb}"
            }
            is AktivitaetValidationStatus.Error -> {
                "Fehlerhafte Aktivität"
            }
            is AktivitaetValidationStatus.Warning -> {
                localState.title
            }
        }
    val confirmationDialogDescription: String
        get() = when (val localState = publishingValidationStatus) {
            AktivitaetValidationStatus.Valid -> {
                if (state.value.sendPushNotification) {
                    "Die Aktivität wird mit Push-Nachricht ${selectedSheetMode.verbPassiv}."
                }
                else {
                    "Die Aktivität wird ohne Push-Nachricht ${selectedSheetMode.verbPassiv}."
                }
            }
            is AktivitaetValidationStatus.Error -> {
                when (localState.type) {
                    is AktivitaetValidationStatusErrorType.Snackbar -> localState.type.message
                    is AktivitaetValidationStatusErrorType.TitleTextField -> localState.type.message
                }
            }
            is AktivitaetValidationStatus.Warning -> {
                localState.description
            }
        }
    val confirmationDialogIcon: ImageVector
        get() = when (publishingValidationStatus) {
            is AktivitaetValidationStatus.Error, is AktivitaetValidationStatus.Warning -> {
                Icons.Outlined.Error
            }
            AktivitaetValidationStatus.Valid -> {
                Icons.Outlined.Publish
            }
        }
    val confirmationDialogConfirmButtonText: String
        get() = when (publishingValidationStatus) {
            AktivitaetValidationStatus.Valid -> {
                buttonTitle
            }
            else -> {
                "Trotzdem ${selectedSheetMode.verb}"
            }
        }

    val buttonTitle: String
        get() = selectedSheetMode.verb.replaceFirstChar { it.uppercase() }
    val startDateDateFormatted: String
        get() = DateTimeUtil.shared.formatDate(
            date = state.value.start,
            format = "dd.MM.yyyy",
            withRelativeDateFormatting = false
        )
    val startDateTimeFormatted: String
        get() = DateTimeUtil.shared.formatDate(
            date = state.value.start,
            format = "HH:mm",
            withRelativeDateFormatting = false
        )
    val endDateDateFormatted: String
        get() = DateTimeUtil.shared.formatDate(
            date = state.value.end,
            format = "dd.MM.yyyy",
            withRelativeDateFormatting = false
        )
    val endDateTimeFormatted: String
        get() = DateTimeUtil.shared.formatDate(
            date = state.value.end,
            format = "HH:mm",
            withRelativeDateFormatting = false
        )

    fun fetchAktivitaetIfNecessary() {

        if (selectedSheetMode !is StufenbereichSheetMode.Update) {
            return
        }
        _state.update {
            it.copy(
                aktivitaetState = UiState.Loading
            )
        }

        viewModelScope.launch {
            when (val result = service.fetchEvent(stufe = stufe, eventId = selectedSheetMode.id)) {
                is SeesturmResult.Error -> {
                    _state.update { 
                        it.copy(
                            aktivitaetState = UiState.Error("Aktivität konnte nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            title = it.title.copy(
                                text = result.data.title
                            ),
                            location = result.data.location ?: "",
                            start = result.data.startDate,
                            end = result.data.endDate,
                            description = RichTextState().setHtml(result.data.description ?: ""),
                            aktivitaetState = UiState.Success(Unit),
                            startDatePickerState = DatePickerState(
                                initialSelectedDateMillis = result.data.startDate.toInstant().toEpochMilli(),
                                locale = CalendarLocale.getDefault()
                            ),
                            startTimePickerState = TimePickerState(
                                initialHour = result.data.startDate.hour,
                                initialMinute = result.data.startDate.minute,
                                is24Hour = true
                            ),
                            endDatePickerState = DatePickerState(
                                initialSelectedDateMillis = result.data.endDate.toInstant().toEpochMilli(),
                                locale = CalendarLocale.getDefault()
                            ),
                            endTimePickerState = TimePickerState(
                                initialHour = result.data.endDate.hour,
                                initialMinute = result.data.endDate.minute,
                                is24Hour = true
                            )
                        )
                    }
                }
            }
        }
    }

    fun trySubmit() {

        when (val valStatus = publishingValidationStatus) {
            is AktivitaetValidationStatus.Error -> {
                when (val errorType = valStatus.type) {
                    is AktivitaetValidationStatusErrorType.Snackbar -> {
                        viewModelScope.launch {
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = errorType.message,
                                    duration = SnackbarDuration.Long,
                                    type = SnackbarType.Error,
                                    allowManualDismiss = true,
                                    onDismiss = {},
                                    showInSheetIfPossible = true
                                )
                            )
                        }
                    }
                    is AktivitaetValidationStatusErrorType.TitleTextField -> {
                        _state.update {
                            it.copy(
                                title = it.title.copy(
                                    state = SeesturmBinaryUiState.Error(errorType.message)
                                )
                            )
                        }
                    }
                }
            }
            AktivitaetValidationStatus.Valid, is AktivitaetValidationStatus.Warning -> {
                updateConfirmationDialogVisibility(true)
            }
        }
    }

    fun submit() {

        updatePublishAktivitaetState(ActionState.Loading(Unit))

        when (selectedSheetMode) {
            StufenbereichSheetMode.Insert -> {
                addNewAktivitaet()
            }
            is StufenbereichSheetMode.Update -> {
                updateExistingAktivitaet(selectedSheetMode.id)
            }
        }
    }

    private fun addNewAktivitaet() {

        val withNotification = state.value.sendPushNotification
        viewModelScope.launch {
            when (val result = service.addNewAktivitaet(
                event = aktivitaetForPublishing,
                stufe = stufe,
                withNotification = withNotification
            )) {
                is SeesturmResult.Error -> {
                    val message = if (withNotification) {
                        "Beim Veröffentlichen oder Senden der Push-Nachricht ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    }
                    else {
                        "Beim Veröffentlichen der Aktivität ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    }
                    updatePublishAktivitaetState(ActionState.Error(Unit, message))
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {},
                            showInSheetIfPossible = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    onDismiss()
                    val message = if (withNotification) {
                        "Aktivität erfolgreich veröffentlicht. Push-Nachricht versendet."
                    }
                    else {
                        "Aktivität erfolgreich veröffentlicht."
                    }
                    updatePublishAktivitaetState(ActionState.Success(Unit, message))
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SnackbarType.Success,
                            allowManualDismiss = true,
                            onDismiss = {},
                            showInSheetIfPossible = false
                        )
                    )
                }
            }
        }
    }

    private fun updateExistingAktivitaet(eventId: String) {

        val withNotification = state.value.sendPushNotification
        viewModelScope.launch {
            when (val result = service.updateExistingAktivitaet(
                eventId = eventId,
                event = aktivitaetForPublishing,
                stufe = stufe,
                withNotification = withNotification
            )) {
                is SeesturmResult.Error -> {
                    val message = if (withNotification) {
                        "Beim Aktualisieren oder Senden der Push-Nachricht ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    }
                    else {
                        "Beim Aktualisieren der Aktivität ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    }
                    updatePublishAktivitaetState(ActionState.Error(Unit, message))
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {},
                            showInSheetIfPossible = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    onDismiss()
                    val message = if (withNotification) {
                        "Aktivität erfolgreich aktualisiert. Push-Nachricht versendet."
                    }
                    else {
                        "Aktivität erfolgreich aktualisiert."
                    }
                    updatePublishAktivitaetState(ActionState.Error(Unit, message))
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SnackbarType.Success,
                            allowManualDismiss = true,
                            onDismiss = {},
                            showInSheetIfPossible = false
                        )
                    )
                }
            }
        }
    }

    private fun updateTitleString(newTitle: String) {
        _state.update {
            it.copy(
                title = state.value.title.copy(
                    text = newTitle,
                    state = SeesturmBinaryUiState.Success(Unit)
                )
            )
        }
    }
    fun updateLocation(newLocation: String) {
        _state.update {
            it.copy(
                location = newLocation
            )
        }
    }
    fun updateStartDate(year: Int, month: Int, dayOfMonth: Int) {
        _state.update {
            it.copy(
                start = it.start.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth)
            )
        }
    }
    fun updateStartDate(hour: Int, minute: Int) {
        _state.update {
            it.copy(
                start = it.start.withHour(hour).withMinute(minute)
            )
        }
    }
    fun updateEndDate(year: Int, month: Int, dayOfMonth: Int) {
        _state.update {
            it.copy(
                end = it.end.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth)
            )
        }
    }
    fun updateEndDate(hour: Int, minute: Int) {
        _state.update {
            it.copy(
                end = it.end.withHour(hour).withMinute(minute)
            )
        }
    }
    fun updatePushNotification(send: Boolean) {
        _state.update {
            it.copy(
                sendPushNotification = send
            )
        }
    }
    fun updateConfirmationDialogVisibility(isVisible: Boolean) {
        _state.update {
            it.copy(
                showConfirmationDialog = isVisible
            )
        }
    }
    private fun updatePublishAktivitaetState(state: ActionState<Unit>) {
        _state.update {
            it.copy(
                publishAktivitaetState = state
            )
        }
    }
}

sealed class AktivitaetValidationStatus {
    data object Valid: AktivitaetValidationStatus()
    data class Warning(
        val title: String,
        val description: String
    ): AktivitaetValidationStatus()
    data class Error(
        val type: AktivitaetValidationStatusErrorType
    ): AktivitaetValidationStatus()
}
sealed class AktivitaetValidationStatusErrorType {
    data class TitleTextField(
        val message: String
    ): AktivitaetValidationStatusErrorType()
    data class Snackbar(
        val message: String
    ): AktivitaetValidationStatusErrorType()
}