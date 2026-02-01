package ch.seesturm.pfadiseesturm.presentation.common.event_management

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventManagementMode
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventToManageType
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.SeesturmRichTextState
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.Binding
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.UiState
import com.mohamedrejeb.richeditor.model.RichTextState
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
data class ManageEventCommonState (
    val eventState: UiState<Unit>,
    val publishEventState: ActionState<Unit> = ActionState.Idle,
    val title: SeesturmTextFieldState,
    val location: Binding<String>,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val isAllDay: Binding<Boolean>,
    val showConfirmationDialog: Boolean = false,
    val showPreviewSheet: Boolean = false,
    val description: SeesturmRichTextState,
    val startDatePickerState: DatePickerState,
    val startTimePickerState: TimePickerState,
    val endDatePickerState: DatePickerState,
    val endTimePickerState: TimePickerState
) {
    companion object {
        fun create(
            eventType: EventToManageType,
            mode: EventManagementMode,
            onTitleChanged: (String) -> Unit,
            onDescriptionChanged: () -> Unit,
            onLocationChange: (String) -> Unit,
            onAllDayChange: (Boolean) -> Unit
        ): ManageEventCommonState {

            val initialStartDate = when (eventType) {
                is EventToManageType.Aktivitaet, EventToManageType.MultipleAktivitaeten -> DateTimeUtil.shared.nextSaturday(14)
                is EventToManageType.Termin -> ZonedDateTime
                    .now(ZoneId.of("Europe/Zurich"))
                    .truncatedTo(ChronoUnit.HOURS)
                    .plusHours(1)
            }
            val initialEndDate = when (eventType) {
                is EventToManageType.Aktivitaet, EventToManageType.MultipleAktivitaeten -> DateTimeUtil.shared.nextSaturday(16)
                is EventToManageType.Termin -> ZonedDateTime
                    .now(ZoneId.of("Europe/Zurich"))
                    .truncatedTo(ChronoUnit.HOURS)
                    .plusHours(3)
            }

            return ManageEventCommonState(
                eventState = when (mode) {
                    EventManagementMode.Insert -> UiState.Success(Unit)
                    is EventManagementMode.Update -> UiState.Loading
                },
                title = SeesturmTextFieldState(
                    text = when (eventType) {
                        is EventToManageType.Aktivitaet -> eventType.stufe.aktivitaetDescription
                        EventToManageType.MultipleAktivitaeten, is EventToManageType.Termin -> ""
                    },
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = onTitleChanged,
                    label = "Titel"
                ),
                start = initialStartDate,
                end = initialEndDate,
                description = SeesturmRichTextState(
                    state = RichTextState(),
                    onValueChanged = onDescriptionChanged
                ),
                location = Binding(
                    get = { "" },
                    set = { onLocationChange(it) }
                ),
                isAllDay = Binding(
                    get = { false },
                    set = { onAllDayChange(it) }
                ),
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