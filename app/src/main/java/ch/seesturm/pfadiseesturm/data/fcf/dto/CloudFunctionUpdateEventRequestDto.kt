package ch.seesturm.pfadiseesturm.data.fcf.dto

import kotlinx.serialization.Serializable

@Serializable
data class CloudFunctionUpdateEventRequestDto(
    val calendarId: String,
    val eventId: String,
    val payload: CloudFunctionEventPayloadDto
)
