package ch.seesturm.pfadiseesturm.util.state

sealed interface ProgressActionState<out D>: SeesturmState {

    data object Idle: ProgressActionState<Nothing>
    data class Loading<out D>(val action: D, val progress: Double): ProgressActionState<D>
    data class Error<out D>(val action: D, val message: String) : ProgressActionState<D>
    data class Success<out D>(val action: D, val message: String) : ProgressActionState<D>

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
}