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
    val updated: ZonedDateTime,
    val createdFormatted: String,
    val updatedFormatted: String,
    val isAllDay: Boolean,
    val firstDayOfMonthOfStartDate: ZonedDateTime,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val startDayFormatted: String,
    val startMonthFormatted: String,
    val endDateFormatted: String?,
    val timeFormatted: String,
    val fullDateTimeFormatted: String
) {
    val showUpdated: Boolean
        get() = abs(Duration.between(created, updated).toMinutes()) > 2
    val hasEnded: Boolean
        get() = endDate < ZonedDateTime.now()
    val hasStarted: Boolean
        get() = startDate <= ZonedDateTime.now()
}

fun GoogleCalendarEvent.toAktivitaetWithAnAbmeldungen(anAbmeldungen: List<AktivitaetAnAbmeldung>): GoogleCalendarEventWithAnAbmeldungen {
    return GoogleCalendarEventWithAnAbmeldungen(
        event = this,
        anAbmeldungen = anAbmeldungen.filter { it.eventId == this.id }
    )
}

// computed property to group the posts by year
val List<GoogleCalendarEvent>.groupedByYearAndMonth: List<Pair<ZonedDateTime, List<GoogleCalendarEvent>>>
    get() = this
        .groupBy { it.firstDayOfMonthOfStartDate }
        .toSortedMap(compareBy { it })
        .map { (startDay, events) -> startDay to events }