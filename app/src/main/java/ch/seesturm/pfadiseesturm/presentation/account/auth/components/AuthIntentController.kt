package ch.seesturm.pfadiseesturm.presentation.account.auth.components

import android.content.Intent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object AuthIntentController {

    private val _intents = Channel<Intent>()
    val intents = _intents.receiveAsFlow()

    suspend fun launchIntent(intent: Intent) {
        _intents.send(intent)
    }
}