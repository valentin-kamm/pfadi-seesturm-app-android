package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvents
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import java.time.ZoneId

data class GoogleCalendarEventsDto(
    val updated: String,
    val timeZone: String,
    val nextPageToken: String?,
    val items: List<GoogleCalendarEventDto>
)

fun GoogleCalendarEventsDto.toGoogleCalendarEvents(): GoogleCalendarEvents {

    val calendarTimeZone = ZoneId.of(timeZone)
    val targetDisplayTimezone = ZoneId.of("Europe/Zurich")
    val updatedDate = DateTimeUtil.shared.parseIsoDateWithOffset(updated).atZone(targetDisplayTimezone)

    return GoogleCalendarEvents(
        updatedFormatted = DateTimeUtil.shared.formatDate(
            date = updatedDate,
            format = "dd. MMMM yyyy",
            type = DateFormattingType.Relative(false)
        ),
        timeZone = calendarTimeZone,
        nextPageToken = nextPageToken,
        items = items.map { it.toGoogleCalendarEvent(calendarTimeZone) }
    )
}