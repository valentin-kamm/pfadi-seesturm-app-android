package ch.seesturm.pfadiseesturm.presentation.common

import android.app.TimePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import ch.seesturm.pfadiseesturm.presentation.theme.PfadiSeesturmTheme
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

private typealias year = Int
private typealias month = Int
private typealias dayOfMonth = Int

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeesturmDatePicker(
    isShown: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (year, month, dayOfMonth) -> Unit,
    initialSelectedDate: ZonedDateTime = ZonedDateTime.now(),
    confirmButtonText: String = "OK",
    dismissButtonText: String = "Abbrechen",
    dismissOnClickOutside: Boolean = false,
    dismissOnBackPress: Boolean = false,
    usePlatformDefaultWidth: Boolean = false,
    datePickerState: DatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate.toInstant().toEpochMilli()
    )
) {

    if (isShown) {
        DatePickerDialog(
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.ofEpochMilli(millis)
                            val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
                            onConfirm(zonedDateTime.year, zonedDateTime.monthValue, zonedDateTime.dayOfMonth)
                        }
                        onDismiss()
                    }
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(dismissButtonText)
                }
            },
            properties = DialogProperties(
                dismissOnClickOutside = dismissOnClickOutside,
                dismissOnBackPress = dismissOnBackPress,
                usePlatformDefaultWidth = usePlatformDefaultWidth
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SeesturmDatePickerPreview() {
    PfadiSeesturmTheme {
        SeesturmDatePicker(
            isShown = true,
            onDismiss = {},
            onConfirm = { _, _, _ -> }
        )
    }
}