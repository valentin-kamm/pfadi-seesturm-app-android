package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import ch.seesturm.pfadiseesturm.util.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState
import com.mohamedrejeb.richeditor.model.RichTextState
import java.time.ZonedDateTime

data class AktivitaetBearbeitenState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val aktivitaetState: UiState<Unit>,
    val publishAktivitaetState: ActionState<Unit>,
    val title: SeesturmTextFieldState,
    val description: RichTextState,
    val location: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val sendPushNotification: Boolean = true,
    val showConfirmationDialog: Boolean = false,
    val startDatePickerState: DatePickerState,
    val startTimePickerState: TimePickerState,
    val endDatePickerState: DatePickerState,
    val endTimePickerState: TimePickerState,
) {
    companion object {
        @OptIn(ExperimentalMaterial3Api::class)
        fun create(
            initialAktivitaetState: UiState<Unit>,
            onTitleChanged: (String) -> Unit,
            initialStartDate: ZonedDateTime,
            initialEndDate: ZonedDateTime,
            initialTextFieldText: String = "",
            initialTextFieldState: SeesturmBinaryUiState<Unit> = SeesturmBinaryUiState.Success(Unit)
        ): AktivitaetBearbeitenState {
            return AktivitaetBearbeitenState(
                aktivitaetState = initialAktivitaetState,
                publishAktivitaetState = ActionState.Idle,
                title = SeesturmTextFieldState(
                    text = initialTextFieldText,
                    state = initialTextFieldState,
                    onValueChanged = onTitleChanged,
                    label = "Titel"
                ),
                description = RichTextState(),
                location = initialTextFieldText,
                start = initialStartDate,
                end = initialEndDate,
                sendPushNotification = true,
                showConfirmationDialog = false,
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
                )
            )
        }
    }
}