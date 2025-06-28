package ch.seesturm.pfadiseesturm.util.state

sealed interface InfiniteScrollUiState<out D>: SeesturmState {

    data object Loading: InfiniteScrollUiState<Nothing>
    data class Error<out D>(val message: String): InfiniteScrollUiState<D>
    data class Success<out D>(val data: D, val subState: InfiniteScrollUiSubState):
        InfiniteScrollUiState<D> {
        sealed interface InfiniteScrollUiSubState {

            data object Loading: InfiniteScrollUiSubState
            data class Error(val message: String): InfiniteScrollUiSubState
            data object Success: InfiniteScrollUiSubState

            val infiniteScrollTaskShouldRun: Boolean
                get() = when (this) {
                    is Success -> true
                    else -> false
                }
        }
    }

    override val isError: Boolean
        get() = when (this) {
            is Error -> true
            else -> false
        }

    override val isSuccess: Boolean
        get() = when (this) {
            is Success -> true
            else -> false
        }

    val scrollingDisabled: Boolean
        get() = when (this) {
            is Loading -> true
            else -> false
        }
}

fun <D> InfiniteScrollUiState<D>.updateSubState(
    newSubState: InfiniteScrollUiState.Success.InfiniteScrollUiSubState
): InfiniteScrollUiState<D> {
    return when (this) {
        is InfiniteScrollUiState.Success -> this.copy(subState = newSubState)
        else -> this
    }
}

fun <D> InfiniteScrollUiState<D>.updateDataAndSubState(
    transformData: (D) -> D,
    newSubState: InfiniteScrollUiState.Success.InfiniteScrollUiSubState
): InfiniteScrollUiState<D> {
    return when (this) {
        is InfiniteScrollUiState.Success -> this.copy(
            data = transformData(this.data),
            subState = newSubState
        )
        else -> this
    }
}