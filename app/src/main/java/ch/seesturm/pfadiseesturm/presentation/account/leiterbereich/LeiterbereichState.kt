package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import ch.seesturm.pfadiseesturm.data.firestore.dto.SchoepflialarmDto
import ch.seesturm.pfadiseesturm.data.firestore.dto.SchoepflialarmReactionDto
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.domain.firestore.model.FoodOrder
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState

data class LeiterbereichState(
    val termineState: UiState<List<GoogleCalendarEvent>>,
    val usersResult: UiState<List<FirebaseHitobitoUser>>,
    val foodResult: UiState<List<FoodOrder>>,
    val selectedStufen: UiState<Set<SeesturmStufe>>,
    val showSignOutAlert: Boolean,
    val showDeleteAccountAlert: Boolean,
    val foodItemState: SeesturmTextFieldState,
    val newFoodItemCount: Int,
    val addNewOrderState: ActionState<Unit>,
    val showDeleteAllOrdersAlert: Boolean,
    val deleteAllOrdersState: ActionState<Unit>,
    val schoepflialarmResultDto: UiState<SchoepflialarmDto>,
    val schoepflialarmReactionsResultDto: UiState<List<SchoepflialarmReactionDto>>,
    val sendSchoepflialarmState: ActionState<Unit>,
    val sendSchoepflialarmReactionState: ActionState<SchoepflialarmReactionType>,
    val showNotificationSettingsAlert: Boolean,
    val showLocationSettingsAlert: Boolean,
    val showConfirmSchoepflialarmAlert: Boolean,
    val schoepflialarmMessage: SeesturmTextFieldState,
    val notificationTopicsReadingState: UiState<Set<SeesturmFCMNotificationTopic>>,
    val toggleSchoepflialarmReactionsPushNotificationState: ActionState<SeesturmFCMNotificationTopic>
) {
    companion object {
        fun create(
            onFoodItemValueChanged: (String) -> Unit,
            onSchoepflialarmValueChanged: (String) -> Unit
        ): LeiterbereichState {
            return LeiterbereichState(
                termineState = UiState.Loading,
                usersResult = UiState.Loading,
                foodResult = UiState.Loading,
                selectedStufen = UiState.Loading,
                showSignOutAlert = false,
                showDeleteAccountAlert = false,
                foodItemState = SeesturmTextFieldState(
                    text = "",
                    label = "Bestellung",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onFoodItemValueChanged
                ),
                addNewOrderState = ActionState.Idle,
                newFoodItemCount = 1,
                showDeleteAllOrdersAlert = false,
                deleteAllOrdersState = ActionState.Idle,
                schoepflialarmResultDto = UiState.Loading,
                schoepflialarmReactionsResultDto = UiState.Loading,
                sendSchoepflialarmState = ActionState.Idle,
                sendSchoepflialarmReactionState = ActionState.Idle,
                showNotificationSettingsAlert = false,
                showLocationSettingsAlert = false,
                showConfirmSchoepflialarmAlert = false,
                schoepflialarmMessage = SeesturmTextFieldState(
                    text = "",
                    label = "Schöpflialarm",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onSchoepflialarmValueChanged
                ),
                notificationTopicsReadingState = UiState.Loading,
                toggleSchoepflialarmReactionsPushNotificationState = ActionState.Idle
            )
        }
    }
}
