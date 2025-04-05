package ch.seesturm.pfadiseesturm.data.fcf.dto

import kotlinx.serialization.Serializable

@Serializable
data class CloudFunctionAddEventRequestDto(
    val calendarId: String,
    val payload: CloudFunctionEventPayloadDto
)
