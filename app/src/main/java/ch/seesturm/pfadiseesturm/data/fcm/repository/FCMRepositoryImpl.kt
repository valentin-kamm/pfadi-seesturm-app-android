package ch.seesturm.pfadiseesturm.data.fcm.repository

import androidx.datastore.core.DataStore
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.data.fcm.FCMApi
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.domain.fcm.repository.FCMRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FCMRepositoryImpl(
    private val api: FCMApi,
    private val dataStore: DataStore<SeesturmPreferencesDao>
): FCMRepository {

    override suspend fun subscribeToTopic(topic: SeesturmFCMNotificationTopic) {
        api.subscribeToFCMTopic(topic)
        insertLocalTopic(topic)
    }
    override suspend fun unsubscribeFromTopic(topic: SeesturmFCMNotificationTopic) {
        api.unsubscribeFromFCMTopic(topic)
        deleteLocalTopic(topic)
    }

    override fun getSubscribedTopics(): Flow<Set<SeesturmFCMNotificationTopic>> =
        dataStore.data.map { it.subscribedFcmTopics }

    override suspend fun getCurrentFCMToken(): String =
        api.getCurrentFCMToken()

    private suspend fun insertLocalTopic(topic: SeesturmFCMNotificationTopic) {
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

    private suspend fun deleteLocalTopic(topic: SeesturmFCMNotificationTopic) {
        dataStore.updateData { oldData ->
            val newTopicList = oldData.subscribedFcmTopics.toMutableSet()
            newTopicList.remove(topic)
            oldData.copy(
                subscribedFcmTopics = newTopicList
            )
        }
    }
}