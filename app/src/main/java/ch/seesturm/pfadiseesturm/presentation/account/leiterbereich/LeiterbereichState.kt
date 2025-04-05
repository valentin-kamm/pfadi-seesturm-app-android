package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.domain.firestore.model.FoodOrder
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.GespeichertePersonenState
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState

data class LeiterbereichState(
    val termineResult: UiState<List<GoogleCalendarEvent>>,
    val usersResult: UiState<List<FirebaseHitobitoUser>>,
    val foodResult: UiState<List<FoodOrder>>,
    val selectedStufen: UiState<Set<SeesturmStufe>>,
    val showSignOutAlert: Boolean,
    val showDeleteAccountAlert: Boolean,
    val foodItemState: SeesturmTextFieldState,
    val newFoodItemCount: Int,
    val addNewOrderState: ActionState<Unit>,
    val showDeleteAllOrdersAlert: Boolean,
    val deleteAllOrdersState: ActionState<Unit>
) {
    companion object {
        fun create(
            initialTermineResult: UiState<List<GoogleCalendarEvent>> = UiState.Loading,
            initialUsersResult: UiState<List<FirebaseHitobitoUser>> = UiState.Loading,
            initialFoodResult: UiState<List<FoodOrder>> = UiState.Loading,
            initialSelectedStufen: UiState<Set<SeesturmStufe>> = UiState.Loading,
            initialAlert: Boolean = false,
            initialTextFieldText: String = "",
            initialTextFieldState: SeesturmBinaryUiState<Unit> = SeesturmBinaryUiState.Success(Unit),
            onFoodItemValueChanged: (String) -> Unit,
            initialAddNewOrderState: ActionState<Unit> = ActionState.Idle,
            initialNewFoodItemCount: Int = 1,
            initialDeleteAllOrdersState: ActionState<Unit> = ActionState.Idle
        ): LeiterbereichState {
            return LeiterbereichState(
                termineResult = initialTermineResult,
                usersResult = initialUsersResult,
                foodResult = initialFoodResult,
                selectedStufen = initialSelectedStufen,
                showSignOutAlert = initialAlert,
                showDeleteAccountAlert = initialAlert,
                foodItemState = SeesturmTextFieldState(
                    text = initialTextFieldText,
                    label = "Bestellung",
                    state = initialTextFieldState,
                    onValueChanged = onFoodItemValueChanged
                ),
                addNewOrderState = initialAddNewOrderState,
                newFoodItemCount = initialNewFoodItemCount,
                showDeleteAllOrdersAlert = initialAlert,
                deleteAllOrdersState = initialDeleteAllOrdersState
            )
        }
    }
}
