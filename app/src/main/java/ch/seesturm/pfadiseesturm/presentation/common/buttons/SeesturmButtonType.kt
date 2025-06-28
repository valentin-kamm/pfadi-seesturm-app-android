package ch.seesturm.pfadiseesturm.presentation.common.buttons

import androidx.compose.ui.graphics.Color
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED

sealed class SeesturmButtonType {
    data class Primary(
        val buttonColor: Color = Color.SEESTURM_RED,
        val contentColor: Color = Color.White,
        val icon: SeesturmButtonIconType = SeesturmButtonIconType.None
    ): SeesturmButtonType()
    data class Secondary(
        val buttonColor: Color = Color.SEESTURM_GREEN,
        val contentColor: Color = Color.White,
        val icon: SeesturmButtonIconType = SeesturmButtonIconType.None
    ): SeesturmButtonType()
    data class IconButton(
        val buttonColor: Color,
        val contentColor: Color,
        val icon: SeesturmButtonIconType
    ): SeesturmButtonType()
}