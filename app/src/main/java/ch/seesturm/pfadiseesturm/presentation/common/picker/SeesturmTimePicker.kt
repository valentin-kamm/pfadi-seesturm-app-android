package ch.seesturm.pfadiseesturm.presentation.common.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import java.time.ZonedDateTime

private typealias hour = Int
private typealias minute = Int

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeesturmTimePicker(
    isShown: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (hour, minute) -> Unit,
    modifier: Modifier = Modifier,
    initialSelectedHour: hour = ZonedDateTime.now().hour,
    initialSelectedMinute: minute = ZonedDateTime.now().minute,
    confirmButtonText: String = "OK",
    dismissButtonText: String = "Abbrechen",
    dismissOnClickOutside: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    dismissOnBackPress: Boolean = false,
    usePlatformDefaultWidth: Boolean = false,
    tonalElevation: Dp = DatePickerDefaults.TonalElevation,
    state: TimePickerState = rememberTimePickerState(
        initialHour = initialSelectedHour,
        initialMinute = initialSelectedMinute,
        is24Hour = true
    )
) {

    if (isShown) {
        BasicAlertDialog(
            onDismissRequest = onDismiss,
            modifier = modifier,
            properties = DialogProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside,
                usePlatformDefaultWidth = usePlatformDefaultWidth
            )
        ) {
            Surface(
                shape = DatePickerDefaults.shape,
                tonalElevation = tonalElevation,
                color = containerColor,
                modifier = modifier
                    .wrapContentSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                    ) {
                        TimeInput(
                            state = state,
                            colors = TimePickerDefaults.colors().copy(
                                timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onBackground,
                                timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(PaddingValues(bottom = 8.dp, end = 6.dp))
                    ) {
                        Row {
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
                            TextButton(
                                onClick = {
                                    onConfirm(state.hour, state.minute)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.textButtonColors().copy(
                                    contentColor = Color.SEESTURM_GREEN
                                )
                            ) {
                                Text(confirmButtonText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SeesturmTimePickerPreview() {
    PfadiSeesturmTheme {
        SeesturmTimePicker(
            isShown = true,
            onDismiss = {},
            onConfirm = { _, _ -> },
            initialSelectedHour = 20,
            initialSelectedMinute = 15
        )
    }
}