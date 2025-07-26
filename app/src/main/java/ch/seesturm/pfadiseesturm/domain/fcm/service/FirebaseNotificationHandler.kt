package ch.seesturm.pfadiseesturm.domain.fcm.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionPushNotificationRequestDto
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.main.MainActivity
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.authModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.fcmModule
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID

class FirebaseNotificationHandler: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            handleIncomingNotification(message)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            updateFCMToken(token)
        }
    }

    private suspend fun handleIncomingNotification(message: RemoteMessage) {

        try {
            val notificationData = decodeNotificationData(message.data)
            val topic = SeesturmFCMNotificationTopic.fromTopicString(notificationData.topic)

            if (!topic.displayForAuthenticatedHitobitoUserOnly) {
                showNotification(notificationData, topic)
                return
            }

            val isHitobitoUser = authModule.authService.isCurrentUserHitobitoUser()
            if (!isHitobitoUser) {
                return
            }

            showNotification(notificationData, topic)
        }
        catch (e: Exception) {
            println("Incoming notification could not be handled. ${e.message}")
            return
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(data: CloudFunctionPushNotificationRequestDto, topic: SeesturmFCMNotificationTopic) {

        val notificationId = UUID.randomUUID().hashCode()

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("topic", topic)
            putExtra("customKey", data.customKey)
            putExtra("notificationId", notificationId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "seesturm_notification_channel")
            .setContentTitle(data.title)
            .setContentText(data.body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.logotabbar)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data.body))

        if (topic == SeesturmFCMNotificationTopic.Schoepflialarm) {
            for (reaction in SchoepflialarmReactionType.entries) {

                val reactionIntent = Intent(this, SchoepflialarmNotificationActionReceiver::class.java).apply {
                    putExtra("reaction", reaction)
                    putExtra("notificationId", notificationId)
                }
                reactionIntent.action = "reaction_to_${notificationId}_${reaction.name}"
                val reactionPendingIntent = PendingIntent.getBroadcast(
                    this,
                    notificationId * 100 + reaction.ordinal,
                    reactionIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                notification.addAction(
                    reaction.iconRef,
                    reaction.title,
                    reactionPendingIntent
                )
            }
        }

        NotificationManagerCompat.from(this).notify(notificationId, notification.build())
    }

    private fun decodeNotificationData(data: Map<String, String>): CloudFunctionPushNotificationRequestDto {

        val gson = Gson()
        val jsonString = gson.toJson(data)
        return gson.fromJson(jsonString, CloudFunctionPushNotificationRequestDto::class.java)
    }

    private suspend fun updateFCMToken(token: String) {

        val currentUid = authModule.authService.getCurrentUid()
            ?: return

        val isHitobitoUser = authModule.authService.isCurrentUserHitobitoUser()
        if (!isHitobitoUser) {
            return
        }

        val result = fcmModule.fcmService.updateFCMToken(
            userId = currentUid,
            newToken = token
        )

        if (result is SeesturmResult.Error) {
            println("New FCM Token could not be saved to firestore. ${result.error.defaultMessage}")
        }
    }
}