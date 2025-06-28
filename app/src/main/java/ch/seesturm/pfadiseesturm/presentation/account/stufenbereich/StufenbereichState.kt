package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich

import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetAnAbmeldung
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import java.time.ZoneId
import java.time.ZonedDateTime

data class StufenbereichState(
    val anAbmeldungenState: UiState<List<AktivitaetAnAbmeldung>> = UiState.Loading,
    val aktivitaetenState: UiState<List<GoogleCalendarEvent>> = UiState.Loading,
    val deleteAbmeldungenState: ActionState<GoogleCalendarEventWithAnAbmeldungen> = ActionState.Idle,
    val deleteAllAbmeldungenState: ActionState<Unit> = ActionState.Idle,
    val sendPushNotificationState: ActionState<GoogleCalendarEventWithAnAbmeldungen> = ActionState.Idle,
    val refreshing: Boolean = false,
    val selectedAktivitaetInteraction: AktivitaetInteractionType = AktivitaetInteractionType.ABMELDEN,
    val selectedDate: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()).minusMonths(3),
    val showDeleteAllAbmeldungenAlert: Boolean = false,
    val showDeleteAbmeldungenAlert: GoogleCalendarEventWithAnAbmeldungen? = null,
    val showSendPushNotificationAlert: GoogleCalendarEventWithAnAbmeldungen? = null
)
