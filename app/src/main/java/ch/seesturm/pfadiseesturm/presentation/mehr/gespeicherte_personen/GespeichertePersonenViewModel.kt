package ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.domain.data_store.service.GespeichertePersonenService
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarType
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
    private val service: GespeichertePersonenService,
    private val updateSheetContent: (BottomSheetContent?) -> Unit
): ViewModel() {

    // ui state
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

    init {
        startListeningToPersons()
    }

    // computed properties
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

    // function to read the stored persons and update the ui
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

    // functions to modify the stored persons
    fun addPerson() {
        viewModelScope.launch {
            validateNewPerson()
            if (newPersonCanBeSaved) {
                when (val result = service.addPerson(newPerson)) {
                    is SeesturmResult.Error -> {
                        SnackbarController.sendEvent(
                            event = SeesturmSnackbarEvent(
                                message = "Person kann nicht gespeichert werden. ${result.error.defaultMessage}",
                                type = SnackbarType.Error,
                                onDismiss = {},
                                duration = SnackbarDuration.Long,
                                allowManualDismiss = true,
                                showInSheetIfPossible = true
                            )
                        )
                    }
                    is SeesturmResult.Success -> {
                        updateSheetContent(null)
                        SnackbarController.sendEvent(
                            event = SeesturmSnackbarEvent(
                                message = "Person erfolgreich gespeichert.",
                                type = SnackbarType.Success,
                                onDismiss = {},
                                duration = SnackbarDuration.Short,
                                allowManualDismiss = true,
                                showInSheetIfPossible = false
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
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = "Person kann nicht gelöscht werden.",
                            type = SnackbarType.Error,
                            onDismiss = {},
                            duration = SnackbarDuration.Long,
                            allowManualDismiss = true,
                            showInSheetIfPossible = true
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

    // functions to validate the input before saving a new person
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

    // functions to process the text field inputs
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

    // functions to control the swipe to delete actions
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
        when (val localState = state.value.readingResult) {
            is UiState.Success -> {
                _state.update {
                    it.copy(
                        readingResult = localState.copy(
                            data = localState.data.map { listElement ->
                                listElement.copy(swipeActionsRevealed = false) }
                        )
                    )
                }
            }
            else -> {}
        }
    }
    fun disableSwipeActions(id: String) {
        when (val localState = state.value.readingResult) {
            is UiState.Success -> {
                val updatePersonList = localState.data.map { item ->
                    if (item.id == id) {
                        item.copy(
                            swipeActionsRevealed = false
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
            else -> {
                // do nothing
            }
        }
    }
    fun toggleSwipeActionsEnabled(id: String) {
        when (val localState = state.value.readingResult) {
            is UiState.Success -> {
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
            else -> {
                // do nothing
            }
        }
    }
}