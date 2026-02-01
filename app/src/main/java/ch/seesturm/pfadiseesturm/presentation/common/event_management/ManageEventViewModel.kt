package ch.seesturm.pfadiseesturm.presentation.common.event_management

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Publish
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.account.service.StufenbereichService
import ch.seesturm.pfadiseesturm.domain.fcf.model.CloudFunctionEventPayload
import ch.seesturm.pfadiseesturm.domain.fcf.model.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AnlaesseService
import ch.seesturm.pfadiseesturm.presentation.common.event_management.controllers.ManageAktivitaetController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.controllers.ManageAktivitaetenController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.controllers.ManageTerminController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.EventManagementController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.MultiStufenCapableEventController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.PushNotificationCapableEventController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.TemplatesCapableEventController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces.UpdateCapableEventController
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventManagementMode
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventPreviewType
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventToManageType
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventValidationStatus
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventValidationStatusErrorType
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmRichTextState
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.getUnescapedHtml
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.Binding
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
class ManageEventViewModel(
    private val stufenbereichService: StufenbereichService,
    private val anlaesseService: AnlaesseService,
    val eventType: EventToManageType
): ViewModel() {

    val controller: EventManagementController = when (eventType) {
        is EventToManageType.Aktivitaet -> ManageAktivitaetController(service = stufenbereichService, stufe = eventType.stufe)
        EventToManageType.MultipleAktivitaeten -> ManageAktivitaetenController(service = stufenbereichService)
        is EventToManageType.Termin -> ManageTerminController(service = anlaesseService, calendar = eventType.calendar)
    }
    val mode: EventManagementMode = when (eventType) {
        is EventToManageType.Aktivitaet -> eventType.mode
        EventToManageType.MultipleAktivitaeten -> EventManagementMode.Insert
        is EventToManageType.Termin -> eventType.mode
    }

    private val _commonState = MutableStateFlow(
        ManageEventCommonState.create(
            eventType = eventType,
            mode = mode,
            onTitleChanged = { updateTitle(it) },
            onDescriptionChanged = { updateDescriptionState() },
            onLocationChange = { updateLocation(it) },
            onAllDayChange = { updateAllDay(it) }
        )
    )
    val state: StateFlow<ManageEventState> = combine(
        flow = _commonState,
        flow2 = (controller as? PushNotificationCapableEventController)?.let { c ->
            c.sendPushNotification.map { f ->
                Binding(
                    get = { f },
                    set = { c.setSendPushNotification(it) }
                )
            }
        } ?: flowOf(null),
        flow3 = (controller as? TemplatesCapableEventController)?.templatesState ?: flowOf(null),
        flow4 = (controller as? TemplatesCapableEventController)?.let { c ->
            c.showTemplatesSheet.map { f ->
                Binding(
                    get = { f },
                    set = { c.setShowTemplatesSheet(it) }
                )
            }
        } ?: flowOf(null),
        flow5 = (controller as? MultiStufenCapableEventController)?.let { c ->
            c.selectedStufen.map { f ->
                Binding(
                    get = { f },
                    set = { c.setSelectedStufen(it) }
                )
            }
        } ?: flowOf(null)
    ) { commonState, sendPushNotification, templatesState, showTemplatesSheet, selectedStufen ->
        ManageEventState(
            eventState = commonState.eventState,
            publishEventState = commonState.publishEventState,
            title = commonState.title,
            location = commonState.location,
            start = commonState.start,
            end = commonState.end,
            isAllDay = commonState.isAllDay,
            showConfirmationDialog = commonState.showConfirmationDialog,
            previewSheetItem = Binding(
                get = {
                    eventForPreview.let { e ->
                        if (commonState.showPreviewSheet) {
                            e
                        }
                        else {
                            null
                        }
                    }
                },
                set = { newItem ->
                    if (newItem == null) {
                        _commonState.update {
                            it.copy(
                                showPreviewSheet = false
                            )
                        }
                    }
                }
            ),
            description = commonState.description,
            templatesState = templatesState,
            sendPushNotification = sendPushNotification,
            showTemplatesSheet = showTemplatesSheet,
            selectedStufen = selectedStufen,
            startDatePickerState = commonState.startDatePickerState,
            startTimePickerState = commonState.startTimePickerState,
            endDatePickerState = commonState.endDatePickerState,
            endTimePickerState = commonState.endTimePickerState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ManageEventState(
            eventState = _commonState.value.eventState,
            publishEventState = _commonState.value.publishEventState,
            title = _commonState.value.title,
            location = _commonState.value.location,
            start = _commonState.value.start,
            end = _commonState.value.end,
            isAllDay = _commonState.value.isAllDay,
            showConfirmationDialog = _commonState.value.showConfirmationDialog,
            previewSheetItem = Binding(
                get = { null },
                set = { newItem ->
                    if (newItem == null) {
                        _commonState.update {
                            it.copy(
                                showPreviewSheet = false
                            )
                        }
                    }
                }
            ),
            description = _commonState.value.description,
            templatesState = (controller as? TemplatesCapableEventController)?.templatesState?.value,
            sendPushNotification = (controller as? PushNotificationCapableEventController)?.let { c ->
                Binding(
                    get = { c.sendPushNotification.value },
                    set = { c.setSendPushNotification(it) }
                )
            },
            showTemplatesSheet = (controller as? TemplatesCapableEventController)?.let { c ->
                Binding(
                    get = { c.showTemplatesSheet.value },
                    set = { c.setShowTemplatesSheet(it) }
                )
            },
            selectedStufen = (controller as? MultiStufenCapableEventController)?.let { c ->
                Binding(
                    get = { c.selectedStufen.value },
                    set = { c.setSelectedStufen(it) }
                )
            },
            startDatePickerState = _commonState.value.startDatePickerState,
            startTimePickerState = _commonState.value.startTimePickerState,
            endDatePickerState = _commonState.value.endDatePickerState,
            endTimePickerState = _commonState.value.endTimePickerState
        )
    )

    init {
        fetchEventIfPossible()
        observeTemplatesIfPossible()
    }

    val eventPreviewType: EventPreviewType
        get() = controller.eventPreviewType

    private val eventForPublishing: CloudFunctionEventPayload
        get() = CloudFunctionEventPayload(
            summary = state.value.title.text.trim(),
            description = state.value.description.state.getUnescapedHtml(),
            location = state.value.location.get().trim(),
            isAllDay = state.value.isAllDay.get(),
            start = if (state.value.isAllDay.get()) { state.value.start.toLocalDate().atStartOfDay(ZoneId.of("Europe/Zurich")) } else { state.value.start },
            end = if (state.value.isAllDay.get()) { state.value.end.toLocalDate().atStartOfDay(ZoneId.of("Europe/Zurich")) } else { state.value.end }
        )
    val eventForPreview: GoogleCalendarEvent?
        get() {
            return try {
                eventForPublishing.toGoogleCalendarEvent()
            }
            catch(_: Exception) {
                null
            }
        }
    private val publishingValidationStatus: EventValidationStatus
        get() = controller.validateEvent(event = eventForPublishing, isAllDay = state.value.isAllDay.get(), mode = mode)

    val confirmationDialogTitle: String
        get() = when(val localState = publishingValidationStatus) {
            EventValidationStatus.Valid -> {
                when (eventType) {
                    is EventToManageType.Aktivitaet -> "${eventType.stufe.aktivitaetDescription} ${mode.verb}"
                    EventToManageType.MultipleAktivitaeten -> "Aktivität ${mode.verb}"
                    is EventToManageType.Termin -> "Anlass ${mode.verb}"
                }
            }
            is EventValidationStatus.Error -> {
                when (eventType) {
                    is EventToManageType.Aktivitaet -> "Fehlerhafte ${eventType.stufe.aktivitaetDescription}"
                    EventToManageType.MultipleAktivitaeten -> "Fehlerhafte Aktivität"
                    is EventToManageType.Termin -> "Fehlerhafter Anlass"
                }
            }
            is EventValidationStatus.Warning -> localState.title
        }
    val confirmationDialogDescription: String
        get() = when (val localState = publishingValidationStatus) {
            EventValidationStatus.Valid -> {
                when (eventType) {
                    is EventToManageType.Aktivitaet -> "Die ${eventType.stufe} wird ${if (state.value.sendPushNotification?.get() == true) "mit" else "ohne"} Push-Nachricht ${mode.verbPassiv}"
                    EventToManageType.MultipleAktivitaeten -> "Die Aktivität wird ${if (state.value.sendPushNotification?.get() == true) "mit" else "ohne"} Push-Nachricht ${mode.verbPassiv}"
                    is EventToManageType.Termin -> "Möchtest du den Anlass wirklich ${mode.verb}?"
                }
            }
            is EventValidationStatus.Error -> {
                when (localState.type) {
                    is EventValidationStatusErrorType.Snackbar -> localState.type.message
                    is EventValidationStatusErrorType.TitleTextField -> localState.type.message
                }
            }
            is EventValidationStatus.Warning -> localState.description
        }
    val confirmationDialogButtonText: String
        get() = when(publishingValidationStatus) {
            EventValidationStatus.Valid -> {
                mode.nomen
            }
            else -> {
                "Trotzdem ${mode.verb}"
            }
        }
    val confirmationDialogIcon: ImageVector
        get() = when (publishingValidationStatus) {
            is EventValidationStatus.Error, is EventValidationStatus.Warning -> {
                Icons.Outlined.Error
            }
            EventValidationStatus.Valid -> {
                Icons.Outlined.Publish
            }
        }

    val onShowTemplatesSheet: (() -> Unit)?
        get() = (controller as? TemplatesCapableEventController)?.let { c ->
            { c.setShowTemplatesSheet(true) }
        }

    private fun updateTitle(title: String) {
        _commonState.update {
            it.copy(
                title = _commonState.value.title.copy(
                    text = title,
                    state = SeesturmBinaryUiState.Success(Unit)
                )
            )
        }
    }
    fun updatePreviewSheetVisibility(isVisible: Boolean) {
        _commonState.update {
            it.copy(
                showPreviewSheet = isVisible
            )
        }
    }
    fun updateConfirmationDialogVisibility(isVisible: Boolean) {
        _commonState.update {
            it.copy(
                showConfirmationDialog = isVisible
            )
        }
    }
    private fun updateDescriptionState() {
        _commonState.update {
            it.copy(
                description = SeesturmRichTextState(
                    state = it.description.state,
                    onValueChanged = it.description.onValueChanged
                )
            )
        }
    }
    fun updateAllDay(isAllDay: Boolean) {
        _commonState.update {
            it.copy(
                isAllDay = _commonState.value.isAllDay.copy(
                    get = { isAllDay }
                )
            )
        }
    }
    fun updateLocation(newLocation: String) {
        _commonState.update {
            it.copy(
                location = _commonState.value.location.copy(
                    get = { newLocation }
                )
            )
        }
    }
    fun updateStartDate(year: Int, month: Int, dayOfMonth: Int) {
        _commonState.update {
            it.copy(
                start = it.start.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth)
            )
        }
    }
    fun updateStartDate(hour: Int, minute: Int) {
        _commonState.update {
            it.copy(
                start = it.start.withHour(hour).withMinute(minute)
            )
        }
    }
    fun updateEndDate(year: Int, month: Int, dayOfMonth: Int) {
        _commonState.update {
            it.copy(
                end = it.end.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth)
            )
        }
    }
    fun updateEndDate(hour: Int, minute: Int) {
        _commonState.update {
            it.copy(
                end = it.end.withHour(hour).withMinute(minute)
            )
        }
    }

    fun fetchEventIfPossible() {

        val localMode = mode as? EventManagementMode.Update ?: return
        val localController = controller as? UpdateCapableEventController ?: return

        _commonState.update {
            it.copy(
                eventState = UiState.Loading
            )
        }

        viewModelScope.launch {
            when (val result = localController.fetchEvent(eventId = localMode.eventId)) {
                is SeesturmResult.Error -> {
                    val message = when (eventType) {
                        is EventToManageType.Aktivitaet -> "${eventType.stufe.aktivitaetDescription} konnte nicht geladen werden. ${result.error.defaultMessage}"
                        EventToManageType.MultipleAktivitaeten -> "Aktivität konnte nicht geladen werden. ${result.error.defaultMessage}"
                        is EventToManageType.Termin -> "Anlass konnte nicht geladen werden. ${result.error.defaultMessage}"
                    }
                    _commonState.update {
                        it.copy(
                            eventState = UiState.Error(message)
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _commonState.update {
                        it.copy(
                            title = it.title.copy(
                                text = result.data.title
                            ),
                            location = it.location.copy(
                                get = { result.data.location ?: "" }
                            ),
                            start = result.data.start,
                            end = result.data.end,
                            description = SeesturmRichTextState(
                                state = RichTextState().setHtml(result.data.description ?: ""),
                                onValueChanged = {
                                    updateDescriptionState()
                                }
                            ),
                            eventState = UiState.Success(Unit),
                            isAllDay = it.isAllDay.copy(
                                get = { result.data.isAllDay }
                            ),
                            startDatePickerState = DatePickerState(
                                initialSelectedDateMillis = result.data.start
                                    .toLocalDate()
                                    .atStartOfDay(ZoneOffset.UTC)
                                    .toInstant()
                                    .toEpochMilli(),
                                locale = CalendarLocale.getDefault()
                            ),
                            startTimePickerState = TimePickerState(
                                initialHour = result.data.start.hour,
                                initialMinute = result.data.start.minute,
                                is24Hour = true
                            ),
                            endDatePickerState = DatePickerState(
                                initialSelectedDateMillis = result.data.end
                                    .toLocalDate()
                                    .atStartOfDay(ZoneOffset.UTC)
                                    .toInstant()
                                    .toEpochMilli(),
                                locale = CalendarLocale.getDefault()
                            ),
                            endTimePickerState = TimePickerState(
                                initialHour = result.data.end.hour,
                                initialMinute = result.data.end.minute,
                                is24Hour = true
                            ),
                        )
                    }
                }
            }
        }
    }

    fun trySubmit() {

        when (val status = publishingValidationStatus) {
            is EventValidationStatus.Error -> {
                when (val errorType = status.type) {
                    is EventValidationStatusErrorType.Snackbar -> {
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
                    is EventValidationStatusErrorType.TitleTextField -> {
                        _commonState.update {
                            it.copy(
                                title = it.title.copy(
                                    state = SeesturmBinaryUiState.Error(errorType.message)
                                )
                            )
                        }
                    }
                }
            }
            EventValidationStatus.Valid, is EventValidationStatus.Warning -> {
                updateConfirmationDialogVisibility(true)
            }
        }
    }

    fun submit() {

        when (val localMode = mode) {
            EventManagementMode.Insert -> {
                viewModelScope.launch {
                    executeInsertOrUpdate {
                        controller.addEvent(event = eventForPublishing)
                    }
                }
            }
            is EventManagementMode.Update -> {
                val localController = controller as? UpdateCapableEventController ?: return
                viewModelScope.launch {
                    executeInsertOrUpdate {
                        localController.updateEvent(eventId = localMode.eventId, event = eventForPublishing)
                    }
                }
            }
        }
    }

    private suspend fun executeInsertOrUpdate(execute: suspend () -> SeesturmResult<Unit, DataError.CloudFunctionsError>) {

        _commonState.update {
            it.copy(
                publishEventState = ActionState.Loading(Unit)
            )
        }

        when (val result = execute()) {
            is SeesturmResult.Error -> {
                val message = when (eventType) {
                    is EventToManageType.Aktivitaet -> "Beim ${mode.nomen} der ${eventType.stufe.aktivitaetDescription} ${if (state.value.sendPushNotification?.get() == true) "oder beim Senden der Push-Nachricht " else ""}ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    EventToManageType.MultipleAktivitaeten -> "Beim ${mode.nomen} der Aktivitäten ${if (state.value.sendPushNotification?.get() == true) "oder beim Senden der Push-Nachrichten " else ""}ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                    is EventToManageType.Termin -> "Beim ${mode.nomen} des Anlasses ${if (state.value.sendPushNotification?.get() == true) "oder beim Senden der Push-Nachricht " else ""}ist ein Fehler aufgetreten. ${result.error.defaultMessage}"
                }
                _commonState.update {
                    it.copy(
                        publishEventState = ActionState.Error(Unit, message)
                    )
                }
                SnackbarController.showSnackbar(
                    snackbar = SeesturmSnackbar.Error(
                        message = message,
                        onDismiss = {
                            _commonState.update {
                                it.copy(
                                    publishEventState = ActionState.Idle
                                )
                            }
                        },
                        location = SeesturmSnackbarLocation.Default,
                        allowManualDismiss = true
                    )
                )
            }
            is SeesturmResult.Success -> {
                val message = when (eventType) {
                    is EventToManageType.Aktivitaet -> "${eventType.stufe.aktivitaetDescription} erfolgreich ${mode.verbPassiv}.${if (state.value.sendPushNotification?.get() == true) " Push-Nachricht gesendet" else ""}"
                    EventToManageType.MultipleAktivitaeten -> "Aktivitäten erfolgreich ${mode.verbPassiv}.${if (state.value.sendPushNotification?.get() == true) " Push-Nachrichten gesendet" else ""}"
                    is EventToManageType.Termin -> "Anlass erfolgreich ${mode.verbPassiv}.${if (state.value.sendPushNotification?.get() == true) " Push-Nachricht gesendet" else ""}"
                }
                _commonState.update {
                    it.copy(
                        publishEventState = ActionState.Success(Unit, message)
                    )
                }
                SnackbarController.showSnackbar(
                    snackbar = SeesturmSnackbar.Success(
                        message = message,
                        onDismiss = {
                            _commonState.update {
                                it.copy(
                                    publishEventState = ActionState.Idle
                                )
                            }
                        },
                        location = SeesturmSnackbarLocation.Default,
                        allowManualDismiss = true
                    )
                )
            }
        }
    }

    private fun observeTemplatesIfPossible() {

        (controller as? TemplatesCapableEventController)?.observeTemplates(viewModelScope)
            ?: return
    }

    fun useTemplateIfPossible(template: AktivitaetTemplate) {

        val localController = controller as? TemplatesCapableEventController ?: return
        _commonState.update {
            it.copy(
                description = SeesturmRichTextState(
                    state = RichTextState().setHtml(template.description),
                    onValueChanged = {
                        updateDescriptionState()
                    }
                )
            )
        }
        localController.setShowTemplatesSheet(false)
    }
}