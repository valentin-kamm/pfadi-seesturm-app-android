package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.util.Constants

data class StickyHeaderOffsets(
    val uniqueKey: String,
    val offset: Int,
    val isPinned: Boolean
)

@Composable
fun rememberStickyHeaderOffsets(
    columnState: LazyListState,
    statusBarTopPx: Int
): List<StickyHeaderOffsets> {

    // get the offsets of every single sticky header and add them to a list keyed by their unique key
    return remember {
        derivedStateOf {
            val offsetsList = mutableListOf<StickyHeaderOffsets>()
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
                        StickyHeaderOffsets(
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

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.myStickyHeader(
    uniqueKey: String,
    stickyOffsets: List<StickyHeaderOffsets>,
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
                .zIndex(index.toFloat()),
            contentAlignment = Alignment.CenterStart
        ) {
            content(isPinned)
        }
    }
}

@Composable
fun BasicListHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            title.uppercase(),
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .alpha(0.4f)
        )
    }
}

@Composable
fun BasicListFooter(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            title,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .alpha(0.4f)
        )
    }
}

@Composable
fun BasicLoadingStickHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RedactedText(
            1,
            MaterialTheme.typography.bodyLarge,
            lastLineFraction = 0.4f,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
        )
    }
}

@Preview
@Composable
fun BasicStickyHeaderPreview() {
    BasicListHeader("Pfadijahr 2025")
}

@Preview
@Composable
fun BasicLoadingStickHeaderPreview() {
    BasicLoadingStickHeader()
}