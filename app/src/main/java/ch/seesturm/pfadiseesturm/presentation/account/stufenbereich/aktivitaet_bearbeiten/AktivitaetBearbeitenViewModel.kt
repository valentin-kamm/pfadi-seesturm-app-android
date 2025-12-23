package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Publish
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.account.service.StufenbereichService
import ch.seesturm.pfadiseesturm.domain.fcf.model.CloudFunctionEventPayload
import ch.seesturm.pfadiseesturm.domain.fcf.model.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmRichTextState
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.getUnescapedHtml
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
class AktivitaetBearbeitenViewModel(
    private val mode: AktivitaetBearbeitenMode,
    private val service: StufenbereichService,
    private val stufe: SeesturmStufe,
): ViewModel() {

    private val _state = MutableStateFlow(
        AktivitaetBearbeitenState.create(
            initialAktivitaetState = when (mode) {
                AktivitaetBearbeitenMode.Insert -> {
                    UiState.Success(Unit)
                }
                is AktivitaetBearbeitenMode.Update -> {
                    UiState.Loading
                }
            },
            onTitleChanged = { newTitle ->
                updateTitleString(newTitle)
            },
            stufe = stufe,
            onDescriptionChanged = {
                updateDescriptionState()
            }
        )
    )
    val state = _state.asStateFlow()

    init {
        fetchAktivitaetIfNecessary()
        observeTemplates()
    }

    private val aktivitaetForPublishing: CloudFunctionEventPayload
        get() {
            return CloudFunctionEventPayload(
                summary = _state.value.title.text.trim(),
                description = state.value.description.state.getUnescapedHtml(),
                location = state.value.location.trim(),
                start = state.value.start,
                end = state.value.end
            )
        }
    val aktivitaetForPreview: GoogleCalendarEvent?
        get() {
            return try {
                aktivitaetForPublishing.toGoogleCalendarEvent()
            }
            catch (e: Exception) {
                null
            }
        }
    val aktivitaetForPreviewSheet = mutableStateOf<GoogleCalendarEvent?>(null)

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
                    description = "Die Aktivität ist kürzer als 2 Stunden. Möchtest du die Aktivität trotzdem ${mode.verb}?"
                )
            }
            if (ZonedDateTime.now().isAfter(aktivitaet.start)) {
                return AktivitaetValidationStatus.Warning(
                    title = "Startdatum in der Vergangenheit",
                    description = "Das Startdatum ist in der Vergangenheit. Möchtest du die Aktivität trotzdem ${mode.verb}?"
                )
            }
            if (ZonedDateTime.now().isAfter(aktivitaet.end)) {
                return AktivitaetValidationStatus.Warning(
                    title = "Enddatum in der Vergangenheit",
                    description = "Das Enddatum ist in der Vergangenheit. Möchtest du die Aktivität trotzdem ${mode.verb}?"
                )
            }
            if (aktivitaet.description.isEmpty()) {
                return AktivitaetValidationStatus.Warning(
                    title = "Beschreibung leer",
                    description = "Die Beschreibung ist leer. Möchtest du die Aktivität trotzdem ${mode.verb}?"
                )
            }
            if (aktivitaet.location.isEmpty()) {
                return AktivitaetValidationStatus.Warning(
                    title = "Kein Treffpunkt",
                    description = "Der Treffpunkt ist leer. Möchtest du die Aktivität trotzdem ${mode.verb}?"
                )
            }

            return AktivitaetValidationStatus.Valid
        }
    val confirmationDialogTitle: String
        get() = when (val localState = publishingValidationStatus) {
            AktivitaetValidationStatus.Valid -> {
                "Aktivität ${mode.verb}"
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
                    "Die Aktivität wird mit Push-Nachricht ${mode.verbPassiv}."
                }
                else {
                    "Die Aktivität wird ohne Push-Nachricht ${mode.verbPassiv}."
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
                mode.buttonTitle
            }
            else -> {
                "Trotzdem ${mode.verb}"
            }
        }

    fun fetchAktivitaetIfNecessary() {

        if (mode !is AktivitaetBearbeitenMode.Update) {
            return
        }

        _state.update {
            it.copy(
                aktivitaetState = UiState.Loading
            )
        }

        viewModelScope.launch {

            when (
                val result = service.fetchEvent(
                    stufe = stufe,
                    eventId = mode.id,
                    cacheIdentifier = MemoryCacheIdentifier.ForceReload
                )
            ) {
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
                            start = result.data.start,
                            end = result.data.end,
                            description = SeesturmRichTextState(
                                state = RichTextState().setHtml(result.data.description ?: ""),
                                onValueChanged = {
                                    updateDescriptionState()
                                }
                            ),
                            aktivitaetState = UiState.Success(Unit),
                            startDatePickerState = DatePickerState(
                                initialSelectedDateMillis = result.data.start.toInstant().toEpochMilli(),
                                locale = CalendarLocale.getDefault()
                            ),
                            startTimePickerState = TimePickerState(
                                initialHour = result.data.start.hour,
                                initialMinute = result.data.start.minute,
                                is24Hour = true
                            ),
                            endDatePickerState = DatePickerState(
                                initialSelectedDateMillis = result.data.end.toInstant().toEpochMilli(),
                                locale = CalendarLocale.getDefault()
                            ),
                            endTimePickerState = TimePickerState(
                                initialHour = result.data.end.hour,
                                initialMinute = result.data.end.minute,
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
                            SnackbarController.showSnackbar(
                                snackbar = SeesturmSnackbar.Error(
                                    message = errorType.message,
                                    onDismiss = {},
                                    location = SeesturmSnackbarLocation.Default,
                                    allowManualDismiss = true
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

        when (mode) {
            AktivitaetBearbeitenMode.Insert -> {
                addAktivitaet()
            }
            is AktivitaetBearbeitenMode.Update -> {
                updateAktivitaet(mode.id)
            }
        }
    }

    private fun addAktivitaet() {

        val withNotification = state.value.sendPushNotification
        viewModelScope.launch {
            when (val result = service.addNewAktivitaet(
                event = aktivitaetForPublishing,
                stufe = stufe,
                withNotification = withNotification
            )) {
                is SeesturmResult.Error -> {
                    val message = if (withNotification) {
                        "Beim Veröffentlichen der Aktivität oder beim Senden der Push-Nachricht ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    }
                    else {
                        "Beim Veröffentlichen der Aktivität ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    }
                    updatePublishAktivitaetState(ActionState.Error(Unit, message))
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Error(
                            message = message,
                            onDismiss = {
                                updatePublishAktivitaetState(ActionState.Idle)
                            },
                            location = SeesturmSnackbarLocation.Default,
                            allowManualDismiss = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    val message = if (withNotification) {
                        "Aktivität erfolgreich veröffentlicht. Push-Nachricht gesendet."
                    }
                    else {
                        "Aktivität erfolgreich veröffentlicht."
                    }
                    updatePublishAktivitaetState(ActionState.Success(Unit, message))
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Success(
                            message = message,
                            onDismiss = {
                                updatePublishAktivitaetState(ActionState.Idle)
                            },
                            location = SeesturmSnackbarLocation.Default,
                            allowManualDismiss = true
                        )
                    )
                }
            }
        }
    }

    private fun updateAktivitaet(eventId: String) {

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
                        "Beim Aktualisieren der Aktivität oder beim Senden der Push-Nachricht ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    }
                    else {
                        "Beim Aktualisieren der Aktivität ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    }
                    updatePublishAktivitaetState(ActionState.Error(Unit, message))
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Error(
                            message = message,
                            onDismiss = {
                                updatePublishAktivitaetState(ActionState.Idle)
                            },
                            location = SeesturmSnackbarLocation.Default,
                            allowManualDismiss = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    val message = if (withNotification) {
                        "Aktivität erfolgreich aktualisiert. Push-Nachricht gesendet."
                    }
                    else {
                        "Aktivität erfolgreich aktualisiert."
                    }
                    updatePublishAktivitaetState(ActionState.Error(Unit, message))
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Success(
                            message = message,
                            onDismiss = {
                                updatePublishAktivitaetState(ActionState.Idle)
                            },
                            location = SeesturmSnackbarLocation.Default,
                            allowManualDismiss = true
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
    private fun updateDescriptionState() {
        _state.update {
            it.copy(
                description = SeesturmRichTextState(
                    state = it.description.state,
                    onValueChanged = it.description.onValueChanged
                )
            )
        }
    }

    private fun observeTemplates() {

        _state.update {
            it.copy(
                templatesState = UiState.Loading
            )
        }
        service.observeAktivitaetTemplates(stufe).onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            templatesState = UiState.Error("Die Vorlagen für die ${stufe.stufenName} konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            templatesState = UiState.Success(result.data)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun useTemplate(template: AktivitaetTemplate) {
        _state.update {
            it.copy(
                description = SeesturmRichTextState(
                    state = RichTextState().setHtml(template.description),
                    onValueChanged = {
                        updateDescriptionState()
                    }
                )
            )
        }
    }
}