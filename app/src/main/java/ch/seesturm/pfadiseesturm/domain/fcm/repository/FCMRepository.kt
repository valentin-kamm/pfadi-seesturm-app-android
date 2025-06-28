package ch.seesturm.pfadiseesturm.domain.fcm.repository

import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import kotlinx.coroutines.flow.Flow

interface FCMRepository {

    suspend fun subscribeToTopic(topic: SeesturmFCMNotificationTopic)
    suspend fun unsubscribeFromTopic(topic: SeesturmFCMNotificationTopic)
    fun getSubscribedTopics(): Flow<Set<SeesturmFCMNotificationTopic>>
    suspend fun getCurrentFCMToken(): String
}