package ch.seesturm.pfadiseesturm.data.fcf.dto

import kotlinx.serialization.Serializable

@Serializable
data class CloudFunctionTokenResponseDto(
    val userId: String,
    val firebaseAuthToken: String
)
