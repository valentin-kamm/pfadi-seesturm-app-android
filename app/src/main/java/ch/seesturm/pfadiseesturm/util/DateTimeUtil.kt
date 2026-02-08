package ch.seesturm.pfadiseesturm.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import com.google.firebase.Timestamp
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class DateTimeUtil {

    companion object {
        val shared: DateTimeUtil by lazy { DateTimeUtil() }
    }

    fun nextSaturday(
        atHour: Int,
        timeZone: ZoneId = ZoneId.of("Europe/Zurich")
    ): ZonedDateTime {

        val now = ZonedDateTime.now(timeZone)

        var nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
        nextSaturday = nextSaturday.withHour(atHour).withMinute(0).withSecond(0).withNano(0)

        return nextSaturday
    }

    fun getIso8601DateString(date: ZonedDateTime, timeZone: ZoneId?): String {

        return timeZone?.let {
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            date.withZoneSameInstant(it).format(formatter)
        } ?: throw PfadiSeesturmError.DateError("Das Datum ist falsch. ISO8601 String kann nicht erstell werden.")
    }

    fun convertFirestoreTimestampToDate(timestamp: Timestamp?): ZonedDateTime {

        if (timestamp != null) {
            return timestamp.toDate().toInstant().atZone(ZoneId.systemDefault())
        }
        throw PfadiSeesturmError.DateError("Datum nicht vorhanden.")
    }

    // function to get the start of the month of the provided date
    fun getFirstDayOfMonthOfADate(date: ZonedDateTime): ZonedDateTime {

        return try {
            date.withDayOfMonth(1).toLocalDate().atStartOfDay(date.zone)
        }
        catch (e: Exception) {
            throw PfadiSeesturmError.DateError("Fehler bei der Datumsverarbeitung.")
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
            throw PfadiSeesturmError.DateError("Datumsformat ungültig.")
        }
    }

    // function to parse a iso date
    fun parseIsoDateWithOffset(iso8601DateString: String): Instant {

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        try {
            return Instant.from(formatter.parse(iso8601DateString))
        }
        catch (e: Exception) {
            throw PfadiSeesturmError.DateError("Datum ungültig.")
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
        // Samstag, 13. Oktober 2025, ganztägig

        // ganztägiges event (mehrere Tag)
        // Samstag, 13. Oktober 2025 bis Sonntag, 14. Oktober 2025, ganztägig

        // nicht-ganztägiges event (1 tag)
        // Samstag, 13. Oktober 2025, 13:00 bis 14:00 Uhr

        // nicht-ganztägiges event (mehrere Tage)
        // Samstag, 13. Oktober 2025, 13:00 Uhr bis Sonntag, 14. Oktober 2025, 14:00 Uhr

        if (endDate < startDate) {
            throw PfadiSeesturmError.DateError("Das Startdatum muss vor dem Enddatum sein.")
        }

        val isSingleDay = startDate.year == endDate.year && startDate.month == endDate.month && startDate.dayOfYear == endDate.dayOfYear

        val firstPart = if (isAllDay && isSingleDay) {
            val formatter = DateTimeFormatter
                .ofPattern("EEEE, dd. MMMM yyyy")
                .withLocale(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                        Locale.of("de", "CH")
                    }
                    else {
                        Locale("de", "CH")
                    }
                )
            formatter.format(startDate) + ", ganztägig"
        }
        else if (isAllDay) {
            val formatter = DateTimeFormatter
                .ofPattern("EEEE, dd. MMMM yyyy")
                .withLocale(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                        Locale.of("de", "CH")
                    }
                    else {
                        Locale("de", "CH")
                    }
                )
            formatter.format(startDate) + " bis "
        }
        else if (isSingleDay) {
            val formatter = DateTimeFormatter
                .ofPattern("EEEE, dd. MMMM yyyy, HH:mm")
                .withLocale(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                        Locale.of("de", "CH")
                    }
                    else {
                        Locale("de", "CH")
                    }
                )
            formatter.format(startDate) + " bis "
        }
        else {
            val formatter = DateTimeFormatter
                .ofPattern("EEEE, dd. MMMM yyyy, HH:mm")
                .withLocale(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                        Locale.of("de", "CH")
                    }
                    else {
                        Locale("de", "CH")
                    }
                )
            formatter.format(startDate) + " Uhr bis "
        }

        val secondPart = if (isAllDay && isSingleDay) {
            ""
        }
        else if (isAllDay) {
            val formatter = DateTimeFormatter
                .ofPattern("EEEE, dd. MMMM yyyy")
                .withLocale(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                        Locale.of("de", "CH")
                    }
                    else {
                        Locale("de", "CH")
                    }
                )
            formatter.format(endDate) + ", ganztägig"
        }
        else if (isSingleDay) {
            val formatter = DateTimeFormatter
                .ofPattern("HH:mm")
                .withLocale(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                        Locale.of("de", "CH")
                    }
                    else {
                        Locale("de", "CH")
                    }
                )
            formatter.format(endDate) + " Uhr"
        }
        else {
            val formatter = DateTimeFormatter
                .ofPattern("EEEE, dd. MMMM yyyy, HH:mm")
                .withLocale(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                        Locale.of("de", "CH")
                    }
                    else {
                        Locale("de", "CH")
                    }
                )
            formatter.format(endDate) + " Uhr"
        }

        return firstPart + secondPart

    }

    fun formatDate(
        date: ZonedDateTime,
        format: String,
        type: DateFormattingType
    ): String {

        val isCandidateForRelativeFormatting = (
                date.isTheDayBeforeYesterday ||
                date.isYesterday ||
                date.isToday ||
                date.isTomorrow ||
                date.isTheDayAfterTomorrow
            )

        val locale = if (Build.VERSION.SDK_INT >= 36) {
            Locale.of("de", "CH")
        }
        else {
            Locale("de", "CH")
        }

        if (type is DateFormattingType.Relative && isCandidateForRelativeFormatting) {

            val dateString =
                if (date.isTheDayBeforeYesterday) {
                    "Vorgestern"
                }
                else if (date.isYesterday) {
                    "Gestern"
                }
                else if (date.isToday) {
                    "Heute"
                }
                else if (date.isTomorrow) {
                    "Morgen"
                }
                else if (date.isTheDayAfterTomorrow) {
                    "Übermorgen"
                }
                else {
                ""
                }

            if (!type.withTime) {
                return dateString
            }

            val timeFormatter = DateTimeFormatter
                .ofPattern("HH:mm")
                .withLocale(locale)
            val timeString = timeFormatter.format(date)

            return "$dateString um $timeString Uhr"
        }

        val formatter = DateTimeFormatter
            .ofPattern(format)
            .withLocale(locale)
        return formatter.format(date)
    }

    private val ZonedDateTime.isTheDayBeforeYesterday: Boolean
        get() {
            val now = ZonedDateTime.now(this.zone).toLocalDate()
            val referenceDate = this.toLocalDate()
            return referenceDate.isEqual(now.minusDays(2))
        }
    private val ZonedDateTime.isYesterday: Boolean
        get() {
            val now = ZonedDateTime.now(this.zone).toLocalDate()
            val referenceDate = this.toLocalDate()
            return referenceDate.isEqual(now.minusDays(1))
        }
    private val ZonedDateTime.isToday: Boolean
        get() {
            val now = ZonedDateTime.now(this.zone).toLocalDate()
            val referenceDate = this.toLocalDate()
            return referenceDate.isEqual(now)
        }
    private val ZonedDateTime.isTomorrow: Boolean
        get() {
            val now = ZonedDateTime.now(this.zone).toLocalDate()
            val referenceDate = this.toLocalDate()
            return referenceDate.isEqual(now.plusDays(1))
        }
    private val ZonedDateTime.isTheDayAfterTomorrow: Boolean
        get() {
            val now = ZonedDateTime.now(this.zone).toLocalDate()
            val referenceDate = this.toLocalDate()
            return referenceDate.isEqual(now.plusDays(2))
        }

    fun getEventTimeString(isAllDay: Boolean, startDate: ZonedDateTime, endDate: ZonedDateTime): String {

        if (isAllDay) {
            return "Ganztägig"
        }

        val startTimeString = formatDate(startDate, "HH:mm", DateFormattingType.Absolute)
        val endTimeString = formatDate(endDate, "HH:mm", DateFormattingType.Absolute)
        return "$startTimeString bis $endTimeString Uhr"
    }

    fun getEventEndDateString(startDate: ZonedDateTime, endDate: ZonedDateTime): String? {
        return if (startDate.toLocalDate() == endDate.toLocalDate()) {
            null
        }
        else {
            shared.formatDate(
                endDate,
                "dd. MMM",
                DateFormattingType.Absolute
            )
        }
    }
}

