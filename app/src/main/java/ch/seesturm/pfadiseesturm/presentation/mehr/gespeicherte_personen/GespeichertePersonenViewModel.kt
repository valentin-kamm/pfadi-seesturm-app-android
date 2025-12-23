package ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.domain.data_store.service.GespeichertePersonenService
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class GespeichertePersonenViewModel(
    private val service: GespeichertePersonenService
): ViewModel() {

    private val _state = MutableStateFlow(
        GespeichertePersonenState.create(
            onVornameValueChanged = { newValue ->
                updateVorname(newValue)
            },
            onNachnameValueChanged = { newValue ->
                updateNachname(newValue)
            },
            onPfadinameValueChanged = { newValue ->
                updatePfadiname(newValue)
            }
        )
    )
    val state = _state.asStateFlow()

    val showSheet = mutableStateOf(false)

    init {
        startListeningToPersons()
    }

    private val newPerson: GespeichertePerson
        get() = GespeichertePerson(
            id = UUID.randomUUID().toString(),
            vorname = state.value.vornameState.text.trim(),
            nachname = state.value.nachnameState.text.trim(),
            pfadiname = state.value.pfadinameState.text.trim().ifEmpty { null },
            swipeActionsRevealed = false
        )
    private val newPersonCanBeSaved: Boolean
        get() = state.value.vornameState.state.isSuccess && state.value.nachnameState.state.isSuccess
    private val isPersonenEmpty: Boolean
        get() = when (val localState = state.value.readingResult) {
            is UiState.Success -> {
                localState.data.isEmpty()
            }
            else -> { false }
        }

    private fun startListeningToPersons() {

        _state.update {
            it.copy(
                readingResult = UiState.Loading
            )
        }
        service.readPersons().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            readingResult = UiState.Error(result.error.defaultMessage)
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            readingResult = UiState.Success(result.data)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun insertPerson() {

        validateNewPerson()

        viewModelScope.launch {
            if (newPersonCanBeSaved) {
                when (val result = service.insertPerson(newPerson)) {
                    is SeesturmResult.Error -> {
                        SnackbarController.showSnackbar(
                            snackbar = SeesturmSnackbar.Error(
                                message = "Person kann nicht gespeichert werden. ${result.error.defaultMessage}",
                                onDismiss = {},
                                location = SeesturmSnackbarLocation.Sheet,
                                allowManualDismiss = true
                            )
                        )
                    }
                    is SeesturmResult.Success -> {
                        showSheet.value = false
                        SnackbarController.showSnackbar(
                            snackbar = SeesturmSnackbar.Success(
                                message = "Person erfolgreich gespeichert.",
                                onDismiss = {},
                                location = SeesturmSnackbarLocation.Default,
                                allowManualDismiss = true
                            )
                        )
                        updateVorname("")
                        updateNachname("")
                        updatePfadiname("")
                    }
                }
            }
        }
    }
    fun deletePerson(id: String) {
        viewModelScope.launch {
            when (service.deletePerson(id)) {
                is SeesturmResult.Error -> {
                    SnackbarController.showSnackbar(
                        snackbar = SeesturmSnackbar.Error(
                            message = "Person kann nicht gelöscht werden.",
                            onDismiss = {},
                            location = SeesturmSnackbarLocation.Default,
                            allowManualDismiss = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    if (state.value.isInEditingMode && isPersonenEmpty) {
                        _state.update {
                            it.copy(
                                isInEditingMode = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun validateNewPerson() {
        if (newPerson.vorname.isEmpty()) {
            _state.update {
                it.copy(
                    vornameState = _state.value.vornameState.copy(
                        state = SeesturmBinaryUiState.Error("Der Vorname ist erforderlich")
                    )
                )
            }
        }
        if (newPerson.nachname.isEmpty()) {
            _state.update {
                it.copy(
                    nachnameState = _state.value.nachnameState.copy(
                        state = SeesturmBinaryUiState.Error("Der Nachname ist erforderlich")
                    )
                )
            }
        }
    }

    private fun updateVorname(newVorname: String) {
        _state.update {
            it.copy(
                vornameState = state.value.vornameState.copy(
                    text = newVorname,
                    state = SeesturmBinaryUiState.Success(Unit)
                )
            )
        }
    }
    private fun updateNachname(newNachname: String) {
        _state.update {
            it.copy(
                nachnameState = state.value.nachnameState.copy(
                    text = newNachname,
                    state = SeesturmBinaryUiState.Success(Unit)
                )
            )
        }
    }
    private fun updatePfadiname(newPfadiname: String) {
        _state.update {
            it.copy(
                pfadinameState = state.value.pfadinameState.copy(
                    text = newPfadiname
                )
            )
        }
    }

    fun toggleEditingMode() {
        val currentIsInEditingMode = state.value.isInEditingMode
        if (currentIsInEditingMode) {
            resetSwipeActions()
        }
        _state.update {
            it.copy(
                isInEditingMode = !it.isInEditingMode
            )
        }
    }
    private fun resetSwipeActions() {

        val localState = state.value.readingResult

        if (localState is UiState.Success) {
            _state.update {
                it.copy(
                    readingResult = localState.copy(
                        data = localState.data.map { listElement ->
                            listElement.copy(swipeActionsRevealed = false) }
                    )
                )
            }
        }
    }
    fun toggleSwipeActions(id: String) {

        val localState = state.value.readingResult

        if (localState is UiState.Success) {
            val updatePersonList = localState.data.map { item ->
                if (item.id == id) {
                    item.copy(
                        swipeActionsRevealed = !item.swipeActionsRevealed
                    )
                }
                else {
                    item
                }
            }
            _state.update {
                it.copy(
                    readingResult = localState.copy(
                        data = updatePersonList
                    )
                )
            }
        }
    }
}