package ch.seesturm.pfadiseesturm.presentation.common.buttons

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

sealed class SeesturmButtonIconType {
    data object None: SeesturmButtonIconType()
    data class Custom(
        val image: Painter
    ): SeesturmButtonIconType()
    data class Predefined(
        val icon: ImageVector
    ): SeesturmButtonIconType()
}