package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Surface
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
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun <T> SwipeableFormItem(
    items: List<T>,
    index: Int,
    mainContent: FormItemContentType,
    enabled: Boolean = false,
    isRevealed: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    onExpanded: () -> Unit = {},
    onCollapsed: () -> Unit = {},
    leadingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    trailingElement: FormItemTertiaryElementType = FormItemTertiaryElementType.Blank,
    modifier: Modifier = Modifier
) {

    val isFirst = index == 0
    val isLast = index == items.lastIndex

    val shape = when {
        isFirst && isLast -> RoundedCornerShape(16.dp)
        isFirst -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        isLast -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        else -> RoundedCornerShape(0.dp)
    }

    if (enabled) {
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
            if(isRevealed) {
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
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offset.value.roundToInt(), 0) }
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
                                            onExpanded()
                                        }
                                    }

                                    else -> {
                                        scope.launch {
                                            offset.animateTo(0f)
                                            onCollapsed()
                                        }
                                    }
                                }
                            }
                        )
                    }
            ) {
                FormItem(
                    items = items,
                    index = index,
                    mainContent = mainContent,
                    leadingIcon = leadingIcon,
                    onClick = onClick,
                    trailingElement = trailingElement,
                    modifier = modifier
                )
            }
        }
    }
    else {
        FormItem(
            items = items,
            index = index,
            mainContent = mainContent,
            leadingIcon = leadingIcon,
            onClick = onClick,
            trailingElement = trailingElement,
            modifier = modifier
        )
    }
}

@Preview("Custom Content")
@Composable
fun SwipeableFormItemPreview() {
    FormItem(
        items = (0..<2).toList(),
        index = 0,
        mainContent = FormItemContentType.Text(
            title = "Fotos"
        ),
        leadingIcon = Icons.Default.House,
        trailingElement = FormItemTertiaryElementType.Custom(
            content = {
                IconButton(
                    onClick = {},
                    colors = IconButtonColors(
                        containerColor = Color.SEESTURM_RED,
                        contentColor = Color.White,
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
        )
    )
}