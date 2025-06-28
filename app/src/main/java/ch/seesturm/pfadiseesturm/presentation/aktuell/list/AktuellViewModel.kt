package ch.seesturm.pfadiseesturm.presentation.aktuell.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AktuellService
import ch.seesturm.pfadiseesturm.util.state.InfiniteScrollUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.updateDataAndSubState
import ch.seesturm.pfadiseesturm.util.state.updateSubState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AktuellViewModel(
    private val service: AktuellService
): ViewModel() {

    private val numberOfPostsPerPage: Int = 5

    private val _state = MutableStateFlow(AktuellListState())
    val state = _state.asStateFlow()

    init {
        getInitialPosts(false)
    }

    private val postsLoadedCount: Int
        get() = when (val currentState = state.value.result) {
            is InfiniteScrollUiState.Success -> currentState.data.count()
            else -> 0
        }

    val hasMorePosts: Boolean
        get() = when (state.value.result) {
            is InfiniteScrollUiState.Success -> {
                postsLoadedCount < state.value.totalPostsAvailable
            }
            else -> false
        }

    fun getInitialPosts(isPullToRefresh: Boolean) {

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
            val result = service.fetchPosts(
                start = 0,
                length = numberOfPostsPerPage
            )
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            result = InfiniteScrollUiState.Error("Posts konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            result = InfiniteScrollUiState.Success(
                                result.data.posts,
                                subState = InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Success
                            ),
                            totalPostsAvailable = result.data.postCount,
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

    fun getMorePosts() {

        _state.update {
            it.copy(
                result = it.result.updateSubState(
                    InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Loading
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
                                InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Error(
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
                                newSubState = InfiniteScrollUiState.Success.InfiniteScrollUiSubState.Success
                            ),
                            totalPostsAvailable = result.data.postCount
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