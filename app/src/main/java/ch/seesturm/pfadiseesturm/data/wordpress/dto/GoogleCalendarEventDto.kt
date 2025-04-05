package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs

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

@Serializable
data class GoogleCalendarEventStartEndDto(
    val dateTime: String?,
    val date: String?
)

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
        updated = localUpdatedDate,
        createdFormatted = DateTimeUtil.shared.formatDate(
            date = localCreatedDate,
            format = "d. MMMM, HH:mm 'Uhr'",
            withRelativeDateFormatting = true
        ),
        updatedFormatted = DateTimeUtil.shared.formatDate(
            date = localUpdatedDate,
            format = "d. MMMM, HH:mm 'Uhr'",
            withRelativeDateFormatting = true
        ),
        firstDayOfMonthOfStartDate = DateTimeUtil.shared.getFirstDayOfMonthOfADate(localStartDate),
        startDate = localStartDate,
        endDate = localEndDate,
        startDayFormatted = DateTimeUtil.shared.formatDate(
            date = localStartDate,
            format = "dd.",
            withRelativeDateFormatting = false
        ),
        startMonthFormatted = DateTimeUtil.shared.formatDate(
            date = localStartDate,
            format = "MMM",
            withRelativeDateFormatting = false
        ),
        endDateFormatted = DateTimeUtil.shared.getEventEndDateString(startDate = localStartDate, endDate = localEndDate),
        timeFormatted = DateTimeUtil.shared.getEventTimeString(isAllDay = isAllDay, startDate = localStartDate, endDate = localEndDate),
        fullDateTimeFormatted = DateTimeUtil.shared.formatDateRange(
            localStartDate,
            localEndDate,
            isAllDay
        ),
        isAllDay = isAllDay
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
            throw PfadiSeesturmAppError.DateError("Anlass ohne Enddatum vorhanden.")
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
            throw PfadiSeesturmAppError.DateError("Anlass ohne Startdatum vorhanden.")
        }
    }.atZone(targetDisplayTimezone)
}