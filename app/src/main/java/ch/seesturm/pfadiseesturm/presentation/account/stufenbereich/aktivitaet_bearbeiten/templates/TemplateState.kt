package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates

import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmRichTextState
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import com.mohamedrejeb.richeditor.model.RichTextState

data class TemplateState(
    val templatesState: UiState<List<AktivitaetTemplate>>,
    val templateEditMode: TemplateEditMode,
    val isInEditingMode: Boolean,
    val editState: ActionState<Unit>,
    val deleteState: ActionState<Unit>,
    val richTextState: SeesturmRichTextState
) {
    companion object {
        fun create(onSubmit: (String) -> Unit): TemplateState {
            return TemplateState(
                templatesState = UiState.Loading,
                templateEditMode = TemplateEditMode.Insert(
                    onSubmit = onSubmit
                ),
                isInEditingMode = false,
                editState = ActionState.Idle,
                deleteState = ActionState.Idle,
                richTextState = SeesturmRichTextState(
                    state = RichTextState(),
                    onValueChanged = {}
                )
            )
        }
    }
}
