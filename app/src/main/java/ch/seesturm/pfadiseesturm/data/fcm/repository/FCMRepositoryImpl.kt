package ch.seesturm.pfadiseesturm.data.fcm.repository

import androidx.datastore.core.DataStore
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.data.fcm.FCMApi
import ch.seesturm.pfadiseesturm.domain.fcm.repository.FCMRepository
import ch.seesturm.pfadiseesturm.util.SeesturmFCMNotificationTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FCMRepositoryImpl(
    private val api: FCMApi,
    private val dataStore: DataStore<SeesturmPreferencesDao>
): FCMRepository {

    override suspend fun subscribeToTopic(topic: SeesturmFCMNotificationTopic) {
        api.subscribeToFCMTopic(topic)
    }
    override suspend fun unsubscribeFromTopic(topic: SeesturmFCMNotificationTopic) {
        api.unsubscribeFromFCMTopic(topic)
    }

    override fun getSubscribedTopics(): Flow<Set<SeesturmFCMNotificationTopic>> =
        dataStore.data.map { it.subscribedFcmTopics }
    override suspend fun addNewTopic(topic: SeesturmFCMNotificationTopic) {
        dataStore.updateData { oldData ->
            if (!oldData.subscribedFcmTopics.contains(topic)) {
                val newTopicList = oldData.subscribedFcmTopics + topic
                oldData.copy(
                    subscribedFcmTopics = newTopicList
                )
            }
            else {
                oldData
            }
        }
    }
    override suspend fun deleteTopic(topic: SeesturmFCMNotificationTopic) {
        dataStore.updateData { oldData ->
            val newTopicList = oldData.subscribedFcmTopics.toMutableSet()
            newTopicList.remove(topic)
            oldData.copy(
                subscribedFcmTopics = newTopicList
            )
        }
    }

    override suspend fun sendPushNotification(topic: SeesturmFCMNotificationTopic) {
        TODO("Not yet implemented")
    }
}