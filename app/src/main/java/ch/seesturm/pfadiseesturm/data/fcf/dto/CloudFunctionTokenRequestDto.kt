package ch.seesturm.pfadiseesturm.data.fcf.dto

import kotlinx.serialization.Serializable

@Serializable
data class CloudFunctionTokenRequestDto(
    val userId: String,
    val hitobitoAccessToken: String
)
