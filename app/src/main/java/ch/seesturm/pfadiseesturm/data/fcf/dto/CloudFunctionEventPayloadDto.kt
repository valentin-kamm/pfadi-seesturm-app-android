package ch.seesturm.pfadiseesturm.data.fcf.dto

import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventStartEndDto
import kotlinx.serialization.Serializable

@Serializable
data class CloudFunctionEventPayloadDto(
    val summary: String?,
    val description: String?,
    val location: String?,
    val start: GoogleCalendarEventStartEndDto,
    val end: GoogleCalendarEventStartEndDto
)
