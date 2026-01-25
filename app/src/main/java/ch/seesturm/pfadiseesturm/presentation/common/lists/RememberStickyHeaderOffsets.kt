package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import ch.seesturm.pfadiseesturm.presentation.common.lists.StickyHeaderOffset
import ch.seesturm.pfadiseesturm.util.Constants

@Composable
fun rememberStickyHeaderOffsets(
    columnState: LazyListState,
    statusBarTopPx: Int
): List<StickyHeaderOffset> {

    // get the offsets of every single sticky header and add them to a list keyed by their unique key
    return remember {
        derivedStateOf {
            val offsetsList = mutableListOf<StickyHeaderOffset>()
            columnState.layoutInfo.visibleItemsInfo.forEach { item ->
                if (item.contentType == Constants.STICKY_HEADER_CONTENT_TYPE) {
                    val offset = if (item.offset > statusBarTopPx) {
                        0
                    }
                    else {
                        statusBarTopPx - item.offset
                    }
                    val isPinned = item.offset <= statusBarTopPx
                    offsetsList.add(
                        StickyHeaderOffset(
                            uniqueKey = item.key.toString(),
                            offset = offset,
                            isPinned = isPinned
                        )
                    )
                }
            }
            offsetsList
        }
    }.value
}