@Preview
@Composable
private fun DateTimeCheckPreview() {

    val heute = ZonedDateTime.now()
    val heuteSpäter = heute.plusHours(2)
    val vorvorgestern = heute.minusDays(3)
    val vorgestern = heute.minusDays(2)
    val gestern = heute.minusDays(1)
    val morgen = heute.plusDays(1)
    val übermorgen = heute.plusDays(2)
    val überübermorgen = heute.plusDays(3)

    PfadiSeesturmTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(DateTimeUtil.shared.formatDate(
                date = vorvorgestern,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = true)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = vorgestern,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = true)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = gestern,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = true)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = heute,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = true)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = morgen,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = true)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = übermorgen,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = true)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = überübermorgen,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = true)
            ))

            HorizontalDivider()
            
            Text(DateTimeUtil.shared.formatDate(
                date = vorvorgestern,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = false)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = vorgestern,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = false)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = gestern,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = false)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = heute,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = false)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = morgen,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = false)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = übermorgen,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = false)
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = überübermorgen,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Relative(withTime = false)
            ))

            HorizontalDivider()

            Text(DateTimeUtil.shared.formatDate(
                date = vorvorgestern,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Absolute
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = vorgestern,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Absolute
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = gestern,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Absolute
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = heute,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Absolute
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = morgen,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Absolute
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = übermorgen,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Absolute
            ))
            Text(DateTimeUtil.shared.formatDate(
                date = überübermorgen,
                format = "dd.MM.yyyy",
                type = DateFormattingType.Absolute
            ))

            HorizontalDivider()

            Text(DateTimeUtil.shared.formatDateRange(
                startDate = heute,
                endDate = morgen,
                isAllDay = false
            ))
            Text(DateTimeUtil.shared.formatDateRange(
                startDate = heute,
                endDate = morgen,
                isAllDay = true
            ))
            Text(DateTimeUtil.shared.formatDateRange(
                startDate = heute,
                endDate = heuteSpäter,
                isAllDay = false
            ))
            Text(DateTimeUtil.shared.formatDateRange(
                startDate = heute,
                endDate = heuteSpäter,
                isAllDay = true
            ))
        }
    }
}