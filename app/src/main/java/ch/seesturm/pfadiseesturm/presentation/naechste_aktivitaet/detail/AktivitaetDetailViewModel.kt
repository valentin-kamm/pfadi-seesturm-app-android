package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.data.firestore.dto.AktivitaetAnAbmeldungDto
import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.domain.data_store.service.GespeichertePersonenService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.NaechsteAktivitaetService
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarType
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AktivitaetDetailViewModel(
    private val service: NaechsteAktivitaetService,
    private val gespeichertePersonenService: GespeichertePersonenService,
    private val stufe: SeesturmStufe,
    private val type: AktivitaetDetailViewLocation,
    private val dismissAnAbmeldenSheet: () -> Unit,
    private val userId: String?
): ViewModel() {

    private val _state = MutableStateFlow(
        AktivitaetDetailState.create(
            onVornameValueChange = { newValue ->
                updateVorname(newValue)
            },
            onNachnameValueChange = { newValue ->
                updateNachname(newValue)
            },
            onPfadinameValueChange = { newValue ->
                updatePfadiname(newValue)
            },
            onBemerkungValueChange = { newValue ->
                updateBemerkung(newValue)
            }
        )
    )
    val state = _state.asStateFlow()

    private val newAnAbmeldung: AktivitaetAnAbmeldungDto
        get() = AktivitaetAnAbmeldungDto(
            eventId = type.eventId ?: "",
            uid = userId,
            vorname = state.value.vornameState.text.trim(),
            nachname = state.value.nachnameState.text.trim(),
            pfadiname = state.value.pfadinameState.text.trim().ifEmpty { null },
            bemerkung = state.value.bemerkungState.text.trim().ifEmpty { null },
            typeId = state.value.selectedSheetMode.id,
            stufenId = stufe.id
        )
    private val isNewAnAbmeldungVornameOk: Boolean
        get() = newAnAbmeldung.vorname.isNotEmpty()
    private val isNewAnAbmeldungNachnameOk: Boolean
        get() = newAnAbmeldung.nachname.isNotEmpty()
    private val isNewAnAbmeldungOk: Boolean
        get() = isNewAnAbmeldungVornameOk && isNewAnAbmeldungNachnameOk

    init {
        startListeningToPersons()
        getAktivitaet()
    }

    fun getAktivitaet() {

        if (type.eventId == null || type.eventId?.isEmpty() == true) {
            _state.update {
                it.copy(
                    loadingState = UiState.Success(null)
                )
            }
            return
        }

        _state.update {
            it.copy(
                loadingState = UiState.Loading
            )
        }

        viewModelScope.launch {
            when(val result = type.getAktivitaet()) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            loadingState = UiState.Error("Die ${stufe.aktivitaetDescription} konnte nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            loadingState = UiState.Success(result.data)
                        )
                    }
                }
            }
        }
    }

    fun sendAnAbmeldung() {

        validateAnAbmeldung()

        if (isNewAnAbmeldungOk) {

            viewModelScope.launch {
                _state.update {
                    it.copy(
                        anAbmeldenState = ActionState.Loading(state.value.selectedSheetMode)
                    )
                }

                when (val result = service.sendAnAbmeldung(newAnAbmeldung)) {
                    is SeesturmResult.Error -> {
                        _state.update {
                            it.copy(
                                anAbmeldenState = ActionState.Error(action = state.value.selectedSheetMode, "${state.value.selectedSheetMode.nomen} konnte nicht gespeichert werden. ${result.error.defaultMessage}")
                            )
                        }
                        SnackbarController.sendEvent(
                            event = SeesturmSnackbarEvent(
                                message = "${state.value.selectedSheetMode.nomen} konnte nicht gespeichert werden. ${result.error.defaultMessage}",
                                type = SeesturmSnackbarType.Error,
                                onDismiss = {
                                    _state.update {
                                        it.copy(
                                            anAbmeldenState = ActionState.Idle
                                        )
                                    }
                                },
                                duration = SnackbarDuration.Long,
                                allowManualDismiss = true,
                                showInSheetIfPossible = true
                            )
                        )
                    }
                    is SeesturmResult.Success -> {
                        dismissAnAbmeldenSheet()
                        _state.update {
                            it.copy(
                                anAbmeldenState = ActionState.Success(action = state.value.selectedSheetMode, "${state.value.selectedSheetMode.nomen} erfolgreich gespeichert.")
                            )
                        }
                        SnackbarController.sendEvent(
                            event = SeesturmSnackbarEvent(
                                message = "${state.value.selectedSheetMode.nomen} erfolgreich gespeichert.",
                                type = SeesturmSnackbarType.Success,
                                onDismiss = {
                                    _state.update {
                                        it.copy(
                                            anAbmeldenState = ActionState.Idle
                                        )
                                    }
                                },
                                duration = SnackbarDuration.Short,
                                allowManualDismiss = true,
                                showInSheetIfPossible = false
                            )
                        )
                        updateVorname("")
                        updateNachname("")
                        updatePfadiname("")
                        updateBemerkung("")
                    }
                }
            }
        }
    }
    private fun validateAnAbmeldung() {
        if (!isNewAnAbmeldungVornameOk) {
            _state.update {
                it.copy(
                    vornameState = _state.value.vornameState.copy(
                        state = SeesturmBinaryUiState.Error("Der Vorname ist erforderlich")
                    )
                )
            }
        }
        if (!isNewAnAbmeldungNachnameOk) {
            _state.update {
                it.copy(
                    nachnameState = _state.value.nachnameState.copy(
                        state = SeesturmBinaryUiState.Error("Der Nachname ist erforderlich")
                    )
                )
            }
        }
    }

    private fun startListeningToPersons() {

        _state.update {
            it.copy(
                gespeichertePersonenState = UiState.Loading
            )
        }
        gespeichertePersonenService.readPersons().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            gespeichertePersonenState = UiState.Error(result.error.defaultMessage)
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            gespeichertePersonenState = UiState.Success(result.data)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateVorname(newValue: String) {
        _state.update {
            it.copy(
                vornameState = state.value.vornameState.copy(
                    text = newValue,
                    state = SeesturmBinaryUiState.Success(Unit)
                )
            )
        }
    }
    private fun updateNachname(newValue: String) {
        _state.update {
            it.copy(
                nachnameState = state.value.nachnameState.copy(
                    text = newValue,
                    state = SeesturmBinaryUiState.Success(Unit)
                )
            )
        }
    }
    private fun updatePfadiname(newValue: String) {
        _state.update {
            it.copy(
                pfadinameState = state.value.pfadinameState.copy(
                    text = newValue,
                    state = SeesturmBinaryUiState.Success(Unit)
                )
            )
        }
    }
    private fun updateBemerkung(newValue: String) {
        _state.update {
            it.copy(
                bemerkungState = state.value.bemerkungState.copy(
                    text = newValue,
                    state = SeesturmBinaryUiState.Success(Unit)
                )
            )
        }
    }

    fun changeSheetMode(interaction: AktivitaetInteractionType) {
        _state.update {
            it.copy(
                selectedSheetMode = interaction
            )
        }
    }

    fun setGespeichertePerson(person: GespeichertePerson) {
        updateVorname(person.vorname)
        updateNachname(person.nachname)
        updatePfadiname(person.pfadiname ?: "")
    }
}