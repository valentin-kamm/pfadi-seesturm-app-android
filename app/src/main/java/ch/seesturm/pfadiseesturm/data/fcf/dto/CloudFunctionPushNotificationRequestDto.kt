package ch.seesturm.pfadiseesturm.data.fcf.dto

import kotlinx.serialization.Serializable

@Serializable
data class CloudFunctionPushNotificationRequestDto(
    val topic: String,
    val title: String,
    val body: String,
    val customKey: String?
)
