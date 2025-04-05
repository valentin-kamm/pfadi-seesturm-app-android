package ch.seesturm.pfadiseesturm.domain.wordpress.model

import java.time.ZoneId

data class GoogleCalendarEvents(
    val updatedFormatted: String,
    val timeZone: ZoneId,
    val nextPageToken: String?,
    val items: List<GoogleCalendarEvent>
)
