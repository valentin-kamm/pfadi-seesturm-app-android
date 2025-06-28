package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun <T>SwipeableFormItem(
    items: List<T>,
    index: Int,
    content: FormItemContentType,
    modifier: Modifier = Modifier,
    swipeEnabled: Boolean = false,
    isRevealed: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    onExpand: () -> Unit = {},
    onCollapse: () -> Unit = {},
    leadingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    trailingElementWhenEnabled: @Composable () -> Unit = {
        IconButton(
            onClick = onExpand,
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
    }
) {

    val isFirst = index == 0
    val isLast = index == items.lastIndex

    val shape = when {
        isFirst && isLast -> RoundedCornerShape(size = 16.dp)
        isFirst -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        isLast -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        else -> RoundedCornerShape(size = 0.dp)
    }

    if (swipeEnabled) {

        var contextMenuWidth by remember {
            mutableFloatStateOf(0f)
        }
        val offset = remember {
            Animatable(initialValue = 0f)
        }
        val scope = rememberCoroutineScope()

        LaunchedEffect(
            isRevealed,
            contextMenuWidth
        ) {
            if (isRevealed) {
                offset.animateTo(contextMenuWidth)
            }
            else {
                offset.animateTo(0f)
            }
        }

        Box(
            modifier = modifier
                .clip(shape)
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
                actions()
            }
            FormItem(
                items = items,
                index = index,
                mainContent = content,
                leadingIcon = leadingIcon,
                onClick = onClick,
                disableRoundedCorners = true,
                trailingElement = FormItemTrailingElementType.Custom(
                    content = trailingElementWhenEnabled
                ),
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
                                            onExpand()
                                        }
                                    }
                                    else -> {
                                        scope.launch {
                                            offset.animateTo(0f)
                                            onCollapse()
                                        }
                                    }
                                }
                            }
                        )
                    }
            )
        }
    }
    else {
        FormItem(
            items = items,
            index = index,
            mainContent = content,
            modifier = modifier,
            leadingIcon = leadingIcon,
            onClick = onClick,
            trailingElement = FormItemTrailingElementType.Blank
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwipeableFormItemPreview() {

    PfadiSeesturmTheme {

        val items: List<Int> = (0..<2).toList()

        LazyColumn(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(all = 16.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items.forEachIndexed { index, _ ->
                item {
                    SwipeableFormItem(
                        items = items,
                        index = index,
                        content = FormItemContentType.Text(
                            title = "Test $index"
                        ),
                        swipeEnabled = index == 1,
                        isRevealed = index == 1,
                        actions = {
                            FormItemActionIcon(
                                onClick = {},
                                backgroundColor = Color.Red,
                                icon = Icons.Filled.Delete
                            )
                        }
                    )
                }
            }
        }
    }
}