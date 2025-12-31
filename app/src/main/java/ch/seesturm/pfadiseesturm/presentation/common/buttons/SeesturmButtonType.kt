package ch.seesturm.pfadiseesturm.presentation.common.buttons

import androidx.compose.ui.graphics.Color
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED

sealed interface SeesturmButtonType {
    data object Primary: SeesturmButtonType
    data object Secondary: SeesturmButtonType
    data object Icon: SeesturmButtonType

    val defaultContentColor: Color
        get() = when (this) {
            Primary -> Color.White
            Secondary -> Color.White
            Icon -> Color.White
        }
    val defaultButtonColor: Color
        get() = when (this) {
            Primary -> Color.SEESTURM_RED
            Secondary -> Color.SEESTURM_GREEN
            Icon -> Color.SEESTURM_GREEN
        }
}