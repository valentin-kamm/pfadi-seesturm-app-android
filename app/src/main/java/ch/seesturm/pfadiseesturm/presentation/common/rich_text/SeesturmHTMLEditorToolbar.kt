package ch.seesturm.pfadiseesturm.presentation.common.rich_text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState

@Composable
fun SeesturmHTMLEditorToolbar(
    state: RichTextState,
    onInsertLink: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
    separatorColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
) {
    LazyRow(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SeesturmHTMLToolbarAction.actionGroups.forEachIndexed { index, group ->
            if (index > 0) {
                item(
                    key = "ToolbarDivider$index"
                ) {
                    VerticalDivider(
                        color = separatorColor,
                        thickness = 1.dp,
                        modifier = Modifier
                            .height(30.dp)
                    )
                }
            }
            group.forEach { action ->
                item(
                    key = "ToolbarButton${action.ordinal}"
                ) {
                    SeesturmHTMLEditorToolbarButton(
                        action = action,
                        state = state,
                        enabled = enabled,
                        onInsertLink = onInsertLink
                    )
                }
            }
        }
    }
}

@Composable
private fun SeesturmHTMLEditorToolbarButton(
    action: SeesturmHTMLToolbarAction,
    state: RichTextState,
    onInsertLink: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconToggleButton(
        onCheckedChange = {
            when (action) {
                SeesturmHTMLToolbarAction.Link -> {
                    if (state.isLink) {
                        state.removeLink()
                    }
                    else {
                        onInsertLink()
                    }
                }
                else -> {
                    action.performButtonAction(state)
                }
            }
        },
        checked = action.isSelected(state),
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = IconToggleButtonColors(
            containerColor = Color.Transparent,
            contentColor = action.buttonTint,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = action.buttonTint,
            checkedContainerColor = action.buttonTint.copy(alpha = 0.2f),
            checkedContentColor = action.buttonTint
        )
    ) {
        Icon(
            imageVector = action.icon,
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SeesturmHTMLEditorToolbarPreview() {
    PfadiSeesturmTheme {
        SeesturmHTMLEditorToolbar(
            state = rememberRichTextState(),
            onInsertLink = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SeesturmHTMLEditorToolbarButtonPreview() {
    PfadiSeesturmTheme {
        SeesturmHTMLEditorToolbarButton(
            action = SeesturmHTMLToolbarAction.Bold,
            state = rememberRichTextState(),
            onInsertLink = {}
        )
    }
}