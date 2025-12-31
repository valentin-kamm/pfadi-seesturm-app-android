package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich

import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetAnAbmeldung
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class StufenbereichState(
    // load data
    val aktivitaetenState: UiState<List<GoogleCalendarEvent>> = UiState.Loading,
    val anAbmeldungenState: UiState<List<AktivitaetAnAbmeldung>> = UiState.Loading,

    // actions on single events
    val deleteAbmeldungenState: ActionState<GoogleCalendarEventWithAnAbmeldungen> = ActionState.Idle,
    val sendPushNotificationState: ActionState<GoogleCalendarEventWithAnAbmeldungen> = ActionState.Idle,
    val showDeleteAbmeldungenAlert: GoogleCalendarEventWithAnAbmeldungen? = null,
    val showSendPushNotificationAlert: GoogleCalendarEventWithAnAbmeldungen? = null,

    // global actions
    val deleteAllAbmeldungenState: ActionState<Unit> = ActionState.Idle,
    val showDeleteAllAbmeldungenAlert: Boolean = false,

    // other state
    val refreshing: Boolean = false,
    val selectedDate: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()).minusMonths(3).with(LocalTime.MIDNIGHT)
)
