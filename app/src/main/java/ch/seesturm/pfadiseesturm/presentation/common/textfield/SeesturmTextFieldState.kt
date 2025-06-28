package ch.seesturm.pfadiseesturm.presentation.common.textfield

import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState

data class SeesturmTextFieldState(
    val text: String,
    val label: String,
    val state: SeesturmBinaryUiState<Unit>,
    val onValueChanged: (String) -> Unit,
)
