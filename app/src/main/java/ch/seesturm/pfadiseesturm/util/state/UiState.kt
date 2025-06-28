package ch.seesturm.pfadiseesturm.util.state

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
            is Loading -> true
            else -> false
        }
}