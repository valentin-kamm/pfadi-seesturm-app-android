package ch.seesturm.pfadiseesturm.presentation.common.event_management

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmRichTextState
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.Binding
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
data class ManageEventState(
    val eventState: UiState<Unit>,
    val publishEventState: ActionState<Unit>,
    val title: SeesturmTextFieldState,
    val location: Binding<String>,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val isAllDay: Binding<Boolean>,
    val showConfirmationDialog: Boolean,
    val previewSheetItem: Binding<GoogleCalendarEvent?>,
    val description: SeesturmRichTextState,
    val templatesState: UiState<List<AktivitaetTemplate>>?,
    val sendPushNotification: Binding<Boolean>?,
    val showTemplatesSheet: Binding<Boolean>?,
    val selectedStufen: Binding<Set<SeesturmStufe>>?,
    val startDatePickerState: DatePickerState,
    val startTimePickerState: TimePickerState,
    val endDatePickerState: DatePickerState,
    val endTimePickerState: TimePickerState
)