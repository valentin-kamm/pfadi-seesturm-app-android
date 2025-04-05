package ch.seesturm.pfadiseesturm.util

import com.google.firebase.Timestamp
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class DateTimeUtil {

    companion object {
        val shared: DateTimeUtil by lazy { DateTimeUtil() }
    }

    fun nextSaturdayInCHTime(atHour: Int): ZonedDateTime {

        val zoneId = ZoneId.of("Europe/Zurich")
        val now = ZonedDateTime.now(zoneId)

        var nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
        nextSaturday = nextSaturday.withHour(atHour).withMinute(0).withSecond(0).withNano(0)

        return nextSaturday
    }

    fun getIso8601DateString(date: ZonedDateTime, timeZone: ZoneId?): String {
        return timeZone?.let {
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            date.withZoneSameInstant(it).format(formatter)
        } ?: throw PfadiSeesturmAppError.DateError("Das Datum ist falsch. ISO8601 String kann nicht erstell werden.")
    }

    fun convertFirestoreTimestampToDate(timestamp: Timestamp?): ZonedDateTime {
        if (timestamp != null) {
            return timestamp.toDate().toInstant().atZone(ZoneId.of("UTC"))
        }
        else {
            throw PfadiSeesturmAppError.DateError("Datum nicht vorhanden.")
        }
    }

    // function to get the start of the month of the provided date
    fun getFirstDayOfMonthOfADate(date: ZonedDateTime): ZonedDateTime {
        return try {
            date.withDayOfMonth(1).toLocalDate().atStartOfDay(date.zone)
        }
        catch (e: Exception) {
            throw PfadiSeesturmAppError.DateError("Fehler bei der Datumsverarbeitung.")
        }
    }

    // used to parse start and end dates of all day events of google calendar
    fun parseFloatingDateString(floatingDateString: String, floatingDateTimeZone: ZoneId): Instant {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return try {
            val localDate = LocalDate.parse(floatingDateString, formatter)
            localDate.atStartOfDay(floatingDateTimeZone).toInstant()
        }
        catch (e: Exception) {
            throw PfadiSeesturmAppError.DateError("Datumsformat ungültig.")
        }
    }

    // function to parse a iso date
    fun parseIsoDateWithOffset(dateString: String): Instant {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        try {
            return Instant.from(formatter.parse(dateString))
        }
        catch (e: Exception) {
            throw PfadiSeesturmAppError.DateError("Datum ungültig.")
        }
    }

    // function to format a date range
    fun formatDateRange(
        startDate: ZonedDateTime,
        endDate: ZonedDateTime,
        isAllDay: Boolean
    ): String {

        // Beispiele:

        // ganztägiges event (1 tag)
        // 13. Oktober 2025, ganztägig

        // ganztägiges event (mehrere Tag)
        // 13. Oktober 2025 bis 14. Oktober 2025, ganztägig

        // nicht-ganztägiges event (1 tag)
        // 13. Oktober 2025, 13:00 bis 14:00 Uhr

        // nicht-ganztägiges event (mehrere Tage)
        // 13. Oktober 2025, 13:00 Uhr bis 14. Oktober 2025, 14:00 Uhr

        if (endDate < startDate) {
            throw PfadiSeesturmAppError.DateError("Enddatum ist kleiner als Startdatum.")
        }

        val isSingleDay = startDate.year == endDate.year && startDate.month == endDate.month && startDate.dayOfYear == endDate.dayOfYear

        val firstPart = if (isAllDay && isSingleDay) {
            val formatter = DateTimeFormatter
                .ofPattern("dd. MMMM yyyy")
                .withLocale(Locale("de", "CH"))
            formatter.format(startDate) + ", ganztägig"
        }
        else if (isAllDay && !isSingleDay) {
            val formatter = DateTimeFormatter
                .ofPattern("dd. MMMM yyyy")
                .withLocale(Locale("de", "CH"))
            formatter.format(startDate) + " bis "
        }
        else if (!isAllDay && isSingleDay) {
            val formatter = DateTimeFormatter
                .ofPattern("dd. MMMM yyyy, HH:mm")
                .withLocale(Locale("de", "CH"))
            formatter.format(startDate) + " bis "
        }
        else {
            val formatter = DateTimeFormatter
                .ofPattern("dd. MMMM yyyy, HH:mm")
                .withLocale(Locale("de", "CH"))
            formatter.format(startDate) + " Uhr bis "
        }

        val secondPart = if (isAllDay && isSingleDay) {
            ""
        }
        else if (isAllDay && !isSingleDay) {
            val formatter = DateTimeFormatter
                .ofPattern("dd. MMMM yyyy")
                .withLocale(Locale("de", "CH"))
            formatter.format(endDate) + ", ganztägig"
        }
        else if (!isAllDay && isSingleDay) {
            val formatter = DateTimeFormatter
                .ofPattern("HH:mm")
                .withLocale(Locale("de", "CH"))
            formatter.format(endDate) + " Uhr"
        }
        else {
            val formatter = DateTimeFormatter
                .ofPattern("dd. MMMM yyyy, HH:mm")
                .withLocale(Locale("de", "CH"))
            formatter.format(endDate) + " Uhr"
        }

        return firstPart + secondPart

    }

    // function to format a date into the desired string
    fun formatDate(date: ZonedDateTime, format: String, withRelativeDateFormatting: Boolean, includeTimeInRelativeFormatting: Boolean = true): String {
        val candidateForRelativeFormatting = (
                isTheDayBeforeYesterday(date) ||
                isYesterday(date) ||
                isToday(date) ||
                isTomorrow(date) ||
                isTheDayAfterTomorrow(date)
                )
        if (withRelativeDateFormatting && candidateForRelativeFormatting) {
            val dateString =
                if (isTheDayBeforeYesterday(date)) {
                    "Vorgestern"
                }
                else if (isYesterday(date)) {
                    "Gestern"
                }
                else if (isToday(date)) {
                    "Heute"
                }
                else if (isTomorrow(date)) {
                    "Morgen"
                }
                else if (isTheDayAfterTomorrow(date)) {
                    "Übermorgen"
                }
                else {
                ""
                }
            if (!includeTimeInRelativeFormatting) {
                return dateString
            }
            val timeFormatter = DateTimeFormatter
                .ofPattern("HH:mm")
                .withLocale(Locale("de", "CH"))
            val timeString = timeFormatter.format(date)
            return "$dateString um $timeString Uhr"
        }
        val formatter = DateTimeFormatter
            .ofPattern(format)
            .withLocale(Locale("de", "CH"))
        return formatter.format(date)
    }

    private fun isTheDayBeforeYesterday(date: ZonedDateTime): Boolean {
        val now = ZonedDateTime.now(date.zone).toLocalDate()
        val referenceDate = date.toLocalDate()
        return referenceDate.isEqual(now.minusDays(2))
    }
    private fun isYesterday(date: ZonedDateTime): Boolean {
        val now = ZonedDateTime.now(date.zone).toLocalDate()
        val referenceDate = date.toLocalDate()
        return referenceDate.isEqual(now.minusDays(1))
    }
    private fun isToday(date: ZonedDateTime): Boolean {
        val now = ZonedDateTime.now(date.zone).toLocalDate()
        val referenceDate = date.toLocalDate()
        return referenceDate.isEqual(now)
    }
    private fun isTomorrow(date: ZonedDateTime): Boolean {
        val now = ZonedDateTime.now(date.zone).toLocalDate()
        val referenceDate = date.toLocalDate()
        return referenceDate.isEqual(now.plusDays(1))
    }
    private fun isTheDayAfterTomorrow(date: ZonedDateTime): Boolean {
        val now = ZonedDateTime.now(date.zone).toLocalDate()
        val referenceDate = date.toLocalDate()
        return referenceDate.isEqual(now.plusDays(2))
    }

    fun getEventTimeString(isAllDay: Boolean, startDate: ZonedDateTime, endDate: ZonedDateTime): String {
        if (isAllDay) {
            return "Ganztägig"
        }
        else {
            val startTimeString = formatDate(startDate, "HH:mm", false)
            val endTimeString = formatDate(endDate, "HH:mm", false)
            return "$startTimeString bis $endTimeString Uhr"
        }
    }

    fun getEventEndDateString(startDate: ZonedDateTime, endDate: ZonedDateTime): String? {
        return if (startDate.toLocalDate() == endDate.toLocalDate()) {
            null
        }
        else {
            DateTimeUtil.shared.formatDate(
                endDate,
                "dd. MMM",
                false
            )
        }
    }
}