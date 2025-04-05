package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvents
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
    return GoogleCalendarEvents(
        updatedFormatted = DateTimeUtil.shared.formatDate(
            date = DateTimeUtil.shared.parseIsoDateWithOffset(updated).atZone(targetDisplayTimezone),
            format = "dd. MMMM yyyy",
            withRelativeDateFormatting = true
        ),
        timeZone = calendarTimeZone,
        nextPageToken = nextPageToken,
        items = items.map { it.toGoogleCalendarEvent(calendarTimeZone) }
    )
}