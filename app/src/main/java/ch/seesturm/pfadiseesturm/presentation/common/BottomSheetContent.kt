package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle

private typealias snackbarHost = @Composable () -> Unit

sealed class BottomSheetContent {
    data class Scaffold(
        val title: String?,
        val content: @Composable () -> Unit,
        val sheetHeightPercentage: Float = 0.95f,
        val actions: (@Composable RowScope.() -> Unit)? = null
    ): BottomSheetContent()
    data class Custom(
        val content: @Composable (snackbarHost) -> Unit
    ): BottomSheetContent()
}