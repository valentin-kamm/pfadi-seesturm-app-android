package ch.seesturm.pfadiseesturm.data.data_store.dao

import ch.seesturm.pfadiseesturm.util.SeesturmFCMNotificationTopic
import kotlinx.serialization.Serializable

@Serializable
data class SubscribedFCMNotificationTopicDao(
    val topic: SeesturmFCMNotificationTopic
)