package ch.seesturm.pfadiseesturm.presentation.common.snackbar

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object SnackbarController {

    private val _events = Channel<SeesturmSnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SeesturmSnackbarEvent) {
        _events.send(event)
    }
}