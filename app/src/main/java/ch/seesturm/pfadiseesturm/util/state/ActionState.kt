package ch.seesturm.pfadiseesturm.util.state

sealed interface ActionState<out D>: SeesturmState {

    data object Idle: ActionState<Nothing>
    data class Loading<out D>(val action: D): ActionState<D>
    data class Error<out D>(val action: D, val message: String) : ActionState<D>
    data class Success<out D>(val action: D, val message: String) : ActionState<D>

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