package ch.seesturm.pfadiseesturm.presentation.common.lists

import androidx.compose.runtime.Composable


sealed interface GroupedColumnItemTrailingContentType {

    data object None: GroupedColumnItemTrailingContentType
    data object DisclosureIndicator: GroupedColumnItemTrailingContentType
    data class Custom(
        val content: @Composable () -> Unit
    ): GroupedColumnItemTrailingContentType
}