package ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications

import ch.seesturm.pfadiseesturm.util.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState

data class PushNachrichtenVerwaltenState (
    val readingState: UiState<Set<SeesturmFCMNotificationTopic>> = UiState.Loading,
    val actionState: ActionState<SeesturmFCMNotificationTopic> = ActionState.Idle,
    var showSettingsAlert: Boolean = false
)