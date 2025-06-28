package ch.seesturm.pfadiseesturm.main

import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.util.types.SeesturmAppTheme
import ch.seesturm.pfadiseesturm.util.types.SeesturmAuthState
import ch.seesturm.pfadiseesturm.util.state.ActionState

data class AppState(
    val authState: SeesturmAuthState = SeesturmAuthState.SignedOut(state = ActionState.Idle),
    val sheetContent: BottomSheetContent? = null,
    val theme: SeesturmAppTheme = SeesturmAppTheme.System,
    val showAppVersionCheckOverlay: Boolean = false
)