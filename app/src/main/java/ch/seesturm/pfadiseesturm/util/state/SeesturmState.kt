package ch.seesturm.pfadiseesturm.util.state

import ch.seesturm.pfadiseesturm.util.SeesturmError
import ch.seesturm.pfadiseesturm.util.state.ActionState.Loading

interface SeesturmState {
    val isError: Boolean
    val isSuccess: Boolean
}

// wrapper for user interactions
sealed interface ActionState<out D>: SeesturmState {
    data object Idle: ActionState<Nothing>
    data class Loading<out D>(val action: D): ActionState<D>
    data class Error<out D>(val action: D, val message: String) : ActionState<D>
    data class Success<out D>(val action: D, val message: String) : ActionState<D>

    override val isError: Boolean
        get() = when (this) {
            is Error -> { true }
            else -> { false }
        }
    override val isSuccess: Boolean
        get() = when (this) {
            is Success -> { true }
            else -> { false }
        }
    val isLoading: Boolean
        get() = when (this) {
            is Loading -> { true }
            else -> { false }
        }
}

// handle results from API calls
sealed interface SeesturmResult<out D, out E: SeesturmError>: SeesturmState {
    data class Error<out D, out E: SeesturmError>(val error: E): SeesturmResult<D, E>
    data class Success<out D, out E: SeesturmError>(val data: D): SeesturmResult<D, E>

    override val isError: Boolean
        get() = when (this) {
            is Error -> { true }
            else -> { false }
        }
    override val isSuccess: Boolean
        get() = when (this) {
            is Success -> { true }
            else -> { false }
        }
}

// wrapper for simple UI State (loading, success, error)
sealed interface UiState<out D>: SeesturmState {
    data object Loading: UiState<Nothing>
    data class Error<out D>(val message: String): UiState<D>
    data class Success<out D>(val data: D): UiState<D>

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
    val isLoading: Boolean
        get() = when (this) {
            is Loading -> { true }
            else -> { false }
        }
}

// wrapper for infinite scroll UI State
sealed interface SeesturmInfiniteScrollUiState<out D>: SeesturmState {
    data object Loading: SeesturmInfiniteScrollUiState<Nothing>
    data class Error<out D>(val message: String): SeesturmInfiniteScrollUiState<D>
    data class Success<out D>(val data: D, val subState: SeesturmInfiniteScrollUiSubState):
        SeesturmInfiniteScrollUiState<D> {
        sealed interface SeesturmInfiniteScrollUiSubState {
            data object Loading: SeesturmInfiniteScrollUiSubState
            data class Error(val message: String): SeesturmInfiniteScrollUiSubState
            data object Success: SeesturmInfiniteScrollUiSubState

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
fun <D> SeesturmInfiniteScrollUiState<D>.updateSubState(
    newSubState: SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState
): SeesturmInfiniteScrollUiState<D> {
    return when (this) {
        is SeesturmInfiniteScrollUiState.Success -> this.copy(subState = newSubState)
        else -> this
    }
}
fun <D> SeesturmInfiniteScrollUiState<D>.updateDataAndSubState(
    transformData: (D) -> D,
    newSubState: SeesturmInfiniteScrollUiState.Success.SeesturmInfiniteScrollUiSubState
): SeesturmInfiniteScrollUiState<D> {
    return when (this) {
        is SeesturmInfiniteScrollUiState.Success -> this.copy(
            data = transformData(this.data),
            subState = newSubState
        )
        else -> this
    }
}
