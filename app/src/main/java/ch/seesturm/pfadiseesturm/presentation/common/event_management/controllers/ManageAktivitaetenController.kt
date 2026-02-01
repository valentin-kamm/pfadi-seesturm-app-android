package ch.seesturm.pfadiseesturm.presentation.common.event_management.controllers

import ch.seesturm.pfadiseesturm.domain.account.service.StufenbereichService
import ch.seesturm.pfadiseesturm.domain.fcf.model.CloudFunctionEventPayload
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.EventManagementController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.MultiStufenCapableEventController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.PushNotificationCapableEventController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.TemplatesCapableEventController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventManagementMode
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventPreviewType
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventValidationStatus
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventValidationStatusErrorType
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.Duration
import java.time.ZonedDateTime

class ManageAktivitaetenController(
    private val service: StufenbereichService
): EventManagementController, MultiStufenCapableEventController, PushNotificationCapableEventController, TemplatesCapableEventController {

    private val _sendPushNotification = MutableStateFlow(true)
    override val sendPushNotification = _sendPushNotification.asStateFlow()
    private val _templatesState = MutableStateFlow<UiState<List<AktivitaetTemplate>>>(UiState.Loading)
    override val templatesState = _templatesState.asStateFlow()
    private val _showTemplatesSheet = MutableStateFlow(false)
    override val showTemplatesSheet = _showTemplatesSheet.asStateFlow()
    private val _selectedStufen = MutableStateFlow(SeesturmStufe.entries.toSet())
    override val selectedStufen = _selectedStufen.asStateFlow()

    override val eventPreviewType: EventPreviewType
        get() = EventPreviewType.MultipleAktivitaeten(selectedStufen.value)

    override fun setSendPushNotification(isOn: Boolean) {
        _sendPushNotification.update {
            isOn
        }
    }
    override fun setSelectedStufen(stufen: Set<SeesturmStufe>) {
        _selectedStufen.update {
            stufen
        }
    }
    override fun setShowTemplatesSheet(show: Boolean) {
        _showTemplatesSheet.update {
            show
        }
    }

    override fun validateEvent(
        event: CloudFunctionEventPayload,
        isAllDay: Boolean,
        mode: EventManagementMode
    ): EventValidationStatus {

        // errors
        if (event.end < event.start) {
            return EventValidationStatus.Error(
                type = EventValidationStatusErrorType.Snackbar(
                    message = "Das Enddatum darf nicht vor dem Startdatum sein."
                )
            )
        }
        if (event.summary.isEmpty()) {
            return EventValidationStatus.Error(
                type = EventValidationStatusErrorType.TitleTextField(
                    message = "Der Titel darf nicht leer sein."
                )
            )
        }

        // warnings
        if (Duration.between(event.start, event.end).abs().toHours() < 2 && !isAllDay) {
            return EventValidationStatus.Warning(
                title = "Aktivität kürzer als 2h",
                description = "Die Aktivität ist kürzer als 2 Stunden. Möchtest du die Aktivität trotzdem ${mode.verb}?"
            )
        }
        if (ZonedDateTime.now().isAfter(event.start)) {
            return EventValidationStatus.Warning(
                title = "Startdatum in der Vergangenheit",
                description = "Das Startdatum ist in der Vergangenheit. Möchtest du die Aktivität trotzdem ${mode.verb}?"
            )
        }
        if (ZonedDateTime.now().isAfter(event.end)) {
            return EventValidationStatus.Warning(
                title = "Enddatum in der Vergangenheit",
                description = "Das Enddatum ist in der Vergangenheit. Möchtest du die Aktivität trotzdem ${mode.verb}?"
            )
        }
        if (event.description.isEmpty()) {
            return EventValidationStatus.Warning(
                title = "Beschreibung leer",
                description = "Die Beschreibung ist leer. Möchtest du die Aktivität trotzdem ${mode.verb}?"
            )
        }
        if (event.location.isEmpty()) {
            return EventValidationStatus.Warning(
                title = "Kein Treffpunkt",
                description = "Der Treffpunkt ist leer. Möchtest du die Aktivität trotzdem ${mode.verb}?"
            )
        }

        return EventValidationStatus.Valid
    }

    override suspend fun addEvent(event: CloudFunctionEventPayload): SeesturmResult<Unit, DataError.CloudFunctionsError> =
        service.addMultipleAktivitaeten(event = event, stufen = this.selectedStufen.value, withNotification = this.sendPushNotification.value)

    override fun observeTemplates(viewModelScope: CoroutineScope) {

        _templatesState.update {
            UiState.Loading
        }

        service.observeAktivitaetTemplates(this.selectedStufen.value).onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _templatesState.update {
                        UiState.Error("Die Vorlagen konnten nicht geladen werden. ${result.error.defaultMessage}")
                    }
                }
                is SeesturmResult.Success -> {
                    _templatesState.update {
                        UiState.Success(result.data)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}