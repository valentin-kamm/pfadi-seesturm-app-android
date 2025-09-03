package ch.seesturm.pfadiseesturm.presentation.common.snackbar

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

object SnackbarController {

    private val _events = MutableSharedFlow<SeesturmSnackbarVisuals>()
    val events: Flow<SeesturmSnackbarVisuals> = _events.asSharedFlow()

    suspend fun sendSnackbar(snackbar: SeesturmSnackbar) {
        _events.emit(snackbar.visuals)
    }
}