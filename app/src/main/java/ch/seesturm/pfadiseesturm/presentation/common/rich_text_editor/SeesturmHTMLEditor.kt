package ch.seesturm.pfadiseesturm.presentation.common.rich_text_editor

import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.outlined.AddLink
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorColors
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import org.jsoup.parser.Parser
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeesturmHTMLEditor(
    state: RichTextState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    colors: RichTextEditorColors = RichTextEditorDefaults.richTextEditorColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        focusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
        errorIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
        disabledIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
        placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        disabledPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    ),
    contentPadding: PaddingValues = if (label == null) { RichTextEditorDefaults.richTextEditorWithoutLabelPadding() } else { RichTextEditorDefaults.richTextEditorWithLabelPadding() }
) {

    state.config.linkColor = Color.SEESTURM_BLUE

    val isAddLinkDialogShown = rememberSaveable { mutableStateOf(false) }

    SeesturmHTMLAddLinkView(
        isShown = isAddLinkDialogShown.value,
        state = state,
        onDismiss = {
            isAddLinkDialogShown.value = false
        }
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        RichTextEditor(
            state = state,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            enabled = enabled,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            colors = colors,
            contentPadding = contentPadding
        )
        SeesturmHTMLEditorToolbar(
            state = state,
            enabled = enabled,
            onInsertLink = {
                isAddLinkDialogShown.value = true
            },
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        )
    }
}

@Composable
private fun SeesturmHTMLEditorToolbar(
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

@Composable
private fun SeesturmHTMLAddLinkView(
    isShown: Boolean,
    state: RichTextState,
    onDismiss: () -> Unit
) {

    val text = rememberSaveable { mutableStateOf("") }
    val url = rememberSaveable { mutableStateOf("") }
    val isUrlError = rememberSaveable { mutableStateOf(false) }

    fun isValidUrl(): Boolean {
        return try {
            URL(url.value.trim()).toURI()
            true
        }
        catch (e: Exception) {
            false
        }
    }

    fun onSubmit() {
        if (isValidUrl()) {
            state.addLink(text.value.trim(), url.value.trim())
            onDismiss()
            text.value = ""
            url.value = ""
        }
        else {
            isUrlError.value = true
        }
    }

    if (isShown) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.AddLink,
                    contentDescription = null
                )
            },
            title = {
                Column {
                    Text("Link einfügen")
                    OutlinedTextField(
                        value = text.value,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        onValueChange = {
                            text.value = it
                        },
                        label = { Text("Text") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = null,
                                tint = Color.SEESTURM_GREEN
                            )
                        }
                    )
                    OutlinedTextField(
                        value = url.value,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        onValueChange = {
                            isUrlError.value = false
                            url.value = it
                        },
                        label = { Text("URL") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = Color.SEESTURM_GREEN
                            )
                        },
                        isError = isUrlError.value,
                        supportingText = {
                            if (isUrlError.value) {
                                Text("Die URL ist ungültig")
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSubmit()
                    }
                ) {
                    Text("Einfügen")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

val RichTextState.isHTMLEmpty
    get() = Html.fromHtml(Parser.unescapeEntities(toHtml(), false), Html.FROM_HTML_MODE_LEGACY).toString().trim().isEmpty()

fun RichTextState.getHTMLForGoogleCalendar(): String {
    return if (isHTMLEmpty) {
        ""
    }
    else {
        Parser.unescapeEntities(toHtml().trim(), false)
    }
}

@Preview(showBackground = true)
@Composable
private fun SeesturmHTMLEditorPreview() {
    PfadiSeesturmTheme {
        SeesturmHTMLEditor(
            state = rememberRichTextState(),
            modifier = Modifier
                .height(400.dp)
        )
    }
}


@Preview
@Composable
private fun SeesturmHTMLAddLinkViewPreview() {
    SeesturmHTMLAddLinkView(
        state = rememberRichTextState(),
        isShown = true,
        onDismiss = {}
    )
}

@Preview
@Composable
private fun SeesturmHTMLEditorToolbarPreview() {
    SeesturmHTMLEditorToolbar(
        state = rememberRichTextState(),
        onInsertLink = {},
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Preview
@Composable
private fun SeesturmHTMLEditorToolbarButtonPreview() {
    SeesturmHTMLEditorToolbarButton(
        action = SeesturmHTMLToolbarAction.Bold,
        state = rememberRichTextState(),
        onInsertLink = {}
    )
}