package ch.seesturm.pfadiseesturm.presentation.anlaesse.list

import ch.seesturm.pfadiseesturm.util.state.SeesturmInfiniteScrollUiState
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent

data class AnlaesseListState(
    val result: SeesturmInfiniteScrollUiState<List<GoogleCalendarEvent>> = SeesturmInfiniteScrollUiState.Loading,
    val nextPageToken: String? = "",
    val lastUpdated: String = "",
    val refreshing: Boolean = false,
    val showCalendarSubscriptionAlert: Boolean = false
)
