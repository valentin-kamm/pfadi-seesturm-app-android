package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState

data class AktivitaetDetailState(
    val loadingState: UiState<GoogleCalendarEvent?> = UiState.Loading,
    var anAbmeldenState: ActionState<AktivitaetInteractionType> = ActionState.Idle,
    val gespeichertePersonenState: UiState<List<GespeichertePerson>> = UiState.Loading,
    val showSheet: Boolean = false,
    val selectedSheetMode: AktivitaetInteractionType = AktivitaetInteractionType.ABMELDEN,
    val vornameState: SeesturmTextFieldState,
    val nachnameState: SeesturmTextFieldState,
    val pfadinameState: SeesturmTextFieldState,
    val bemerkungState: SeesturmTextFieldState,
    val showCalendarSubscriptionAlert: Boolean = false
) {
    companion object {
        fun create(
            onVornameValueChange: (String) -> Unit,
            onNachnameValueChange: (String) -> Unit,
            onPfadinameValueChange: (String) -> Unit,
            onBemerkungValueChange: (String) -> Unit
        ): AktivitaetDetailState {
            return AktivitaetDetailState(
                vornameState = SeesturmTextFieldState(
                    text = "",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onVornameValueChange,
                    label = "Vorname"
                ),
                nachnameState = SeesturmTextFieldState(
                    text = "",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onNachnameValueChange,
                    label = "Nachname"
                ),
                pfadinameState = SeesturmTextFieldState(
                    text = "",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onPfadinameValueChange,
                    label = "Pfadiname (optional)"
                ),
                bemerkungState = SeesturmTextFieldState(
                    text = "",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onBemerkungValueChange,
                    label = "Bemerkung (optional)"
                )
            )
        }
    }
}