package ch.seesturm.pfadiseesturm.presentation.anlaesse.list

import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.util.state.InfiniteScrollUiState

data class AnlaesseListState(
    val result: InfiniteScrollUiState<List<GoogleCalendarEvent>> = InfiniteScrollUiState.Loading,
    val nextPageToken: String? = "",
    val lastUpdated: String = "",
    val refreshing: Boolean = false
)
