package ch.seesturm.pfadiseesturm.domain.fcf.model

import android.text.Html
import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionEventPayloadDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventStartEndDto
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

data class CloudFunctionEventPayload(
    val summary: String,
    val description: String,
    val location: String,
    val isAllDay: Boolean,
    val start: ZonedDateTime,
    val end: ZonedDateTime
)

fun CloudFunctionEventPayload.toGoogleCalendarEvent(): GoogleCalendarEvent {

    val now = ZonedDateTime.now()
    val nowString = DateTimeUtil.shared.formatDate(
        date = now,
        format = "d. MMMM, HH:mm 'Uhr'",
        withRelativeDateFormatting = true,
        includeTimeInRelativeFormatting = true
    )

    return GoogleCalendarEvent(
        id = UUID.randomUUID().toString(),
        title = summary,
        description = description.trim().ifEmpty { null },
        location = location.trim().ifEmpty { null },
        created = now,
        updated = now,
        createdFormatted = nowString,
        updatedFormatted = nowString,
        firstDayOfMonthOfStartDate = DateTimeUtil.shared.getFirstDayOfMonthOfADate(start),
        startDate = start,
        endDate = end,
        startDayFormatted = DateTimeUtil.shared.formatDate(
            date = start,
            format = "dd.",
            withRelativeDateFormatting = false
        ),
        startMonthFormatted = DateTimeUtil.shared.formatDate(
            date = start,
            format = "MMM",
            withRelativeDateFormatting = false
        ),
        endDateFormatted = DateTimeUtil.shared.getEventEndDateString(startDate = start, endDate = end),
        timeFormatted = DateTimeUtil.shared.getEventTimeString(isAllDay = isAllDay, startDate = start, endDate = end),
        fullDateTimeFormatted = DateTimeUtil.shared.formatDateRange(
            start,
            end,
            isAllDay
        ),
        isAllDay = isAllDay
    )
}

fun CloudFunctionEventPayload.toCloudFunctionEventPayloadDto(): CloudFunctionEventPayloadDto {

    val timeZoneForEvent = ZoneId.of("Europe/Zurich")

    return CloudFunctionEventPayloadDto(
        summary = summary.trim().ifEmpty { null },
        description = description.trim().ifEmpty { null },
        location = location.trim().ifEmpty { null },
        start = GoogleCalendarEventStartEndDto(
            dateTime = if (isAllDay) null else { DateTimeUtil.shared.getIso8601DateString(date = start, timeZone = timeZoneForEvent) },
            date = if (isAllDay) { DateTimeUtil.shared.formatDate(date = start, format = "yyyy-MM-dd", withRelativeDateFormatting = false, includeTimeInRelativeFormatting = false) } else null
        ),
        end = GoogleCalendarEventStartEndDto(
            dateTime = if (isAllDay) null else { DateTimeUtil.shared.getIso8601DateString(date = end, timeZone = timeZoneForEvent) },
            date = if (isAllDay) { DateTimeUtil.shared.formatDate(date = end, format = "yyyy-MM-dd", withRelativeDateFormatting = false, includeTimeInRelativeFormatting = false) } else null
        )
    )
}