package ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN

@Composable
fun AlertWithSettingsAction(
    isShown: Boolean,
    type: AlertWithSettingsActionType,
    onDismiss: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer
) {

    val activity = LocalActivity.current

    if (isShown) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            icon = {
                Icon(
                    imageVector = type.icon,
                    contentDescription = null,
                    tint = Color.SEESTURM_GREEN
                )
            },
            title = {
                Text(
                    text = type.title,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Text(
                    text = type.description,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        activity?.openAppSettings()
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = Color.SEESTURM_GREEN
                    )
                ) {
                    Text("Einstellungen")
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
            containerColor = containerColor
        )
    }
}

sealed class AlertWithSettingsActionType {
    data object Notifications: AlertWithSettingsActionType()
    data object Location: AlertWithSettingsActionType()

    val icon: ImageVector
        get() {
            return when (this) {
                Location -> Icons.Outlined.LocationOn
                Notifications -> Icons.Outlined.NotificationsOff
            }
        }
    val title: String
        get() {
            return when (this) {
                Location -> "Ortungsdienste nicht aktiviert"
                Notifications -> "Push-Nachrichten nicht aktiviert"
            }
        }
    val description: String
        get() {
            return when (this) {
                Location -> "Um diese Funktion nutzen zu können, musst du die Ortungsdienste in den Einstellungen aktivieren."
                Notifications -> "Um diese Funktion nutzen zu können, musst du Push-Nachrichten in den Einstellungen aktivieren."
            }
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
    PfadiSeesturmTheme {
        AlertWithSettingsAction(
            isShown = true,
            type = AlertWithSettingsActionType.Notifications,
            onDismiss = {}
        )
    }
}