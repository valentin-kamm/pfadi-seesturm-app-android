package ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen

import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState


data class GespeichertePersonenState(
    val readingResult: UiState<List<GespeichertePerson>> = UiState.Loading,
    val isInEditingMode: Boolean = false,
    val vornameState: SeesturmTextFieldState,
    val nachnameState: SeesturmTextFieldState,
    val pfadinameState: SeesturmTextFieldState
) {
    companion object {
        fun create(
            onVornameValueChanged: (String) -> Unit,
            onNachnameValueChanged: (String) -> Unit,
            onPfadinameValueChanged: (String) -> Unit,
        ): GespeichertePersonenState {
            return GespeichertePersonenState(
                vornameState = SeesturmTextFieldState(
                    text = "",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onVornameValueChanged,
                    label = "Vorname"
                ),
                nachnameState = SeesturmTextFieldState(
                    text = "",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onNachnameValueChanged,
                    label = "Nachname"
                ),
                pfadinameState = SeesturmTextFieldState(
                    text = "",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onPfadinameValueChanged,
                    label = "Pfadiname"
                )
            )
        }
    }
}