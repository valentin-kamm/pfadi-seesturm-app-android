package ch.seesturm.pfadiseesturm.presentation.common.lists

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import ch.seesturm.pfadiseesturm.presentation.common.lists.StickyHeaderOffset

data class GroupedColumnStickyHeaderData(
    val uniqueKey: String,
    val stickyOffsets: List<StickyHeaderOffset>,
    val content: @Composable LazyListScope.(Boolean) -> Unit
)