package ch.seesturm.pfadiseesturm.domain.fcm.model

data class SeesturmFCMNotificationContent(
    val title: String,
    val body: String,
    val customKey: String?
)