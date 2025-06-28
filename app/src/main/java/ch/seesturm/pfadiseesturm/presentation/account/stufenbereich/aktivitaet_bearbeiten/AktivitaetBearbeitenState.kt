package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmRichTextState
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState
import com.mohamedrejeb.richeditor.model.RichTextState
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
data class AktivitaetBearbeitenState (
    val aktivitaetState: UiState<Unit>,
    val publishAktivitaetState: ActionState<Unit>,
    val title: SeesturmTextFieldState,
    val description: SeesturmRichTextState,
    val location: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val sendPushNotification: Boolean = true,
    val showConfirmationDialog: Boolean = false,
    val startDatePickerState: DatePickerState,
    val startTimePickerState: TimePickerState,
    val endDatePickerState: DatePickerState,
    val endTimePickerState: TimePickerState,
    val templatesState: UiState<List<AktivitaetTemplate>>
) {
    companion object {
        fun create(
            initialAktivitaetState: UiState<Unit>,
            onTitleChanged: (String) -> Unit,
            stufe: SeesturmStufe,
            onDescriptionChanged: () -> Unit,
            initialStartDate: ZonedDateTime = DateTimeUtil.shared.nextSaturday(14),
            initialEndDate: ZonedDateTime = DateTimeUtil.shared.nextSaturday(16)
        ): AktivitaetBearbeitenState {

            return AktivitaetBearbeitenState(
                aktivitaetState = initialAktivitaetState,
                publishAktivitaetState = ActionState.Idle,
                title = SeesturmTextFieldState(
                    text = stufe.aktivitaetDescription,
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onTitleChanged,
                    label = "Titel"
                ),
                description = SeesturmRichTextState(
                    state = RichTextState(),
                    onValueChanged = onDescriptionChanged
                ),
                location = "",
                start = initialStartDate,
                end = initialEndDate,
                startDatePickerState = DatePickerState(
                    initialSelectedDateMillis = initialStartDate.toInstant().toEpochMilli(),
                    locale = CalendarLocale.getDefault()
                ),
                startTimePickerState = TimePickerState(
                    initialHour = initialStartDate.hour,
                    initialMinute = initialStartDate.minute,
                    is24Hour = true
                ),
                endDatePickerState = DatePickerState(
                    initialSelectedDateMillis = initialEndDate.toInstant().toEpochMilli(),
                    locale = CalendarLocale.getDefault()
                ),
                endTimePickerState = TimePickerState(
                    initialHour = initialEndDate.hour,
                    initialMinute = initialEndDate.minute,
                    is24Hour = true
                ),
                templatesState = UiState.Loading
            )
        }
    }
}