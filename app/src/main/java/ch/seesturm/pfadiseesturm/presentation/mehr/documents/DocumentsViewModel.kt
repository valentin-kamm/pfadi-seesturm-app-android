package ch.seesturm.pfadiseesturm.presentation.mehr.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressDocument
import ch.seesturm.pfadiseesturm.domain.wordpress.service.DocumentsService
import ch.seesturm.pfadiseesturm.util.types.WordpressDocumentType
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentsViewModel(
    private val service: DocumentsService,
    private val documentType: WordpressDocumentType
): ViewModel() {

    private val _state = MutableStateFlow<UiState<List<WordpressDocument>>>(UiState.Loading)
    val state = _state.asStateFlow()

    init {
        fetchDocuments()
    }

    fun fetchDocuments() {

        _state.update {
            UiState.Loading
        }

        viewModelScope.launch {
            when (val result = service.getDocuments(documentType)) {
                is SeesturmResult.Error -> {
                    _state.update {
                        UiState.Error("Dokumente konnten nicht geladen werden. ${result.error.defaultMessage}")
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        UiState.Success(result.data)
                    }
                }
            }
        }
    }
}