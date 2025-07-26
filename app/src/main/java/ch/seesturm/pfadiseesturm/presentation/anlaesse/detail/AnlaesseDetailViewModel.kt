package ch.seesturm.pfadiseesturm.presentation.anlaesse.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AnlaesseService
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnlaesseDetailViewModel(
    private val calendar: SeesturmCalendar,
    private val eventId: String,
    private val service: AnlaesseService,
    private val cacheIdentifier: MemoryCacheIdentifier
): ViewModel() {

    private val _state = MutableStateFlow(AnlaesseDetailState())
    val state = _state.asStateFlow()

    init {
        getEvent()
    }

    fun getEvent() {

        _state.update {
            it.copy(
                eventState = UiState.Loading
            )
        }
        viewModelScope.launch {
            val result = service.getOrFetchEvent(
                calendar = calendar,
                eventId = eventId,
                cacheIdentifier = cacheIdentifier
            )
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            eventState = UiState.Error("Der Anlass konnte nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            eventState = UiState.Success(result.data)
                        )
                    }
                }
            }
        }
    }
}
