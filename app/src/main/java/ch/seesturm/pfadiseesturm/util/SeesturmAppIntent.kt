package ch.seesturm.pfadiseesturm.util

import android.content.Intent
import android.os.Build
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.util.types.SeesturmAppLink

sealed class SeesturmAppIntent {

    data class AppLinksIntent(
        val link: SeesturmAppLink
    ): SeesturmAppIntent()
    data class PushNotificationIntent(
        val notificationId: Int,
        val topic: SeesturmFCMNotificationTopic,
        val customKey: String?
    ): SeesturmAppIntent()

    companion object {

        fun fromIntent(intent: Intent): SeesturmAppIntent? {

            // check if I am dealing with an app links intent
            val url = intent.data
            val appLink = SeesturmAppLink.fromUrl(url)
            if (appLink != null) {
                return AppLinksIntent(appLink)
            }

            // check if I am dealing with a push notification (pending) intent
            val notificationId = intent.getIntExtra("notificationId", -1)
            val topic = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("topic", SeesturmFCMNotificationTopic::class.java)
            }
            else {
                intent.getSerializableExtra("topic") as? SeesturmFCMNotificationTopic
            }
            val customKey = intent.getStringExtra("customKey")
            if (topic != null && notificationId != -1) {
                return PushNotificationIntent(
                    notificationId = notificationId,
                    topic = topic,
                    customKey = customKey
                )
            }

            return null
        }
    }
}