package ch.seesturm.pfadiseesturm.presentation.common.alert

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
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
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED

@Composable
fun SimpleAlert(
    isShown: Boolean,
    title: String,
    description: String? = null,
    icon: ImageVector,
    confirmButtonText: String,
    dismissButtonText: String = "Abbrechen",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isConfirmButtonCritical: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    if (isShown) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            icon = {
                Icon(
                    imageVector = icon,
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
                if (description != null) {
                    Text(
                        text = description,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = if (isConfirmButtonCritical) {
                            Color.SEESTURM_RED
                        }
                        else {
                            Color.SEESTURM_GREEN
                        }
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
            containerColor = containerColor
        )
    }
}

@Preview
@Composable
private fun AccountAlertPreview() {
    PfadiSeesturmTheme {
        SimpleAlert(
            isShown = true,
            title = "Titel",
            description = "Beschreibung",
            icon = Icons.Default.AccountBox,
            confirmButtonText = "Löschen",
            dismissButtonText = "Abbrechen",
            onConfirm = {},
            onDismiss = {},
            isConfirmButtonCritical = true
        )
    }
}