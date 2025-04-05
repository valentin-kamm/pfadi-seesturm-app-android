package ch.seesturm.pfadiseesturm.presentation.main

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.util.SeesturmAuthState
import ch.seesturm.pfadiseesturm.util.state.ActionState
import kotlinx.serialization.Serializable

data class AppState(
    val authState: SeesturmAuthState = SeesturmAuthState.SignedOut(state = ActionState.Idle),
    val sheetContent: BottomSheetContent? = null,
    val theme: SeesturmAppTheme = SeesturmAppTheme.System
)

@Serializable
enum class SeesturmAppTheme {
    Dark,
    Light,
    System;

    val description: String
        get() = when (this) {
            Dark -> "Dunkel"
            Light -> "Hell"
            System -> "System"
        }
}