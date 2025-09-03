package ch.seesturm.pfadiseesturm.util.state

sealed interface AnonymousActionState<out D>: SeesturmState {

    data object Idle: AnonymousActionState<Nothing>
    data object Loading: AnonymousActionState<Nothing>
    data class Error(val message: String) : AnonymousActionState<Nothing>
    data class Success<out D>(val action: D, val message: String) : AnonymousActionState<D>

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

    val isLoading: Boolean
        get() = when (this) {
            is Loading -> true
            else -> false
        }
}