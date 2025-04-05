package ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen

import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.SeesturmTextFieldState


data class GespeichertePersonenState(
    val readingResult: UiState<List<GespeichertePerson>>,
    val isInEditingMode: Boolean,
    val vornameState: SeesturmTextFieldState,
    val nachnameState: SeesturmTextFieldState,
    val pfadinameState: SeesturmTextFieldState
) {
    companion object {
        fun create(
            initialReadingResult: UiState<List<GespeichertePerson>> = UiState.Loading,
            initialTextFieldText: String = "",
            initialTextFieldState: SeesturmBinaryUiState<Unit> = SeesturmBinaryUiState.Success(Unit),
            initialEditingMode: Boolean = false,
            onVornameValueChanged: (String) -> Unit,
            onNachnameValueChanged: (String) -> Unit,
            onPfadinameValueChanged: (String) -> Unit,
        ): GespeichertePersonenState {
            return GespeichertePersonenState(
                readingResult = initialReadingResult,
                isInEditingMode = initialEditingMode,
                vornameState = SeesturmTextFieldState(
                    text = initialTextFieldText,
                    state = initialTextFieldState,
                    onValueChanged = onVornameValueChanged,
                    label = "Vorname"
                ),
                nachnameState = SeesturmTextFieldState(
                    text = initialTextFieldText,
                    state = initialTextFieldState,
                    onValueChanged = onNachnameValueChanged,
                    label = "Nachname"
                ),
                pfadinameState = SeesturmTextFieldState(
                    text = initialTextFieldText,
                    state = initialTextFieldState,
                    onValueChanged = onPfadinameValueChanged,
                    label = "Pfadiname"
                )
            )
        }
    }
}