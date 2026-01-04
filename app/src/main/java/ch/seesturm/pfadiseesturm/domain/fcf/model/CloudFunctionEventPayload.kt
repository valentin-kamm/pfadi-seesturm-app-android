package ch.seesturm.pfadiseesturm.domain.fcf.model

import ch.seesturm.pfadiseesturm.data.fcf.dto.CloudFunctionEventPayloadDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventStartEndDto
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
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

fun CloudFunctionEventPayload.toCloudFunctionEventPayloadDto(): CloudFunctionEventPayloadDto {

    val timeZoneForEvent = ZoneId.of("Europe/Zurich")

    return CloudFunctionEventPayloadDto(
        summary = summary.trim().ifEmpty { null },
        description = description.trim().ifEmpty { null },
        location = location.trim().ifEmpty { null },
        start = GoogleCalendarEventStartEndDto(
            dateTime = if (isAllDay) null else { DateTimeUtil.shared.getIso8601DateString(date = start, timeZone = timeZoneForEvent) },
            date = if (isAllDay) {
                DateTimeUtil.shared.formatDate(
                    date = start,
                    format = "yyyy-MM-dd",
                    type = DateFormattingType.Absolute
                )
            }
            else {
                null
            }
        ),
        end = GoogleCalendarEventStartEndDto(
            dateTime = if (isAllDay) null else { DateTimeUtil.shared.getIso8601DateString(date = end, timeZone = timeZoneForEvent) },
            date = if (isAllDay) {
                DateTimeUtil.shared.formatDate(
                    date = end.plusDays(1),
                    format = "yyyy-MM-dd",
                    type = DateFormattingType.Absolute
                )
            }
            else {
                null
            }
        )
    )
}

fun CloudFunctionEventPayload.toGoogleCalendarEvent(): GoogleCalendarEvent {

    val targetDisplayTimezone = ZoneId.of("Europe/Zurich")

    val now = ZonedDateTime.now(targetDisplayTimezone)
    val nowString = DateTimeUtil.shared.formatDate(
        date = now,
        format = "d. MMMM, HH:mm 'Uhr'",
        type = DateFormattingType.Relative(true)
    )

    val startDate: ZonedDateTime = if (!isAllDay) {
        start
    }
    else {
        start.toLocalDate().atStartOfDay(targetDisplayTimezone)
    }
    val endDate: ZonedDateTime = if (!isAllDay) {
        end
    }
    else {
        end.toLocalDate().atStartOfDay(targetDisplayTimezone)
    }

    return GoogleCalendarEvent(
        id = UUID.randomUUID().toString(),
        title = summary.trim(),
        description = description.trim().ifEmpty { null },
        location = location.trim().ifEmpty { null },
        created = now,
        modified = now,
        createdFormatted = nowString,
        modifiedFormatted = nowString,
        isAllDay = isAllDay,
        firstDayOfMonthOfStartDate = DateTimeUtil.shared.getFirstDayOfMonthOfADate(startDate),
        start = startDate,
        end = endDate,
        startDateFormatted = DateTimeUtil.shared.formatDate(
            date = startDate,
            format = "dd. MMMM yyyy",
            type = DateFormattingType.Absolute
        ),
        startDayFormatted = DateTimeUtil.shared.formatDate(
            date = startDate,
            format = "dd.",
            type = DateFormattingType.Absolute
        ),
        startMonthFormatted = DateTimeUtil.shared.formatDate(
            date = startDate,
            format = "MMM",
            type = DateFormattingType.Absolute
        ),
        endDateFormatted = DateTimeUtil.shared.getEventEndDateString(startDate = startDate, endDate = endDate),
        timeFormatted = DateTimeUtil.shared.getEventTimeString(isAllDay = isAllDay, startDate = startDate, endDate = endDate),
        fullDateTimeFormatted = DateTimeUtil.shared.formatDateRange(
            startDate,
            endDate,
            isAllDay
        )
    )
}