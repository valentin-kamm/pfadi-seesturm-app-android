package ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces

import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface TemplatesCapableEventController {
    val templatesState: StateFlow<UiState<List<AktivitaetTemplate>>>
    val showTemplatesSheet: StateFlow<Boolean>
    fun setShowTemplatesSheet(show: Boolean)
    fun observeTemplates(viewModelScope: CoroutineScope)
}