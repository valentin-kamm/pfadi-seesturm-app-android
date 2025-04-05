package ch.seesturm.pfadiseesturm.presentation.anlaesse.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.state.SeesturmInfiniteScrollUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.updateDataAndSubState
import ch.seesturm.pfadiseesturm.util.state.updateSubState
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AnlaesseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AnlaesseViewModel(
    private val service: AnlaesseService,
    private val calendar: SeesturmCalendar
): ViewModel() {

    // number of events per page loaded
    private val numberOfEventsPerPage: Int = 10

    // ui state
    private val _state = MutableStateFlow(AnlaesseListState())
    val state = _state.asStateFlow()

    init {
        getInitialEvents(false)
    }

    // function to load the initial set of events
    fun getInitialEvents(isPullToRefresh: Boolean) {

        if (!isPullToRefresh) {
            _state.update {
                it.copy(
                    result = SeesturmInfiniteScrollUiState.Loading
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
                            result = SeesturmInfiniteScrollUiState.Error("Anlässe konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            result = SeesturmInfiniteScrollUiState.Success(
                                result.data.items,
                                subState = SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Success
                            ),
                            nextPageToken = result.data.nextPageToken,
                            lastUpdated = result.data.updatedFormatted
                        )
                    }
                }
            }
        }.invokeOnCompletion {
            changeRefreshStatus(false)
        }
    }

    // function to load more events when scrolling to the bottom
    fun getMoreEvents() {
        val currentNextPageToken = state.value.nextPageToken
        if (currentNextPageToken == null) {
            _state.update {
                it.copy(
                    result = SeesturmInfiniteScrollUiState.Error("Es konnten keine weiteren Anlässe geladen werden, da die nächste Seite unbekannt ist.")
                )
            }
            return
        }
        _state.update {
            it.copy(
                result = it.result.updateSubState(
                    SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Loading
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
                                SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Error(
                                    "Es konnten nicht mehr Anlässe geladen werden. ${result.error.defaultMessage}"
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
                                newSubState = SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Success
                            ),
                            nextPageToken = result.data.nextPageToken,
                            lastUpdated = result.data.updatedFormatted
                        )
                    }
                }
            }
        }
    }

    // computed property that tells me if there is a next page
    val hasMoreEvents: Boolean
        get() = state.value.nextPageToken != null

    // function to group events by year and month
    fun groupEventsByYearAndMonth(posts: List<WordpressPost>): List<Pair<String, List<WordpressPost>>> {
        return posts
            .groupBy { it.publishedYear }
            .toSortedMap(compareByDescending { it })
            .map { (year, posts) -> year to posts }
    }

    // function to update the pull to refresh status
    private fun changeRefreshStatus(newStatus: Boolean) {
        _state.update {
            it.copy(
                refreshing = newStatus
            )
        }
    }

    fun updateAlertVisibility(isVisible: Boolean) {
        _state.update {
            it.copy(
                showCalendarSubscriptionAlert = isVisible
            )
        }
    }
}