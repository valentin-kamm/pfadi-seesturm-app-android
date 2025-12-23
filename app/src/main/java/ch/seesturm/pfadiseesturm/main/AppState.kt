package ch.seesturm.pfadiseesturm.main

import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.types.SeesturmAppTheme
import ch.seesturm.pfadiseesturm.util.types.SeesturmAuthState

data class AppState(
    val authState: SeesturmAuthState = SeesturmAuthState.SignedOut(state = ActionState.Idle),
    val theme: SeesturmAppTheme = SeesturmAppTheme.System,
    val showAppVersionCheckOverlay: Boolean = false,
    val allowedOrientation: AllowedOrientation = AllowedOrientation.PortraitOnly
)

sealed class AllowedOrientation {
    data object PortraitOnly: AllowedOrientation()
    data object All: AllowedOrientation()
}