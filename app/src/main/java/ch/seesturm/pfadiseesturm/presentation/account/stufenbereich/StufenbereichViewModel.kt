package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.seesturm.pfadiseesturm.domain.account.service.StufenbereichService
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.domain.wordpress.model.toAktivitaetWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarType
import ch.seesturm.pfadiseesturm.presentation.home.HomeListState
import ch.seesturm.pfadiseesturm.util.AktivitaetInteraction
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class StufenbereichViewModel(
    private val stufe: SeesturmStufe,
    private val service: StufenbereichService
): ViewModel() {

    private val _state = MutableStateFlow(StufenbereichState())
    val state = _state.asStateFlow()

    init {
        getAktivitaeten(false)
        observeAnAbmeldungen()
    }

    val abmeldungenState: UiState<List<GoogleCalendarEventWithAnAbmeldungen>>
        get() = when (val localAktivitaetenState = state.value.aktivitaetenState) {
            UiState.Loading -> {
                UiState.Loading
            }
            is UiState.Error -> {
                UiState.Error(localAktivitaetenState.message)
            }
            is UiState.Success -> {
                when (val localAnAbmeldungenState = state.value.anAbmeldungenState) {
                    UiState.Loading -> {
                        UiState.Loading
                    }
                    is UiState.Error -> {
                        UiState.Error(localAnAbmeldungenState.message)
                    }
                    is UiState.Success -> {
                        val combined = localAktivitaetenState.data.map { it.toAktivitaetWithAnAbmeldungen(anAbmeldungen = localAnAbmeldungenState.data) }
                        UiState.Success(combined)
                    }
                }
            }
        }
    val selectedDateFormatted: String
        get() = DateTimeUtil.shared.formatDate(
            date = state.value.selectedDate,
            format = "dd.MM.yyyy",
            withRelativeDateFormatting = false,
            includeTimeInRelativeFormatting = false
        )
    private val mustReloadAktivitaeten: Boolean
        get() = when (val localState = state.value.aktivitaetenState) {
            is UiState.Success -> {
                val endDates = localState.data.map { it.endDate }
                val oldestEndDate = endDates.minOrNull()
                if (oldestEndDate != null) {
                    state.value.selectedDate < oldestEndDate
                }
                else {
                    true
                }
            }
            else -> {
                false
            }
        }

    fun isEditButtonLoading(aktivitaet: GoogleCalendarEventWithAnAbmeldungen): Boolean {
        val localDeleteState = state.value.deleteAbmeldungenState
        val localPushNotificationState = state.value.sendPushNotificationState
        if (localDeleteState is ActionState.Loading) {
            return localDeleteState.action == aktivitaet
        }
        if (localPushNotificationState is ActionState.Loading) {
            return localPushNotificationState.action == aktivitaet
        }
        return false
    }

    fun refresh() {
        getAktivitaeten(true)
    }

    private fun reloadAktivitaetenIfNecessary() {
        if (mustReloadAktivitaeten) {
            getAktivitaeten(false)
        }
    }

    fun getAktivitaeten(isPullToRefresh: Boolean) {

        if (!isPullToRefresh) {
            _state.update {
                it.copy(
                    aktivitaetenState = UiState.Loading
                )
            }
        }
        else {
            changeRefreshStatus(true)
        }
        viewModelScope.launch {
            when (val result = service.fetchEvents(stufe = stufe, timeMin = state.value.selectedDate)) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            aktivitaetenState = UiState.Error("Aktivitäten der ${stufe.stufenName} konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            aktivitaetenState = UiState.Success(result.data)
                        )
                    }
                }
            }
        }.invokeOnCompletion {
            if (isPullToRefresh) {
                changeRefreshStatus(false)
            }
        }
    }

    private fun observeAnAbmeldungen() {
        _state.update {
            it.copy(
                anAbmeldungenState = UiState.Loading
            )
        }
        service.observeAnAbmeldungen(stufe = stufe).onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            anAbmeldungenState = UiState.Error("An- und Abmeldungen konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            anAbmeldungenState = UiState.Success(result.data)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteAnAbmeldungenForAktivitaet(aktivitaet: GoogleCalendarEventWithAnAbmeldungen) {
        _state.update {
            it.copy(
                deleteAbmeldungenState = ActionState.Loading(aktivitaet)
            )
        }
        viewModelScope.launch {
            when (val result = service.deleteAnAbmeldungen(aktivitaet)) {
                is SeesturmResult.Error -> {
                    val message = "An- und Abmeldungen für ${aktivitaet.event.title} konnten nicht gelöscht werden. ${result.error.defaultMessage}"
                    _state.update {
                        it.copy(
                            deleteAbmeldungenState = ActionState.Error(aktivitaet, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        deleteAbmeldungenState = ActionState.Idle
                                    )
                                }
                            },
                            showInSheetIfPossible = false
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    val message = "An- und Abmeldungen für ${aktivitaet.event.title} erfolgreich gelöscht."
                    _state.update {
                        it.copy(
                            deleteAbmeldungenState = ActionState.Success(aktivitaet, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SnackbarType.Success,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        deleteAbmeldungenState = ActionState.Idle
                                    )
                                }
                            },
                            showInSheetIfPossible = false
                        )
                    )
                }
            }
        }
    }

    fun sendPushNotification(aktivitaet: GoogleCalendarEventWithAnAbmeldungen) {

        _state.update {
            it.copy(
                sendPushNotificationState = ActionState.Loading(aktivitaet)
            )
        }
        viewModelScope.launch {
            when (val result = service.sendPushNotification(stufe, aktivitaet.event)) {
                is SeesturmResult.Error -> {
                    val message = "Push-Nachricht für ${aktivitaet.event.title} konnten nicht gesendet werden. ${result.error.defaultMessage}"
                    _state.update {
                        it.copy(
                            sendPushNotificationState = ActionState.Error(aktivitaet, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        sendPushNotificationState = ActionState.Idle
                                    )
                                }
                            },
                            showInSheetIfPossible = false
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    val message = "Push-Nachricht für ${aktivitaet.event.title} erfolgreich gesendet."
                    _state.update {
                        it.copy(
                            sendPushNotificationState = ActionState.Success(aktivitaet, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SnackbarType.Success,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        sendPushNotificationState = ActionState.Idle
                                    )
                                }
                            },
                            showInSheetIfPossible = false
                        )
                    )
                }
            }
        }
    }

    fun deleteAllAnAbmeldungen() {

        viewModelScope.launch {
            when (val localState = state.value.anAbmeldungenState) {
                is UiState.Success -> {
                    _state.update {
                        it.copy(
                            deleteAllAbmeldungenState = ActionState.Loading(Unit)
                        )
                    }
                    when (val result = service.deleteAllPastAnAbmeldungen(stufe, localState.data)) {
                        is SeesturmResult.Error -> {
                            val message = "An- und Abmeldungen konnten nicht gelöscht werden. ${result.error.defaultMessage})"
                            _state.update {
                                it.copy(
                                    deleteAllAbmeldungenState = ActionState.Error(Unit, message)
                                )
                            }
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = message,
                                    duration = SnackbarDuration.Long,
                                    type = SnackbarType.Error,
                                    allowManualDismiss = true,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                deleteAllAbmeldungenState = ActionState.Idle
                                            )
                                        }
                                    },
                                    showInSheetIfPossible = false
                                )
                            )
                        }
                        is SeesturmResult.Success -> {
                            val message = "Vergangene An- und Abmeldungen für die ${stufe.stufenName} erfolgreich gelöscht."
                            _state.update {
                                it.copy(
                                    deleteAllAbmeldungenState = ActionState.Success(Unit, message)
                                )
                            }
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = message,
                                    duration = SnackbarDuration.Long,
                                    type = SnackbarType.Success,
                                    allowManualDismiss = true,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                deleteAllAbmeldungenState = ActionState.Idle
                                            )
                                        }
                                    },
                                    showInSheetIfPossible = false
                                )
                            )
                        }
                    }
                }
                else -> {
                    val message = "An- und Abmeldungen konnten nicht gelöscht werden. Die Daten wurden noch nicht geladen."
                    _state.update {
                        it.copy(
                            deleteAllAbmeldungenState = ActionState.Error(Unit, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Long,
                            type = SnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        deleteAllAbmeldungenState = ActionState.Idle
                                    )
                                }
                            },
                            showInSheetIfPossible = false
                        )
                    )
                }
            }
        }
    }

    fun updateSelectedAktivitaetInteraction(interaction: AktivitaetInteraction) {
        _state.update {
            it.copy(
                selectedAktivitaetInteraction = interaction
            )
        }
    }
    fun updateShowDeleteAllAbmeldungenAlert(isVisible: Boolean) {
        _state.update {
            it.copy(
                showDeleteAllAbmeldungenAlert = isVisible
            )
        }
    }
    fun updateShowDeleteAbmeldungenAlert(action: GoogleCalendarEventWithAnAbmeldungen?) {
        _state.update {
            it.copy(
                showDeleteAbmeldungenAlert = action
            )
        }
    }
    fun updateShowSendPushNotificationAlert(action: GoogleCalendarEventWithAnAbmeldungen?) {
        _state.update {
            it.copy(
                showSendPushNotificationAlert = action
            )
        }
    }
    fun updateSelectedDate(year: Int, month: Int, dayOfMonth: Int) {
        _state.update {
            it.copy(
                selectedDate = it.selectedDate.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth)
            )
        }
        reloadAktivitaetenIfNecessary()
    }

    private fun changeRefreshStatus(newStatus: Boolean) {
        _state.update {
            it.copy(
                refreshing = newStatus
            )
        }
    }
}