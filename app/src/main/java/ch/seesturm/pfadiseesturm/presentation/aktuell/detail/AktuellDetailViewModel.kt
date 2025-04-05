package ch.seesturm.pfadiseesturm.presentation.aktuell.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AktuellService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AktuellDetailViewModel(
    private val postId: Int,
    private val service: AktuellService,
    private val cacheIdentifier: MemoryCacheIdentifier
): ViewModel() {

    private val _state = MutableStateFlow<UiState<WordpressPost>>(UiState.Loading)
    val state = _state.asStateFlow()

    init {
        getPost()
    }

    fun getPost() {

        _state.update {
            UiState.Loading
        }
        viewModelScope.launch {
            val result = service.getOrFetchPost(
                postId = postId,
                cacheIdentifier = cacheIdentifier
            )
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        UiState.Error("Der Post konnte nicht geladen werden. ${result.error.defaultMessage}")
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