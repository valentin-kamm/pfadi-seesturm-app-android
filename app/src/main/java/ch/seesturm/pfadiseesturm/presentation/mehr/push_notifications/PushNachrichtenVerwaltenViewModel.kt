package ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.domain.fcm.service.FCMService
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarType
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PushNachrichtenVerwaltenViewModel(
    private val service: FCMService
): ViewModel() {

    private val _state = MutableStateFlow(PushNachrichtenVerwaltenState())
    val state = _state.asStateFlow()

    init {
        getSubscribedTopics()
        sendSnackbars()
    }

    private fun getSubscribedTopics() {
        _state.update {
            it.copy(
                subscribedTopicsState = UiState.Loading
            )
        }
        service.readSubscribedTopics().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            subscribedTopicsState = UiState.Error(result.error.defaultMessage)
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            subscribedTopicsState = UiState.Success(result.data)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleTopic(
        topic: SeesturmFCMNotificationTopic,
        isSwitchingOn: Boolean,
        requestPermission: suspend () -> Boolean
    ) {
        _state.update {
            it.copy(
                actionState = ActionState.Loading(topic)
            )
        }
        viewModelScope.launch {
            if (isSwitchingOn) {
                subscribeTo(topic = topic, requestPermission = requestPermission)
            }
            else {
                unsubscribeFrom(topic = topic)
            }
        }
    }

    private suspend fun subscribeTo(
        topic: SeesturmFCMNotificationTopic,
        requestPermission: suspend () -> Boolean
    ) {
        when (val result = service.subscribe(topic, requestPermission)) {
            is SeesturmResult.Error -> {
                when (result.error) {
                    DataError.Messaging.PERMISSION_ERROR -> {
                        _state.update {
                            it.copy(
                                actionState = ActionState.Idle
                            )
                        }
                        updateAlertVisibility(true)
                    }
                    else -> {
                        _state.update {
                            it.copy(
                                actionState = ActionState.Error(topic, result.error.defaultMessage)
                            )
                        }
                    }
                }
            }
            is SeesturmResult.Success -> {
                _state.update {
                    it.copy(
                        actionState = ActionState.Success(topic, "Anmeldung für ${topic.topicName} erfolgreich.")
                    )
                }
            }
        }
    }

    private suspend fun unsubscribeFrom(
        topic: SeesturmFCMNotificationTopic,
    ) {
        when (val result = service.unsubscribe(topic)) {
            is SeesturmResult.Error -> {
                _state.update {
                    it.copy(
                        actionState = ActionState.Error(topic, result.error.defaultMessage)
                    )
                }
            }
            is SeesturmResult.Success -> {
                _state.update {
                    it.copy(
                        actionState = ActionState.Success(topic, "Abmeldung von ${topic.topicName} erfolgreich.")
                    )
                }
            }
        }
    }

    private fun sendSnackbars() {
        viewModelScope.launch {
            state.map { it.actionState }
                .distinctUntilChanged()
                .collect { newActionState ->
                    when (newActionState) {
                        is ActionState.Error -> {
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = newActionState.message,
                                    duration = SnackbarDuration.Short,
                                    type = SeesturmSnackbarType.Error,
                                    allowManualDismiss = true,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                actionState = ActionState.Idle
                                            )
                                        }
                                    },
                                    showInSheetIfPossible = false
                                )
                            )
                        }
                        is ActionState.Success -> {
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = newActionState.message,
                                    duration = SnackbarDuration.Short,
                                    type = SeesturmSnackbarType.Success,
                                    allowManualDismiss = true,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                actionState = ActionState.Idle
                                            )
                                        }
                                    },
                                    showInSheetIfPossible = false
                                )
                            )
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
        }
    }

    fun updateAlertVisibility(isVisible: Boolean) {
        _state.update {
            it.copy(
                showSettingsAlert = isVisible
            )
        }
    }
}