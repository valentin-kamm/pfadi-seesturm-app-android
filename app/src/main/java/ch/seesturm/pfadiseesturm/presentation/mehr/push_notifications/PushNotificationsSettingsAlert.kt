package ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PushNotificationsSettingsAlert(
    isShown: Boolean,
    onDismiss: () -> Unit
) {

    val activity = LocalActivity.current

    if (isShown) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.NotificationsOff,
                    contentDescription = null
                )
            },
            title = {
                Text(text = "Push-Nachrichten nicht aktiviert")
            },
            text = {
                Text("Um diese Funktion nutzen zu können, musst du Push-Nachrichten in den Einstellungen aktivieren.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        activity?.openAppSettings()
                    }
                ) {
                    Text("Einstellungen")
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

private fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

@Preview
@Composable
private fun PushNotificationsSettingsAlertPreview() {
    PushNotificationsSettingsAlert(
        isShown = true,
        onDismiss = {}
    )
}