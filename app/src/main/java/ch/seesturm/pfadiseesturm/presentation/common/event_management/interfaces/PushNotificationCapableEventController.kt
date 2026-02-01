package ch.seesturm.pfadiseesturm.presentation.common.event_management.interfaces

import kotlinx.coroutines.flow.StateFlow

interface PushNotificationCapableEventController {
    val sendPushNotification: StateFlow<Boolean>
    fun setSendPushNotification(isOn: Boolean)
}