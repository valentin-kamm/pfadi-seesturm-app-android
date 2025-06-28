package ch.seesturm.pfadiseesturm.data.wordpress.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleCalendarEventStartEndDto(
    val dateTime: String?,
    val date: String?
)