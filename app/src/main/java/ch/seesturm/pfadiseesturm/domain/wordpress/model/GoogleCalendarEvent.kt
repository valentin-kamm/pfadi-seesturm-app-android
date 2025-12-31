package ch.seesturm.pfadiseesturm.domain.wordpress.model

import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetAnAbmeldung
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.math.abs

data class GoogleCalendarEvent(
    val id: String,
    val title: String,
    val description: String?,
    val location: String?,
    val created: ZonedDateTime,
    val modified: ZonedDateTime,
    val createdFormatted: String,
    val modifiedFormatted: String,
    val isAllDay: Boolean,
    val firstDayOfMonthOfStartDate: ZonedDateTime,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val startDateFormatted: String,
    val startDayFormatted: String,
    val startMonthFormatted: String,
    val endDateFormatted: String?,
    val timeFormatted: String,
    val fullDateTimeFormatted: String
) {
    val showUpdated: Boolean
        get() = abs(Duration.between(created, modified).toMinutes()) > 5

    val hasEnded: Boolean
        get() = end < ZonedDateTime.now()

    val hasStarted: Boolean
        get() = start <= ZonedDateTime.now()
}

fun GoogleCalendarEvent.toAktivitaetWithAnAbmeldungen(anAbmeldungen: List<AktivitaetAnAbmeldung>): GoogleCalendarEventWithAnAbmeldungen =
    GoogleCalendarEventWithAnAbmeldungen(
        event = this,
        anAbmeldungen = anAbmeldungen.filter { it.eventId == this.id }
    )

val List<GoogleCalendarEvent>.groupedByYearAndMonth: List<Pair<ZonedDateTime, List<GoogleCalendarEvent>>>
    get() = this
        .groupBy { it.firstDayOfMonthOfStartDate }
        .toSortedMap(compareBy { it })
        .map { (startDay, events) -> startDay to events }