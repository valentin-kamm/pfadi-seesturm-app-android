package ch.seesturm.pfadiseesturm.main

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object OnboardingController {

    private val _events = Channel<Unit>()
    val events = _events.receiveAsFlow()

    suspend fun showOnboardingView() {
        _events.send(Unit)
    }
}