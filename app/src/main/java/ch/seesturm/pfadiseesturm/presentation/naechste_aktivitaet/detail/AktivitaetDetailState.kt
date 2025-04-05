package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.util.AktivitaetInteraction
import ch.seesturm.pfadiseesturm.util.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState

data class AktivitaetDetailState(
    val loadingState: UiState<GoogleCalendarEvent?>,
    var anAbmeldenState: ActionState<AktivitaetInteraction>,
    val gespeichertePersonenState: UiState<List<GespeichertePerson>>,
    val showSheet: Boolean,
    val selectedSheetMode: AktivitaetInteraction,
    val vornameState: SeesturmTextFieldState,
    val nachnameState: SeesturmTextFieldState,
    val pfadinameState: SeesturmTextFieldState,
    val bemerkungState: SeesturmTextFieldState,
    val showCalendarSubscriptionAlert: Boolean
) {
    companion object {
        fun create(
            initialLoadingState: UiState<GoogleCalendarEvent?> = UiState.Loading,
            initialAnAbmeldenState: ActionState<AktivitaetInteraction> = ActionState.Idle,
            initialGespeichertePersonenState: UiState<List<GespeichertePerson>> = UiState.Loading,
            initialShowSheet: Boolean = false,
            initialSelectedSheetMode: AktivitaetInteraction = AktivitaetInteraction.ABMELDEN,
            initialTextFieldText: String = "",
            initialTextFieldState: SeesturmBinaryUiState<Unit> = SeesturmBinaryUiState.Success(Unit),
            onVornameValueChange: (String) -> Unit,
            onNachnameValueChange: (String) -> Unit,
            onPfadinameValueChange: (String) -> Unit,
            onBemerkungValueChange: (String) -> Unit,
            initialShowCalendarSubscriptionAlert: Boolean = false
        ): AktivitaetDetailState {
            return AktivitaetDetailState(
                loadingState = initialLoadingState,
                anAbmeldenState = initialAnAbmeldenState,
                showSheet = initialShowSheet,
                selectedSheetMode = initialSelectedSheetMode,
                vornameState = SeesturmTextFieldState(
                    text = initialTextFieldText,
                    state = initialTextFieldState,
                    onValueChanged = onVornameValueChange,
                    label = "Vorname"
                ),
                nachnameState = SeesturmTextFieldState(
                    text = initialTextFieldText,
                    state = initialTextFieldState,
                    onValueChanged = onNachnameValueChange,
                    label = "Nachname"
                ),
                pfadinameState = SeesturmTextFieldState(
                    text = initialTextFieldText,
                    state = initialTextFieldState,
                    onValueChanged = onPfadinameValueChange,
                    label = "Pfadiname (optional)"
                ),
                bemerkungState = SeesturmTextFieldState(
                    text = initialTextFieldText,
                    state = initialTextFieldState,
                    onValueChanged = onBemerkungValueChange,
                    label = "Bemerkung (optional)"
                ),
                showCalendarSubscriptionAlert = initialShowCalendarSubscriptionAlert,
                gespeichertePersonenState = initialGespeichertePersonenState
            )
        }
    }
}