package ch.seesturm.pfadiseesturm.presentation.aktuell.list

import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.util.state.InfiniteScrollUiState

data class AktuellListState(
    val result: InfiniteScrollUiState<List<WordpressPost>> = InfiniteScrollUiState.Loading,
    val totalPostsAvailable: Int = 0,
    val refreshing: Boolean = false
)