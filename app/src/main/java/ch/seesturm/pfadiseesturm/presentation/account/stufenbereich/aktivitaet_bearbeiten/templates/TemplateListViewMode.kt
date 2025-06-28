package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates

import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.util.state.ActionState

sealed class TemplateListViewMode {
    data object Use: TemplateListViewMode()
    data class Edit(
        val onAddClick: () -> Unit,
        val editState: ActionState<Unit>,
        val deleteState: ActionState<Unit>,
        val onCollapseItem: (AktivitaetTemplate) -> Unit,
        val onExpandItem: (AktivitaetTemplate) -> Unit,
        val onDeleteItem: (AktivitaetTemplate) -> Unit
    ): TemplateListViewMode()
}