package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

sealed class AktivitaetDetailViewLocation(
    open val getAktivitaet: suspend () -> SeesturmResult<GoogleCalendarEvent?, DataError.Network>,
    open val eventId: String?
) {
    data class Home(
        override val getAktivitaet: suspend () -> SeesturmResult<GoogleCalendarEvent?, DataError.Network>,
        override val eventId: String?,
        val onNavigateToPushNotifications: () -> Unit,
        val onNavigateToGespeichertePersonen: () -> Unit,
    ) : AktivitaetDetailViewLocation(getAktivitaet, eventId)

    data class Stufenbereich(
        override val eventId: String?,
        override val getAktivitaet: suspend () -> SeesturmResult<GoogleCalendarEvent?, DataError.Network>
    ) : AktivitaetDetailViewLocation(getAktivitaet, eventId)
}