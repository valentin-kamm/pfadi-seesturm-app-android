package ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarType
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import kotlinx.coroutines.launch

@Composable
fun CalendarSubscriptionAlert(
    isShown: Boolean,
    title: String,
    calendar: SeesturmCalendar,
    onDismiss: () -> Unit
) {

    val clipboardManager = LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val corountineScope = rememberCoroutineScope()

    if (isShown) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = Color.SEESTURM_GREEN
                )
            },
            title = {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Text(
                    text = "Auf deinem Gerät ist keine App installiert, die automatisch Webcal-Kalender abonnieren kann.\n\nBitte installiere eine kompatible App oder kopiere die Kalender-URL, um den Feed manuell in deiner bevorzugten Kalender-App hinzuzufügen.",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val httpsUrl = calendar.subscriptionUrl.replace("webcal://", "https://")
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("Kalender URL", httpsUrl))
                        onDismiss()
                        corountineScope.launch {
                            SnackbarController.sendEvent(
                                event = SeesturmSnackbarEvent(
                                    message = "URL in Zwischenablage kopiert",
                                    duration = SnackbarDuration.Short,
                                    type = SeesturmSnackbarType.Info,
                                    allowManualDismiss = true,
                                    onDismiss = {},
                                    showInSheetIfPossible = false
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = Color.SEESTURM_GREEN
                    )
                ) {
                    Text("URL in Zwischenablage kopieren")
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
                    Text("Abbrechen")
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Preview
@Composable
fun AktivitaetCalendarSubscriptionAlertPreview() {
    PfadiSeesturmTheme {
        CalendarSubscriptionAlert(
            isShown = true,
            title = "Test",
            calendar = SeesturmCalendar.AKTIVITAETEN_BIBERSTUFE,
            onDismiss = {}
        )
    }
}