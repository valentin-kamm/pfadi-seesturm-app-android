package ch.seesturm.pfadiseesturm.presentation.common.lists

import androidx.compose.runtime.Composable

data class GroupedSectionItemData(
    val header: @Composable (() -> Unit)?,
    val stickyHeader: GroupedColumnStickyHeaderData?,
    val footer: @Composable (() -> Unit)?,
    val content: GroupedColumnSectionScope.() -> Unit
)