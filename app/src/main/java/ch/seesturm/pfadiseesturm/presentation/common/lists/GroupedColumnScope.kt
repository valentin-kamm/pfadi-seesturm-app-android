package ch.seesturm.pfadiseesturm.presentation.common.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import ch.seesturm.pfadiseesturm.util.Constants

@GroupedColumnDsl
class GroupedColumnScope(
    private val lazyListScope: LazyListScope,
    private val separatorColor: Color,
    private val separatorWidth: Float,
    private val sectionSpacing: Dp
) {

    private val sections = mutableListOf<GroupedSectionItemData>()

    fun section(
        header: @Composable (() -> Unit)? = null,
        stickyHeader: GroupedColumnStickyHeaderData? = null,
        footer: @Composable (() -> Unit)? = null,
        content: GroupedColumnSectionScope.() -> Unit
    ) {
        sections.add(
            GroupedSectionItemData(
                header = header,
                stickyHeader = stickyHeader,
                footer = footer,
                content = content
            )
        )
    }

    internal fun emit() {
        sections.forEachIndexed { index, sectionData ->

            sectionData.stickyHeader?.let { stickyHeaderData ->
                val props = stickyHeaderData.stickyOffsets.find { it.uniqueKey == stickyHeaderData.uniqueKey }
                val index = stickyHeaderData.stickyOffsets
                    .indexOfFirst { it.uniqueKey == stickyHeaderData.uniqueKey }
                    .takeIf { it != -1 }
                    ?: 0
                val offset = props?.offset ?: 0
                val isPinned = props?.isPinned ?: true
                lazyListScope.stickyHeader(
                    contentType = Constants.STICKY_HEADER_CONTENT_TYPE,
                    key = stickyHeaderData.uniqueKey
                ) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, offset) }
                            .background(Color.Transparent)
                            .fillMaxWidth()
                            .zIndex(index.toFloat() + 100)
                            .animateItem(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        stickyHeaderData.content(lazyListScope, isPinned)
                    }
                }
            }
            sectionData.header?.let { header ->
                lazyListScope.item(
                    key = "sectionheader$index"
                ) {
                    Box(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                    ) {
                        header()
                    }
                }
            }
            val sectionScope = GroupedColumnSectionScope(
                lazyListScope = lazyListScope,
                separatorColor = separatorColor,
                separatorWidth = separatorWidth
            )
            sectionScope.apply(sectionData.content)
            sectionScope.emit()
            sectionData.footer?.let { footer ->
                lazyListScope.item(
                    key = "sectionfooter$index"
                ) {
                    Box(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                    ) {
                        footer()
                    }
                }
            }
            if (index < sections.lastIndex) {
                lazyListScope.item(
                    key = "sectionspacer$index"
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sectionSpacing)
                    )
                }
            }
        }
    }
}
