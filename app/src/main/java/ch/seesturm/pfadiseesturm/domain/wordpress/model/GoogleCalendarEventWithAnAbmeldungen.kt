package ch.seesturm.pfadiseesturm.domain.wordpress.model

import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetAnAbmeldung
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import java.time.ZonedDateTime

data class GoogleCalendarEventWithAnAbmeldungen(
    val event: GoogleCalendarEvent,
    val anAbmeldungen: List<AktivitaetAnAbmeldung>
)

fun GoogleCalendarEventWithAnAbmeldungen.displayTextAnAbmeldungen(interaction: AktivitaetInteractionType): String {
    val count = anAbmeldungen.count { it.type == interaction }
    return if (count == 1) {
        "$count ${interaction.nomen}"
    }
    else {
        "$count ${interaction.nomenMehrzahl}"
    }
}

val List<GoogleCalendarEventWithAnAbmeldungen>.groupedByYearAndMonth: List<Pair<ZonedDateTime, List<GoogleCalendarEventWithAnAbmeldungen>>>
    get() = this
        .groupBy { it.event.firstDayOfMonthOfStartDate }
        .toSortedMap(compareByDescending { it })
        .map { (startDay, events) -> startDay to events }