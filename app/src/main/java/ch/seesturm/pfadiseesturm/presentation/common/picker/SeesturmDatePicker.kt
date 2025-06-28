package ch.seesturm.pfadiseesturm.presentation.common.picker

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import java.time.Instant
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
    modifier: Modifier = Modifier,
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
            modifier = modifier,
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
                    },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = Color.SEESTURM_GREEN
                    )
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = Color.SEESTURM_GREEN
                    )
                ) {
                    Text(dismissButtonText)
                }
            },
            properties = DialogProperties(
                dismissOnClickOutside = dismissOnClickOutside,
                dismissOnBackPress = dismissOnBackPress,
                usePlatformDefaultWidth = usePlatformDefaultWidth
            ),
            colors = DatePickerDefaults.colors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors().copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    headlineContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
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