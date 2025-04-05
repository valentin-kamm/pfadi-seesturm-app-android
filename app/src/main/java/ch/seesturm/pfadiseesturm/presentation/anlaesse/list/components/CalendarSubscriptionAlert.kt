package ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarType
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import kotlinx.coroutines.launch

@Composable
fun CalendarSubscriptionAlert(
    isShown: Boolean,
    title: String,
    calendar: SeesturmCalendar,
    onDismiss: () -> Unit
) {

    val clipboardManager = LocalClipboardManager.current
    val corountineScope = rememberCoroutineScope()

    if (isShown) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null
                )
            },
            title = {
                Text(text = title)
            },
            text = {
                Text("Auf deinem Gerät ist keine App installiert, die automatisch Webcal-Kalender abonnieren kann.\n\nBitte installiere eine kompatible App oder kopiere die Kalender-URL, um den Feed manuell in deiner bevorzugten Kalender-App hinzuzufügen.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val httpsUrl = calendar.subscriptionUrl.replace("webcal://", "https://")
                        clipboardManager.setText(AnnotatedString(httpsUrl))
                        onDismiss()
                        corountineScope.launch {
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = "URL in Zwischenablage kopiert",
                                    duration = SnackbarDuration.Short,
                                    type = SnackbarType.Info,
                                    allowManualDismiss = true,
                                    onDismiss = {},
                                    showInSheetIfPossible = false
                                )
                            )
                        }
                    }
                ) {
                    Text("URL in Zwischenablage kopieren")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

@Preview
@Composable
fun AktivitaetCalendarSubscriptionAlertPreview() {
    CalendarSubscriptionAlert(
        isShown = true,
        title = "Test",
        calendar = SeesturmCalendar.AKTIVITAETEN_BIBERSTUFE,
        onDismiss = {}
    )
}