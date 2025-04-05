package ch.seesturm.pfadiseesturm.data.fcf.dto

import kotlinx.serialization.Serializable

@Serializable
data class CloudFunctionAddEventResponseDto(
    val eventId: String
)
