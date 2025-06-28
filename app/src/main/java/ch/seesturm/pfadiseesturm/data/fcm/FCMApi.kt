package ch.seesturm.pfadiseesturm.data.fcm

import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

interface FCMApi {

    suspend fun subscribeToFCMTopic(topic: SeesturmFCMNotificationTopic)
    suspend fun unsubscribeFromFCMTopic(topic: SeesturmFCMNotificationTopic)
    suspend fun getCurrentFCMToken(): String
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

    override suspend fun getCurrentFCMToken(): String {
        return messaging.token.await()
    }
}