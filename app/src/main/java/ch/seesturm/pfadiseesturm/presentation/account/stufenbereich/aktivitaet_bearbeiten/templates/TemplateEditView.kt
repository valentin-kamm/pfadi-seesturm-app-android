package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmHTMLEditor
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmRichTextState
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.getUnescapedHtml
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.state.ActionState
import com.mohamedrejeb.richeditor.model.RichTextState

@Composable
fun TemplateEditView(
    mode: TemplateEditMode,
    richTextState: SeesturmRichTextState,
    editState: ActionState<Unit>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            FormItem(
                items = (0..0).toList(),
                index = 0,
                mainContent = FormItemContentType.Custom(
                    content = {
                        SeesturmHTMLEditor(
                            state = richTextState,
                            enabled = !editState.isLoading,
                            label = {
                                Text("Vorlage")
                            },
                            placeholder = {
                                Text("Vorlage")
                            },
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                SeesturmButton(
                    type = SeesturmButtonType.Primary(),
                    title = mode.buttonTitle,
                    onClick = {
                        when (mode) {
                            is TemplateEditMode.Insert -> {
                                mode.onSubmit(richTextState.state.getUnescapedHtml())
                            }
                            is TemplateEditMode.Update -> {
                                mode.onSubmit(richTextState.state.getUnescapedHtml())
                            }
                        }
                    },
                    isLoading = editState.isLoading,
                    enabled = !editState.isLoading,
                )
            }
        }
    }
}

@Preview("Loading", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Loading", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemplateEditViewPreview1() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            TemplateEditView(
                mode = TemplateEditMode.Insert(
                    onSubmit = {}
                ),
                richTextState = SeesturmRichTextState(
                    state = RichTextState(),
                    onValueChanged = {},
                    annotatedString = AnnotatedString("")
                ),
                editState = ActionState.Loading(Unit),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}

@Preview("Idle", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Idle", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemplateEditViewPreview2() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            TemplateEditView(
                mode = TemplateEditMode.Update(
                    description = "",
                    onSubmit = {}
                ),
                richTextState = SeesturmRichTextState(
                    state = RichTextState(),
                    onValueChanged = {},
                    annotatedString = AnnotatedString("")
                ),
                editState = ActionState.Idle,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}