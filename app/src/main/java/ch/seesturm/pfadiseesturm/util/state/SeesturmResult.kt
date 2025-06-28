package ch.seesturm.pfadiseesturm.util.state

import ch.seesturm.pfadiseesturm.util.SeesturmError

sealed interface SeesturmResult<out D, out E: SeesturmError>: SeesturmState {

    data class Error<out D, out E: SeesturmError>(val error: E): SeesturmResult<D, E>
    data class Success<out D, out E: SeesturmError>(val data: D): SeesturmResult<D, E>

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