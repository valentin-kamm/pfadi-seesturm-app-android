package ch.seesturm.pfadiseesturm.presentation.common.buttons

import androidx.compose.ui.graphics.Color

sealed interface SeesturmButtonColor {
    data object Predefined: SeesturmButtonColor
    data class Custom(
        val contentColor: Color,
        val buttonColor: Color
    ): SeesturmButtonColor
}