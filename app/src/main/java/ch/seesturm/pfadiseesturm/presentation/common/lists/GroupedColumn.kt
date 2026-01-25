package ch.seesturm.pfadiseesturm.presentation.common.lists

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED

@Composable
fun GroupedColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    separatorColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
    separatorWidth: Float = 0.87f,
    sectionSpacing: Dp = 32.dp,
    content: GroupedColumnScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        overscrollEffect = overscrollEffect
    ) {
        val scope = GroupedColumnScope(
            lazyListScope = this,
            separatorWidth = separatorWidth,
            separatorColor = separatorColor,
            sectionSpacing = sectionSpacing
        )
        scope.content()
        scope.emit()
    }
}

@Preview
@Composable
private fun GroupedColumnPreview() {
    PfadiSeesturmTheme {
        GroupedColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            section {
                textItem(
                    text = "Hello world!"
                )
                textItem(
                    text = "Hello world!",
                    trailingContent = GroupedColumnItemTrailingContentType.DisclosureIndicator
                )
                textItem(
                    text = "Hello world!",
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Default.Error,
                        color = { Color.SEESTURM_RED }
                    )
                )
                textItem(
                    text = "Hello world!",
                    trailingContent = GroupedColumnItemTrailingContentType.DisclosureIndicator,
                    leadingContent = GroupedColumnItemLeadingContentType.Icon(
                        imageVector = Icons.Default.Error
                    )
                )
                textItem(
                    text = "Hello world!",
                    trailingContent = GroupedColumnItemTrailingContentType.Custom {
                        Button({}) { Text("Hi!") }
                    }
                )
                textItem(
                    text = "Hello world!",
                    leadingContent = GroupedColumnItemLeadingContentType.Custom {
                        Button({}) { Text("Hi!") }
                    }
                )
            }
            section(
                header = {
                    Text("How is it going")
                },
                footer = {
                    Text("good, thanks")
                }
            ) {
                item {
                    Button({}) { Text("Hi!") }
                }
            }
            section {
                textItem(
                    text = "Hello",
                    swipeMode = GroupedColumnItemSwipeMode.Enabled(
                        isRevealed = true,
                        actions = {}
                    )
                )
            }
        }
    }
}