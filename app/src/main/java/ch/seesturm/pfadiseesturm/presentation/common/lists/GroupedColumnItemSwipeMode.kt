package ch.seesturm.pfadiseesturm.presentation.common.lists

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

sealed interface GroupedColumnItemSwipeMode {
    data object Disabled: GroupedColumnItemSwipeMode
    data class Enabled(
        val isRevealed: Boolean = false,
        val onExpand: (() -> Unit)? = null,
        val onCollapse: (() -> Unit)? = null,
        val actions: @Composable RowScope.() -> Unit,
    ): GroupedColumnItemSwipeMode
}