package ch.seesturm.pfadiseesturm.presentation.anlaesse.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AnlaesseService
import ch.seesturm.pfadiseesturm.util.state.InfiniteScrollUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.updateDataAndSubState
import ch.seesturm.pfadiseesturm.util.state.updateSubState
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AnlaesseViewModel(
    private val service: AnlaesseService,
    private val calendar: SeesturmCalendar
): ViewModel() {

    private val numberOfEventsPerPage: Int = 10

    private val _state = MutableStateFlow(AnlaesseListState())
    val state = _state.asStateFlow()

    init {
        getInitialEvents(false)
    }

    val hasMoreEvents: Boolean
        get() = state.value.nextPageToken != null

    fun getInitialEvents(isPullToRefresh: Boolean) {

        if (!isPullToRefresh) {
            _state.update {
                it.copy(
                    result = InfiniteScrollUiState.Loading
                )
            }
        }
        else {
            changeRefreshStatus(true)
        }

        viewModelScope.launch {
            val result = service.fetchEvents(
                calendar = calendar,
                includePast = false,
                maxResults = numberOfEventsPerPage
            )
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            result = InfiniteScrollUiState.Error("Anlässe konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            result = InfiniteScrollUiState.Success(
                                result.data.items,
                                subState = InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Success
                            ),
                            nextPageToken = result.data.nextPageToken,
                            lastUpdated = result.data.updatedFormatted
                        )
                    }
                }
            }
        }.invokeOnCompletion {
            if (isPullToRefresh) {
                changeRefreshStatus(false)
            }
        }
    }

    fun getMoreEvents() {

        val currentNextPageToken = state.value.nextPageToken

        if (currentNextPageToken == null) {
            _state.update {
                it.copy(
                    result = InfiniteScrollUiState.Error("Es konnten keine weiteren Anlässe geladen werden, da die nächste Seite unbekannt ist.")
                )
            }
            return
        }

        _state.update {
            it.copy(
                result = it.result.updateSubState(
                    InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Loading
                )
            )
        }
        viewModelScope.launch {
            val result = service.fetchMoreEvents(
                calendar = calendar,
                pageToken = currentNextPageToken,
                maxResults = numberOfEventsPerPage
            )
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            result = it.result.updateSubState(
                                InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Error(
                                    "Es konnten keine weiteren Anlässe geladen werden. ${result.error.defaultMessage}"
                                )
                            )
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            result = it.result.updateDataAndSubState(
                                { oldData ->
                                    oldData + result.data.items
                                },
                                newSubState = InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Success
                            ),
                            nextPageToken = result.data.nextPageToken,
                            lastUpdated = result.data.updatedFormatted
                        )
                    }
                }
            }
        }
    }

    private fun changeRefreshStatus(newStatus: Boolean) {
        _state.update {
            it.copy(
                refreshing = newStatus
            )
        }
    }
}