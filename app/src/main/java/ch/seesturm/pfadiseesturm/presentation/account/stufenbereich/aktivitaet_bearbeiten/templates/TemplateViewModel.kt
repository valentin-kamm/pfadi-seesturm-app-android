package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.account.service.StufenbereichService
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarType
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TemplateViewModel(
    private val stufe: SeesturmStufe,
    private val service: StufenbereichService,
    private val dismissSheet: () -> Unit
): ViewModel() {

    private val _state = MutableStateFlow(TemplateState())
    val state = _state.asStateFlow()

    init {
        observeTemplates()
    }

    private val isTemplatesEmpty: Boolean
        get() = when (val localState = state.value.templatesState) {
            is UiState.Success -> localState.data.isEmpty()
            else -> false
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
                            templatesState = UiState.Error("Vorlagen für die ${stufe.stufenName} konnten nicht geladen werden. ${result.error.defaultMessage}")
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

    fun toggleSwipeActionsEnabled(template: AktivitaetTemplate) {

        val localState = state.value.templatesState

        if (localState !is UiState.Success) {
            return
        }

        val updatedTemplateList = localState.data.map { item ->
            if (item.id == template.id) {
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
                templatesState = localState.copy(
                    data = updatedTemplateList
                )
            )
        }
    }

    private fun resetSwipeActions() {

        val localState = state.value.templatesState

        if (localState !is UiState.Success) {
            return
        }

        _state.update {
            it.copy(
                templatesState = localState.copy(
                    localState.data.map { listElement ->
                        listElement.copy(swipeActionsRevealed = false)
                    }
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

    fun deleteTemplate(template: AktivitaetTemplate) {

        _state.update {
            it.copy(
                deleteState = ActionState.Loading(Unit)
            )
        }
        viewModelScope.launch {
            when (val result = service.deleteAktivitaetTemplate(template.id)) {
                is SeesturmResult.Error -> {
                    val message = "Vorlage für ${stufe.stufenName} konnte nicht gelöscht werden. ${result.error.defaultMessage}"
                    _state.update {
                        it.copy(
                            deleteState = ActionState.Error(Unit, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Short,
                            type = SeesturmSnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        deleteState = ActionState.Idle
                                    )
                                }
                            },
                            showInSheetIfPossible = false
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    if (state.value.isInEditingMode && isTemplatesEmpty) {
                        _state.update {
                            it.copy(
                                isInEditingMode = false
                            )
                        }
                    }
                    val message = "Vorlage für ${stufe.stufenName} erfolgreich gelöscht."
                    _state.update {
                        it.copy(
                            deleteState = ActionState.Success(Unit, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Short,
                            type = SeesturmSnackbarType.Success,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        deleteState = ActionState.Idle
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

    fun insertTemplate(description: String) {

        viewModelScope.launch {

            if (description.isEmpty()) {
                val message = "Die Beschreibung darf nicht leer sein."
                _state.update {
                    it.copy(
                        editState = ActionState.Error(Unit, message)
                    )
                }
                SnackbarController.sendEvent(
                    event = SeesturmSnackbarEvent(
                        message = message,
                        duration = SnackbarDuration.Short,
                        type = SeesturmSnackbarType.Error,
                        allowManualDismiss = true,
                        onDismiss = {
                            _state.update {
                                it.copy(
                                    editState = ActionState.Idle
                                )
                            }
                        },
                        showInSheetIfPossible = true
                    )
                )
                return@launch
            }

            _state.update {
                it.copy(
                    editState = ActionState.Loading(Unit)
                )
            }

            when (val result = service.insertNewAktivitaetTemplate(stufe, description)) {
                is SeesturmResult.Error -> {
                    val message = "Die Vorlage konnte nicht gespeichert werden. ${result.error.defaultMessage}"
                    _state.update {
                        it.copy(
                            editState = ActionState.Error(Unit, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Short,
                            type = SeesturmSnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        editState = ActionState.Idle
                                    )
                                }
                            },
                            showInSheetIfPossible = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    dismissSheet()
                    val message = "Vorlage erfolgreich gespeichert."
                    _state.update {
                        it.copy(
                            editState = ActionState.Success(Unit, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Short,
                            type = SeesturmSnackbarType.Success,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        editState = ActionState.Idle
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

    fun updateTemplate(id: String, description: String) {

        viewModelScope.launch {

            if (description.isEmpty()) {
                val message = "Die Beschreibung darf nicht leer sein."
                _state.update {
                    it.copy(
                        editState = ActionState.Error(Unit, message)
                    )
                }
                SnackbarController.sendEvent(
                    event = SeesturmSnackbarEvent(
                        message = message,
                        duration = SnackbarDuration.Short,
                        type = SeesturmSnackbarType.Error,
                        allowManualDismiss = true,
                        onDismiss = {
                            _state.update {
                                it.copy(
                                    editState = ActionState.Idle
                                )
                            }
                        },
                        showInSheetIfPossible = true
                    )
                )
                return@launch
            }

            _state.update {
                it.copy(
                    editState = ActionState.Loading(Unit)
                )
            }

            when (val result = service.updateAktivitaetTemplate(id, description)) {
                is SeesturmResult.Error -> {
                    val message = "Die Vorlage konnte nicht aktualisiert werden. ${result.error.defaultMessage}"
                    _state.update {
                        it.copy(
                            editState = ActionState.Error(Unit, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Short,
                            type = SeesturmSnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        editState = ActionState.Idle
                                    )
                                }
                            },
                            showInSheetIfPossible = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    dismissSheet()
                    val message = "Vorlage erfolgreich aktualisiert."
                    _state.update {
                        it.copy(
                            editState = ActionState.Success(Unit, message)
                        )
                    }
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = message,
                            duration = SnackbarDuration.Short,
                            type = SeesturmSnackbarType.Success,
                            allowManualDismiss = true,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        editState = ActionState.Idle
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

    fun updateRichTextState(mode: TemplateEditMode) {
        when (mode) {
            is TemplateEditMode.Insert -> {
                _state.update {
                    it.copy(
                        richTextState = it.richTextState.copy(
                            state = RichTextState()
                        )
                    )
                }
            }
            is TemplateEditMode.Update -> {
                _state.update {
                    it.copy(
                        richTextState = it.richTextState.copy(
                            state = RichTextState().setHtml(mode.description)
                        )
                    )
                }
            }
        }
    }
}