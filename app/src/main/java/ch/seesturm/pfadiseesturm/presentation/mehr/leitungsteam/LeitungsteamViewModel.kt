package ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Leitungsteam
import ch.seesturm.pfadiseesturm.domain.wordpress.service.LeitungsteamService
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeitungsteamViewModel(
    private val service: LeitungsteamService
): ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Leitungsteam>>>(
        UiState.Loading)
    val state = _state.asStateFlow()

    init {
        fetchLeitungsteam()
    }

    fun fetchLeitungsteam() {
        _state.update {
            UiState.Loading
        }
        viewModelScope.launch {
            when (val result = service.fetchLeitungsteam()) {
                is SeesturmResult.Error -> {
                    _state.update {
                        UiState.Error("Leitungsteam konnte nicht geladen werden. ${result.error.defaultMessage}")
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