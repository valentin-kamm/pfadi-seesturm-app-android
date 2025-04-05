package ch.seesturm.pfadiseesturm.presentation.aktuell.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.util.state.SeesturmInfiniteScrollUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.updateDataAndSubState
import ch.seesturm.pfadiseesturm.util.state.updateSubState
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AktuellService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AktuellViewModel(
    private val service: AktuellService
): ViewModel() {

    // number of posts per page loaded
    private val numberOfPostsPerPage: Int = 5

    // ui state
    private val _state = MutableStateFlow(AktuellListState())
    val state = _state.asStateFlow()

    init {
        getInitialPosts(false)
    }

    // number of posts
    private val postsLoadedCount: Int
        get() = when (val currentState = state.value.result) {
            is SeesturmInfiniteScrollUiState.Success -> currentState.data.count()
            else -> 0
        }

    // determine if there are more posts
    val hasMorePosts: Boolean
        get() = when (state.value.result) {
            is SeesturmInfiniteScrollUiState.Success -> {
                postsLoadedCount < state.value.totalPostsAvailable
            }
            else -> false
        }

    // function to get initial set of posts
    fun getInitialPosts(isPullToRefresh: Boolean) {

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
            val result = service.fetchPosts(
                start = 0,
                length = numberOfPostsPerPage
            )
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            result = SeesturmInfiniteScrollUiState.Error("Posts konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            result = SeesturmInfiniteScrollUiState.Success(
                                result.data.posts,
                                subState = SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Success
                            ),
                            totalPostsAvailable = result.data.totalPosts,
                        )
                    }
                }
            }
        }.invokeOnCompletion {
            changeRefreshStatus(false)
        }
    }

    // function to load more posts via the infinite scroll functionality
    fun getMorePosts() {

        _state.update {
            it.copy(
                result = it.result.updateSubState(
                    SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Loading
                )
            )
        }

        viewModelScope.launch {
            val result = service.fetchMorePosts(
                start = postsLoadedCount,
                length = numberOfPostsPerPage
            )
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            result = it.result.updateSubState(
                                SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Error(
                                    "Es konnten nicht mehr Posts geladen werden. ${result.error.defaultMessage}"
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
                                    oldData + result.data.posts
                                },
                                newSubState = SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState.Success
                            ),
                            totalPostsAvailable = result.data.totalPosts
                        )
                    }
                }
            }
        }
    }

    // function to update the pull to refresh status
    private fun changeRefreshStatus(newStatus: Boolean) {
        _state.update {
            it.copy(
                refreshing = newStatus
            )
        }
    }
}