package ch.seesturm.pfadiseesturm.presentation.aktuell.list

import ch.seesturm.pfadiseesturm.util.state.SeesturmInfiniteScrollUiState
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost

data class AktuellListState(
    val result: SeesturmInfiniteScrollUiState<List<WordpressPost>> = SeesturmInfiniteScrollUiState.Loading,
    val totalPostsAvailable: Int = 0,
    val refreshing: Boolean = false
)