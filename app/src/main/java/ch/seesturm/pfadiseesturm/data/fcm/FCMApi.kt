package ch.seesturm.pfadiseesturm.data.fcm

import ch.seesturm.pfadiseesturm.domain.data_store.repository.GespeichertePersonenRepository
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarType
import ch.seesturm.pfadiseesturm.util.SeesturmFCMNotificationTopic
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resumeWithException

interface FCMApi {

    suspend fun subscribeToFCMTopic(topic: SeesturmFCMNotificationTopic)
    suspend fun unsubscribeFromFCMTopic(topic: SeesturmFCMNotificationTopic)
    
    suspend fun sendPushNotification(topic: SeesturmFCMNotificationTopic)

}

class FCMApiImpl(
    private val messaging: FirebaseMessaging
): FCMApi {

    override suspend fun subscribeToFCMTopic(topic: SeesturmFCMNotificationTopic) {
        messaging.subscribeToTopic(topic.topic).await()
    }
    override suspend fun unsubscribeFromFCMTopic(topic: SeesturmFCMNotificationTopic) {
        messaging.unsubscribeFromTopic(topic.topic).await()
    }

    override suspend fun sendPushNotification(topic: SeesturmFCMNotificationTopic) {
        TODO("Not yet implemented")
    }
}