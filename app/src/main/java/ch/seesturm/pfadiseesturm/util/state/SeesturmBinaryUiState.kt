package ch.seesturm.pfadiseesturm.util.state

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed interface SeesturmBinaryUiState<out D> {

    data class Error<out D>(val message: String) : SeesturmBinaryUiState<D>
    data class Success<out D>(val data: D) : SeesturmBinaryUiState<D>

    val isError: Boolean
        get() = when (this) {
            is Error -> true
            else -> false
        }

    val isSuccess: Boolean
        get() = when (this) {
            is Success -> true
            else -> false
        }

    val errorMessage: String
        get() = when (this) {
            is Error -> { this.message }
            else -> { "" }
        }

    val errorText: @Composable (() -> Unit)?
        get() = when (this) {
            is Success -> null
            is Error -> {
                { Text(this.message) }
            }
        }
    }