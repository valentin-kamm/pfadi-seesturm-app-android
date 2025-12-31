package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.account.service.StufenbereichService
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.domain.wordpress.model.toAktivitaetWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class StufenbereichViewModel(
    private val stufe: SeesturmStufe,
    private val service: StufenbereichService
): ViewModel() {

    private val _state = MutableStateFlow(StufenbereichState())
    val state = _state.asStateFlow()

    init {
        loadData(false)
    }

    val abmeldungenState: UiState<List<GoogleCalendarEventWithAnAbmeldungen>>
        get() {
            return when (val localAktivitaetenState = state.value.aktivitaetenState) {
                UiState.Loading -> UiState.Loading
                is UiState.Error -> UiState.Error(message = localAktivitaetenState.message)
                is UiState.Success -> {

                    when (val localAnAbmeldungenState = state.value.anAbmeldungenState) {
                        UiState.Loading -> UiState.Loading
                        is UiState.Error -> UiState.Error(message = localAnAbmeldungenState.message)
                        is UiState.Success -> {
                            val combined = localAktivitaetenState.data.map { it.toAktivitaetWithAnAbmeldungen(anAbmeldungen = localAnAbmeldungenState.data) }
                            UiState.Success(combined)
                        }
                    }
                }
            }
        }

    private val mustReloadData: Boolean
        get() {

            val localState = state.value.aktivitaetenState

            if (localState !is UiState.Success) {
                // data is still loading, do not reload
                return false
            }

            // get all end dates and oldest end date
            val endDates = localState.data.map { it.end }
            val oldestEndDate = endDates.minOrNull()

            return if (oldestEndDate != null && endDates.isNotEmpty()) {
                // if the oldest end date is after the selected date, reload the data
                state.value.selectedDate < oldestEndDate
            }
            else {
                // array is empty, always reload since we have no end dates to compare against
                true
            }
        }

    fun isEditButtonLoading(aktivitaet: GoogleCalendarEventWithAnAbmeldungen): Boolean {

        val localDeleteState = state.value.deleteAbmeldungenState
        val localPushNotificationState = state.value.sendPushNotificationState

        return (localDeleteState is ActionState.Loading && localDeleteState.action == aktivitaet) ||
            (localPushNotificationState is ActionState.Loading && localPushNotificationState.action == aktivitaet)
    }

    fun loadData(isPullToRefresh: Boolean) {

        if (isPullToRefresh) {
            changeRefreshStatus(true)
        }

        viewModelScope.launch {

            getAktivitaeten(isPullToRefresh)

            val localAktivitaetenState = state.value.aktivitaetenState

            if (localAktivitaetenState is UiState.Success) {
                getAnAbmeldungen(localAktivitaetenState.data, isPullToRefresh)
            }
        }.invokeOnCompletion {
            if (isPullToRefresh) {
                changeRefreshStatus(false)
            }
        }
    }

    private fun reloadDataIfNecessary() {
        if (mustReloadData) {
            loadData(false)
        }
    }

    fun refresh() {
        loadData(true)
    }

    private suspend fun getAktivitaeten(isPullToRefresh: Boolean) {

        if (!isPullToRefresh) {
            _state.update {
                it.copy(
                    aktivitaetenState = UiState.Loading
                )
            }
        }

        when (val result = service.fetchEvents(stufe, state.value.selectedDate)) {
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
    }

    private suspend fun getAnAbmeldungen(aktivitaeten: List<GoogleCalendarEvent>, isPullToRefresh: Boolean) {

        if (!isPullToRefresh) {
            _state.update {
                it.copy(
                    anAbmeldungenState = UiState.Loading
                )
            }
        }

        when (val result = service.fetchAnAbmeldungen(aktivitaeten, stufe)) {
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
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Error(
                            message = message,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        deleteAbmeldungenState = ActionState.Idle
                                    )
                                }
                            },
                            location = SeesturmSnackbarLocation.Default,
                            allowManualDismiss = true
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
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Success(
                            message = message,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        deleteAbmeldungenState = ActionState.Idle
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
                    val message = "Push-Nachricht für ${aktivitaet.event.title} konnte nicht gesendet werden. ${result.error.defaultMessage}"
                    _state.update {
                        it.copy(
                            sendPushNotificationState = ActionState.Error(aktivitaet, message)
                        )
                    }
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Error(
                            message = message,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        sendPushNotificationState = ActionState.Idle
                                    )
                                }
                            },
                            location = SeesturmSnackbarLocation.Default,
                            allowManualDismiss = true
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
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Success(
                            message = message,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        sendPushNotificationState = ActionState.Idle
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
    }

    fun deleteAllAnAbmeldungen() {

        val localState = state.value.anAbmeldungenState

        if (localState !is UiState.Success) {
            return
        }

        _state.update {
            it.copy(
                deleteAllAbmeldungenState = ActionState.Loading(Unit)
            )
        }

        viewModelScope.launch {
            when (val result = service.deleteAllPastAnAbmeldungen(stufe, localState.data)) {
                is SeesturmResult.Error -> {
                    val message = "An- und Abmeldungen konnten nicht gelöscht werden. ${result.error.defaultMessage})"
                    _state.update {
                        it.copy(
                            deleteAllAbmeldungenState = ActionState.Error(Unit, message)
                        )
                    }
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Error(
                            message = message,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        deleteAllAbmeldungenState = ActionState.Idle
                                    )
                                }
                            },
                            location = SeesturmSnackbarLocation.Default,
                            allowManualDismiss = true
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
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Success(
                            message = message,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        deleteAllAbmeldungenState = ActionState.Idle
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
        reloadDataIfNecessary()
    }
    private fun changeRefreshStatus(newStatus: Boolean) {
        _state.update {
            it.copy(
                refreshing = newStatus
            )
        }
    }
}