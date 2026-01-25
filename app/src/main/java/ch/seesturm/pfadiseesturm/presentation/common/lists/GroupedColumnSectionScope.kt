package ch.seesturm.pfadiseesturm.presentation.common.lists

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@GroupedColumnDsl
class GroupedColumnSectionScope(
    private val lazyListScope: LazyListScope,
    private val separatorColor: Color,
    private val separatorWidth: Float
) {

    private val items = mutableListOf<GroupedColumnItemData>()

    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        modifier: Modifier = Modifier,
        overlineContent: @Composable (() -> Unit)? = null,
        supportingContent: @Composable (() -> Unit)? = null,
        leadingContent: GroupedColumnItemLeadingContentType = GroupedColumnItemLeadingContentType.None,
        trailingContent: GroupedColumnItemTrailingContentType = GroupedColumnItemTrailingContentType.None,
        tonalElevation: Dp = ListItemDefaults.Elevation,
        shadowElevation: Dp = ListItemDefaults.Elevation,
        onClick: ((index: Int) -> Unit)? = null,
        disableRoundedCorners: Boolean = false,
        swipeMode: (index: Int) -> GroupedColumnItemSwipeMode = { GroupedColumnItemSwipeMode.Disabled },
        padding: (index: Int) -> PaddingValues = { PaddingValues(0.dp) },
        headlineContent: @Composable ((index: Int) -> Unit)
    ) {
        repeat(count) { index ->
            items += internalItem(
                key = key?.let { it(index) },
                modifier = modifier,
                overlineContent = overlineContent,
                supportingContent = supportingContent,
                leadingContent = leadingContent,
                trailingContent = trailingContent,
                tonalElevation = tonalElevation,
                shadowElevation = shadowElevation,
                onClick = onClick?.let { { it(index) } },
                disableRoundedCorners = disableRoundedCorners,
                swipeMode = swipeMode(index),
                padding = padding(index),
                headlineContent = {
                    headlineContent(index)
                }
            )
        }
    }

    fun <T> items(
        items: List<T>,
        key: ((item: T) -> Any)? = null,
        modifier: Modifier = Modifier,
        overlineContent: @Composable (() -> Unit)? = null,
        supportingContent: @Composable (() -> Unit)? = null,
        leadingContent: GroupedColumnItemLeadingContentType = GroupedColumnItemLeadingContentType.None,
        trailingContent: GroupedColumnItemTrailingContentType = GroupedColumnItemTrailingContentType.None,
        tonalElevation: Dp = ListItemDefaults.Elevation,
        shadowElevation: Dp = ListItemDefaults.Elevation,
        onClick: ((item: T) -> Unit)? = null,
        disableRoundedCorners: Boolean = false,
        swipeMode: (item: T) -> GroupedColumnItemSwipeMode = { GroupedColumnItemSwipeMode.Disabled },
        padding: (item: T) -> PaddingValues = { PaddingValues(0.dp) },
        headlineContent: @Composable ((item: T) -> Unit)
    ) {
        items.forEach { i ->
            this.items += internalItem(
                key = key?.let { it(i) },
                modifier = modifier,
                overlineContent = overlineContent,
                supportingContent = supportingContent,
                leadingContent = leadingContent,
                trailingContent = trailingContent,
                tonalElevation = tonalElevation,
                shadowElevation = shadowElevation,
                onClick = onClick?.let { { it(i) } },
                disableRoundedCorners = disableRoundedCorners,
                swipeMode = swipeMode(i),
                padding = padding(i),
                headlineContent = {
                    headlineContent(i)
                }
            )
        }
    }

    fun <T> itemsIndexed(
        items: List<T>,
        key: ((index: Int, item: T) -> Any)? = null,
        modifier: Modifier = Modifier,
        overlineContent: @Composable (() -> Unit)? = null,
        supportingContent: @Composable (() -> Unit)? = null,
        leadingContent: GroupedColumnItemLeadingContentType = GroupedColumnItemLeadingContentType.None,
        trailingContent: GroupedColumnItemTrailingContentType = GroupedColumnItemTrailingContentType.None,
        tonalElevation: Dp = ListItemDefaults.Elevation,
        shadowElevation: Dp = ListItemDefaults.Elevation,
        onClick: ((index: Int, item: T) -> Unit)? = null,
        disableRoundedCorners: Boolean = false,
        swipeMode: (index: Int, item: T) -> GroupedColumnItemSwipeMode = { _, _ -> GroupedColumnItemSwipeMode.Disabled },
        padding: (index: Int, item: T) -> PaddingValues = { _, _ -> PaddingValues(0.dp) },
        headlineContent: @Composable ((index: Int, item: T) -> Unit)
    ) {
        items.forEachIndexed { index, i ->
            this.items += internalItem(
                key = key?.let { it(index, i) },
                modifier = modifier,
                overlineContent = overlineContent,
                supportingContent = supportingContent,
                leadingContent = leadingContent,
                trailingContent = trailingContent,
                tonalElevation = tonalElevation,
                shadowElevation = shadowElevation,
                onClick = onClick?.let { { it(index, i) } },
                disableRoundedCorners = disableRoundedCorners,
                swipeMode = swipeMode(index, i),
                padding = padding(index, i),
                headlineContent = {
                    headlineContent(index, i)
                }
            )
        }
    }

    fun item(
        key: Any? = null,
        modifier: Modifier = Modifier,
        overlineContent: @Composable (() -> Unit)? = null,
        supportingContent: @Composable (() -> Unit)? = null,
        leadingContent: GroupedColumnItemLeadingContentType = GroupedColumnItemLeadingContentType.None,
        trailingContent: GroupedColumnItemTrailingContentType = GroupedColumnItemTrailingContentType.None,
        tonalElevation: Dp = ListItemDefaults.Elevation,
        shadowElevation: Dp = ListItemDefaults.Elevation,
        onClick: (() -> Unit)? = null,
        disableRoundedCorners: Boolean = false,
        swipeMode: GroupedColumnItemSwipeMode = GroupedColumnItemSwipeMode.Disabled,
        padding: PaddingValues = PaddingValues(0.dp),
        headlineContent: @Composable (() -> Unit)
    ) {
        items += internalItem(
            key = key,
            modifier = modifier,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation,
            onClick = onClick,
            disableRoundedCorners = disableRoundedCorners,
            swipeMode = swipeMode,
            padding = padding,
            headlineContent = headlineContent
        )
    }

    fun <T> textItemsIndexed(
        items: List<T>,
        key: ((index: Int, item: T) -> Any)? = null,
        modifier: Modifier = Modifier,
        overlineContent: @Composable (() -> Unit)? = null,
        supportingContent: @Composable (() -> Unit)? = null,
        leadingContent: GroupedColumnItemLeadingContentType = GroupedColumnItemLeadingContentType.None,
        trailingContent: GroupedColumnItemTrailingContentType = GroupedColumnItemTrailingContentType.None,
        textColor: (@Composable () -> Color)? = null,
        tonalElevation: Dp = ListItemDefaults.Elevation,
        shadowElevation: Dp = ListItemDefaults.Elevation,
        onClick: ((index: Int, item: T) -> Unit)? = null,
        disableRoundedCorners: Boolean = false,
        isLoading: Boolean = false,
        swipeMode: (index: Int, item: T) -> GroupedColumnItemSwipeMode = { _, _ -> GroupedColumnItemSwipeMode.Disabled },
        textStyle: (@Composable () -> TextStyle)? = null,
        padding: (index: Int, item: T) -> PaddingValues = { _, _ -> PaddingValues(0.dp) },
        text: (index: Int, item: T) -> String
    ) {
        items.forEachIndexed { index, i ->
            this.items += internalItem(
                key = key?.let { it(index, i) },
                modifier = modifier,
                overlineContent = overlineContent,
                supportingContent = supportingContent,
                leadingContent = leadingContent,
                trailingContent = trailingContent,
                textColor = textColor,
                tonalElevation = tonalElevation,
                shadowElevation = shadowElevation,
                onClick = onClick?.let { { it(index, i) } },
                disableRoundedCorners = disableRoundedCorners,
                swipeMode = swipeMode(index, i),
                padding = padding(index, i),
                headlineContent = {
                    if (isLoading) {
                        RedactedText(
                            numberOfLines = 1,
                            textStyle = textStyle?.invoke() ?: LocalTextStyle.current,
                            lastLineFraction = Random.nextFloat() * (0.8f - 0.4f) + 0.4f
                        )
                    }
                    else {
                        Text(
                            text = text(index, i),
                            style = textStyle?.invoke() ?: LocalTextStyle.current,
                        )
                    }
                }
            )
        }
    }

    fun <T> textItems(
        items: List<T>,
        key: ((item: T) -> Any)? = null,
        modifier: Modifier = Modifier,
        overlineContent: @Composable (() -> Unit)? = null,
        supportingContent: @Composable (() -> Unit)? = null,
        leadingContent: GroupedColumnItemLeadingContentType = GroupedColumnItemLeadingContentType.None,
        trailingContent: GroupedColumnItemTrailingContentType = GroupedColumnItemTrailingContentType.None,
        textColor: (@Composable () -> Color)? = null,
        tonalElevation: Dp = ListItemDefaults.Elevation,
        shadowElevation: Dp = ListItemDefaults.Elevation,
        onClick: ((item: T) -> Unit)? = null,
        disableRoundedCorners: Boolean = false,
        isLoading: Boolean = false,
        swipeMode: (item: T) -> GroupedColumnItemSwipeMode = { GroupedColumnItemSwipeMode.Disabled },
        textStyle: (@Composable () -> TextStyle)? = null,
        padding: (item: T) -> PaddingValues = { PaddingValues(0.dp) },
        text: (item: T) -> String
    ) {
        items.forEach { i ->
            this.items += internalItem(
                key = key?.let { it(i) },
                modifier = modifier,
                overlineContent = overlineContent,
                supportingContent = supportingContent,
                leadingContent = leadingContent,
                trailingContent = trailingContent,
                textColor = textColor,
                tonalElevation = tonalElevation,
                shadowElevation = shadowElevation,
                onClick = onClick?.let { { it(i) } },
                disableRoundedCorners = disableRoundedCorners,
                swipeMode = swipeMode(i),
                padding = padding(i),
                headlineContent = {
                    if (isLoading) {
                        RedactedText(
                            numberOfLines = 1,
                            textStyle = textStyle?.invoke() ?: LocalTextStyle.current,
                            lastLineFraction = Random.nextFloat() * (0.8f - 0.4f) + 0.4f
                        )
                    }
                    else {
                        Text(
                            text = text(i),
                            style = textStyle?.invoke() ?: LocalTextStyle.current,
                        )
                    }
                }
            )
        }
    }

    fun textItems(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        modifier: Modifier = Modifier,
        overlineContent: @Composable (() -> Unit)? = null,
        supportingContent: @Composable (() -> Unit)? = null,
        leadingContent: GroupedColumnItemLeadingContentType = GroupedColumnItemLeadingContentType.None,
        trailingContent: GroupedColumnItemTrailingContentType = GroupedColumnItemTrailingContentType.None,
        textColor: (@Composable () -> Color)? = null,
        tonalElevation: Dp = ListItemDefaults.Elevation,
        shadowElevation: Dp = ListItemDefaults.Elevation,
        onClick: ((index: Int) -> Unit)? = null,
        disableRoundedCorners: Boolean = false,
        isLoading: Boolean = false,
        swipeMode: (index: Int) -> GroupedColumnItemSwipeMode = { GroupedColumnItemSwipeMode.Disabled },
        textStyle: (@Composable () -> TextStyle)? = null,
        padding: (index: Int) -> PaddingValues = { PaddingValues(0.dp) },
        text: (index: Int) -> String
    ) {
        repeat(count) { index ->
            items += internalItem(
                key = key?.let { it(index) },
                modifier = modifier,
                overlineContent = overlineContent,
                supportingContent = supportingContent,
                leadingContent = leadingContent,
                trailingContent = trailingContent,
                textColor = textColor,
                tonalElevation = tonalElevation,
                shadowElevation = shadowElevation,
                onClick = onClick?.let { { it(index) } },
                disableRoundedCorners = disableRoundedCorners,
                swipeMode = swipeMode(index),
                padding = padding(index),
                headlineContent = {
                    if (isLoading) {
                        RedactedText(
                            numberOfLines = 1,
                            textStyle = textStyle?.invoke() ?: LocalTextStyle.current,
                            lastLineFraction = Random.nextFloat() * (0.8f - 0.4f) + 0.4f
                        )
                    }
                    else {
                        Text(
                            text = text(index),
                            style = textStyle?.invoke() ?: LocalTextStyle.current,
                        )
                    }
                }
            )
        }
    }

    fun textItem(
        key: Any? = null,
        modifier: Modifier = Modifier,
        overlineContent: @Composable (() -> Unit)? = null,
        supportingContent: @Composable (() -> Unit)? = null,
        leadingContent: GroupedColumnItemLeadingContentType = GroupedColumnItemLeadingContentType.None,
        trailingContent: GroupedColumnItemTrailingContentType = GroupedColumnItemTrailingContentType.None,
        textColor: (@Composable () -> Color)? = null,
        tonalElevation: Dp = ListItemDefaults.Elevation,
        shadowElevation: Dp = ListItemDefaults.Elevation,
        onClick: (() -> Unit)? = null,
        disableRoundedCorners: Boolean = false,
        isLoading: Boolean = false,
        swipeMode: GroupedColumnItemSwipeMode = GroupedColumnItemSwipeMode.Disabled,
        textStyle: (@Composable () -> TextStyle)? = null,
        padding: PaddingValues = PaddingValues(0.dp),
        text: String
    ) {
        items += internalItem(
            key = key,
            modifier = modifier,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            textColor = textColor,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation,
            onClick = onClick,
            disableRoundedCorners = disableRoundedCorners,
            swipeMode = swipeMode,
            padding = padding,
            headlineContent = {
                if (isLoading) {
                    RedactedText(
                        numberOfLines = 1,
                        textStyle = textStyle?.invoke() ?: LocalTextStyle.current,
                        lastLineFraction = Random.nextFloat() * (0.8f - 0.4f) + 0.4f
                    )
                }
                else {
                    Text(
                        text = text,
                        style = textStyle?.invoke() ?: LocalTextStyle.current,
                    )
                }
            }
        )
    }

    private fun internalItem(
        key: Any?,
        modifier: Modifier,
        overlineContent: @Composable (() -> Unit)?,
        supportingContent: @Composable (() -> Unit)?,
        leadingContent: GroupedColumnItemLeadingContentType,
        trailingContent: GroupedColumnItemTrailingContentType,
        textColor: (@Composable () -> Color)? = null,
        tonalElevation: Dp,
        shadowElevation: Dp,
        onClick: (() -> Unit)?,
        disableRoundedCorners: Boolean,
        swipeMode: GroupedColumnItemSwipeMode,
        padding: PaddingValues,
        headlineContent: @Composable (() -> Unit),
    ): GroupedColumnItemData {
        return GroupedColumnItemData(
            key = key,
            onClick = when (swipeMode) {
                GroupedColumnItemSwipeMode.Disabled -> onClick
                is GroupedColumnItemSwipeMode.Enabled -> null
            },
            disableRoundedCorners = disableRoundedCorners,
            padding = padding
        ) {

            when (swipeMode) {
                GroupedColumnItemSwipeMode.Disabled -> {
                    listItem(
                        modifier = modifier,
                        overlineContent = overlineContent,
                        supportingContent = supportingContent,
                        leadingContent = leadingContent,
                        trailingContent = trailingContent,
                        textColor = textColor,
                        tonalElevation = tonalElevation,
                        shadowElevation = shadowElevation,
                        headlineContent = headlineContent
                    )
                }
                is GroupedColumnItemSwipeMode.Enabled -> {
                    var contextMenuWidth by remember {
                        mutableFloatStateOf(0f)
                    }
                    val offset = remember {
                        Animatable(initialValue = 0f)
                    }
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(
                        swipeMode.isRevealed,
                        contextMenuWidth
                    ) {
                        if (swipeMode.isRevealed) {
                            offset.animateTo(contextMenuWidth)
                        }
                        else {
                            offset.animateTo(0f)
                        }
                    }

                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        Row(
                            modifier = Modifier
                                .onSizeChanged {
                                    contextMenuWidth = it.width.toFloat()
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            swipeMode.actions(this)
                        }
                        listItem(
                            modifier = modifier
                                .fillMaxSize()
                                .offset {
                                    IntOffset(offset.value.roundToInt(), 0)
                                }
                                .pointerInput(contextMenuWidth) {
                                    detectHorizontalDragGestures(
                                        onHorizontalDrag = { _, dragAmount ->
                                            scope.launch {
                                                val newOffset = (offset.value + dragAmount)
                                                    .coerceIn(0f, contextMenuWidth)
                                                offset.snapTo(newOffset)
                                            }
                                        },
                                        onDragEnd = {
                                            when {
                                                offset.value >= contextMenuWidth / 2f -> {
                                                    scope.launch {
                                                        offset.animateTo(contextMenuWidth)
                                                        swipeMode.onExpand?.invoke()
                                                    }
                                                }

                                                else -> {
                                                    scope.launch {
                                                        offset.animateTo(0f)
                                                        swipeMode.onCollapse?.invoke()
                                                    }
                                                }
                                            }
                                        }
                                    )
                                },
                            overlineContent = overlineContent,
                            supportingContent = supportingContent,
                            leadingContent = leadingContent,
                            trailingContent = GroupedColumnItemTrailingContentType.Custom {
                                IconButton(
                                    onClick = { swipeMode.onExpand?.invoke() },
                                    colors = IconButtonColors(
                                        contentColor = Color.White,
                                        containerColor = Color.SEESTURM_RED,
                                        disabledContentColor = Color.White,
                                        disabledContainerColor = Color.SEESTURM_RED
                                    ),
                                    modifier = Modifier
                                        .size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        tint = Color.White,
                                        contentDescription = null
                                    )
                                }
                            },
                            textColor = textColor,
                            tonalElevation = tonalElevation,
                            shadowElevation = shadowElevation,
                            headlineContent = headlineContent
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun listItem(
        modifier: Modifier,
        overlineContent: @Composable (() -> Unit)?,
        supportingContent: @Composable (() -> Unit)?,
        leadingContent: GroupedColumnItemLeadingContentType,
        trailingContent: GroupedColumnItemTrailingContentType,
        textColor: (@Composable () -> Color)? = null,
        tonalElevation: Dp,
        shadowElevation: Dp,
        headlineContent: @Composable (() -> Unit),
    ) {
        ListItem(
            headlineContent = headlineContent,
            modifier = modifier,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = when (leadingContent) {
                GroupedColumnItemLeadingContentType.None -> null
                is GroupedColumnItemLeadingContentType.Icon -> {
                    {
                        Icon(
                            imageVector = leadingContent.imageVector,
                            contentDescription = leadingContent.contentDescription,
                            tint = leadingContent.color()
                        )
                    }
                }
                is GroupedColumnItemLeadingContentType.Custom -> leadingContent.content
            },
            trailingContent = when (trailingContent) {
                GroupedColumnItemTrailingContentType.None -> null
                GroupedColumnItemTrailingContentType.DisclosureIndicator -> {
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier
                                .alpha(0.4f)
                        )
                    }
                }
                is GroupedColumnItemTrailingContentType.Custom -> trailingContent.content
            },
            colors = ListItemDefaults.colors().copy(
                containerColor = if (LocalScreenContext.current is ScreenContext.ModalBottomSheet) {
                    MaterialTheme.colorScheme.tertiaryContainer
                }
                else {
                    MaterialTheme.colorScheme.primaryContainer
                },
                headlineColor = textColor?.invoke() ?: MaterialTheme.colorScheme.onBackground,
                trailingIconColor = MaterialTheme.colorScheme.onBackground
            ),
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation
        )
    }

    fun customItem(
        key: Any? = null,
        onClick: (() -> Unit)? = null,
        disableRoundedCorners: Boolean = true,
        paddingValues: PaddingValues = PaddingValues(0.dp),
        content: @Composable () -> Unit
    ) {
        items += GroupedColumnItemData(
            key = key,
            onClick = onClick,
            disableRoundedCorners = disableRoundedCorners,
            padding = paddingValues,
            content = content
        )
    }

    internal fun emit() {

        items.forEachIndexed { index, itemData ->
            lazyListScope.item(
                key = itemData.key
            ) {

                key(index, items.lastIndex) {
                    val isFirst = index == 0
                    val isLast = index == items.lastIndex
                    val shape = when {
                        isFirst && isLast -> RoundedCornerShape(size = 16.dp)
                        isFirst -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        isLast -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                        else -> RoundedCornerShape(0.dp)
                    }

                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .padding(itemData.padding)
                            .fillMaxWidth()
                            .then(
                                if (!itemData.disableRoundedCorners) {
                                    Modifier
                                        .clip(shape)
                                } else {
                                    Modifier
                                }
                            )
                            .then(
                                if (itemData.onClick != null) {
                                    Modifier
                                        .clickable {
                                            itemData.onClick()
                                        }
                                } else {
                                    Modifier
                                }
                            )
                            .animateItem()
                    ) {
                        itemData.content()
                        if (!isLast) {
                            HorizontalDivider(
                                color = separatorColor,
                                thickness = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth(separatorWidth)
                            )
                        }
                    }
                }
            }
        }
    }
}