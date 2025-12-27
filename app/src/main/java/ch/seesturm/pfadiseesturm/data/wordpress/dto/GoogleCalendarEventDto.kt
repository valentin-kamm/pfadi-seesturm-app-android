package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import java.time.ZoneId
import java.time.ZonedDateTime

data class GoogleCalendarEventDto(
    val id: String,
    val summary: String?,
    val description: String?,
    val location: String?,
    val created: String,
    val updated: String,
    val start: GoogleCalendarEventStartEndDto,
    val end: GoogleCalendarEventStartEndDto
) {
    val isAllDay: Boolean
        get() = start.dateTime == null
}

fun GoogleCalendarEventDto.toGoogleCalendarEvent(calendarTimeZone: ZoneId = ZoneId.of("Europe/Zurich")): GoogleCalendarEvent {

    val targetDisplayTimezone = ZoneId.of("Europe/Zurich")
    val localStartDate = this.getStartDate(calendarTimeZone, targetDisplayTimezone)
    val localEndDate = this.getEndDate(calendarTimeZone, targetDisplayTimezone)
    val localCreatedDate = DateTimeUtil.shared.parseIsoDateWithOffset(this.created).atZone(targetDisplayTimezone)
    val localUpdatedDate = DateTimeUtil.shared.parseIsoDateWithOffset(this.updated).atZone(targetDisplayTimezone)

    return GoogleCalendarEvent(
        id = id,
        title = summary ?: "Unbenannter Anlass",
        description = description,
        location = location,
        created = localCreatedDate,
        modified = localUpdatedDate,
        createdFormatted = DateTimeUtil.shared.formatDate(
            date = localCreatedDate,
            format = "dd. MMM, HH:mm 'Uhr'",
            type = DateFormattingType.Relative(true)
        ),
        modifiedFormatted = DateTimeUtil.shared.formatDate(
            date = localUpdatedDate,
            format = "dd. MMM, HH:mm 'Uhr'",
            type = DateFormattingType.Relative(true)
        ),
        isAllDay = isAllDay,
        firstDayOfMonthOfStartDate = DateTimeUtil.shared.getFirstDayOfMonthOfADate(localStartDate),
        start = localStartDate,
        end = localEndDate,
        startDayFormatted = DateTimeUtil.shared.formatDate(
            date = localStartDate,
            format = "dd.",
            type = DateFormattingType.Absolute
        ),
        startMonthFormatted = DateTimeUtil.shared.formatDate(
            date = localStartDate,
            format = "MMM",
            type = DateFormattingType.Absolute
        ),
        endDateFormatted = DateTimeUtil.shared.getEventEndDateString(startDate = localStartDate, endDate = localEndDate),
        timeFormatted = DateTimeUtil.shared.getEventTimeString(isAllDay = isAllDay, startDate = localStartDate, endDate = localEndDate),
        fullDateTimeFormatted = DateTimeUtil.shared.formatDateRange(
            localStartDate,
            localEndDate,
            isAllDay
        )
    )
}

// get end date of a google calendar event
private fun GoogleCalendarEventDto.getEndDate(calendarTimeZone: ZoneId, targetDisplayTimezone: ZoneId): ZonedDateTime {
    val endDate = when {
        end.dateTime != null -> {
            DateTimeUtil.shared.parseIsoDateWithOffset(end.dateTime)
        }
        end.date != null -> {
            DateTimeUtil.shared.parseFloatingDateString(end.date, calendarTimeZone)
        }
        else -> {
            throw PfadiSeesturmError.DateError("Anlass ohne Enddatum vorhanden.")
        }
    }.atZone(targetDisplayTimezone)
    return if (isAllDay) {
        endDate.minusDays(1)
    } else {
        endDate
    }
}

// get start date of a google calendar event
private fun GoogleCalendarEventDto.getStartDate(calendarTimeZone: ZoneId, targetDisplayTimezone: ZoneId): ZonedDateTime {
    return when {
        start.dateTime != null -> {
            DateTimeUtil.shared.parseIsoDateWithOffset(start.dateTime)
        }
        start.date != null -> {
            DateTimeUtil.shared.parseFloatingDateString(start.date, calendarTimeZone)
        }
        else -> {
            throw PfadiSeesturmError.DateError("Anlass ohne Startdatum vorhanden.")
        }
    }.atZone(targetDisplayTimezone)
}