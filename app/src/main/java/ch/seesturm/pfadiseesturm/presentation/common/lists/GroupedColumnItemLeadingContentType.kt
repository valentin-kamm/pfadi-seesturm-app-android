package ch.seesturm.pfadiseesturm.presentation.common.lists

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN

sealed interface GroupedColumnItemLeadingContentType {
    data object None: GroupedColumnItemLeadingContentType
    data class Icon(
        val imageVector: ImageVector,
        val contentDescription: String? = null,
        val color: @Composable () -> Color = { Color.SEESTURM_GREEN }
    ): GroupedColumnItemLeadingContentType
    data class Custom(
        val content: @Composable () -> Unit
    ): GroupedColumnItemLeadingContentType
}