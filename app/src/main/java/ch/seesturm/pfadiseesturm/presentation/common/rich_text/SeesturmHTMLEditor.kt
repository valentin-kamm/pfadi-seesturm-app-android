package ch.seesturm.pfadiseesturm.presentation.common.rich_text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_BLUE
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorColors
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeesturmHTMLEditor(
    state: SeesturmRichTextState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    editorColor: Color = if (LocalScreenContext.current is ScreenContext.ModalBottomSheet) {
        MaterialTheme.colorScheme.tertiaryContainer
    }
    else {
        MaterialTheme.colorScheme.primaryContainer
    },
    contentPadding: PaddingValues = if (label == null) { RichTextEditorDefaults.richTextEditorWithoutLabelPadding() } else { RichTextEditorDefaults.richTextEditorWithLabelPadding() }
) {

    val colors: RichTextEditorColors = RichTextEditorDefaults.richTextEditorColors(
        containerColor = editorColor,
        focusedIndicatorColor = editorColor,
        unfocusedIndicatorColor = editorColor,
        errorIndicatorColor = editorColor,
        disabledIndicatorColor = editorColor,
        placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        disabledPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
    )

    LaunchedEffect(state.state.annotatedString) {
        state.onValueChanged()
    }

    state.state.config.linkColor = Color.SEESTURM_BLUE

    val isAddLinkDialogShown = rememberSaveable { mutableStateOf(false) }

    SeesturmHTMLAddLinkView(
        isShown = isAddLinkDialogShown.value,
        state = state.state,
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
            state = state.state,
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
            state = state.state,
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

@Preview(showBackground = true)
@Composable
private fun SeesturmHTMLEditorPreview() {
    PfadiSeesturmTheme {
        SeesturmHTMLEditor(
            state = SeesturmRichTextState(
                state = rememberRichTextState(),
                onValueChanged = {}
            ),
            modifier = Modifier
                .height(400.dp)
        )
    }
}