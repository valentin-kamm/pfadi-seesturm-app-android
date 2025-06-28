package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates

import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmRichTextState
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import com.mohamedrejeb.richeditor.model.RichTextState

data class TemplateState(
    val templatesState: UiState<List<AktivitaetTemplate>> = UiState.Loading,
    val isInEditingMode: Boolean = false,
    val editState: ActionState<Unit> = ActionState.Idle,
    val deleteState: ActionState<Unit> = ActionState.Idle,
    val richTextState: SeesturmRichTextState = SeesturmRichTextState(
        state = RichTextState(),
        onValueChanged = {}
    )
)
