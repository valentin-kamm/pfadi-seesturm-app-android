package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import ch.seesturm.pfadiseesturm.presentation.common.lists.StickyHeaderOffset
import ch.seesturm.pfadiseesturm.util.Constants

fun LazyListScope.seesturmStickyHeader(
    uniqueKey: String,
    stickyOffsets: List<StickyHeaderOffset>,
    content: @Composable (LazyItemScope.(Boolean) -> Unit)
) {

    val props = stickyOffsets.find { it.uniqueKey == uniqueKey }
    val index = stickyOffsets
        .indexOfFirst { it.uniqueKey == uniqueKey }
        .takeIf { it != -1 }
        ?: 0
    val offset = props?.offset ?: 0
    val isPinned = props?.isPinned ?: true

    stickyHeader(
        contentType = Constants.STICKY_HEADER_CONTENT_TYPE,
        key = uniqueKey
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(0, offset) }
                .background(Color.Transparent)
                .fillMaxWidth()
                .zIndex(index.toFloat() + 100),
            contentAlignment = Alignment.CenterStart
        ) {
            content(isPinned)
        }
    }
}
