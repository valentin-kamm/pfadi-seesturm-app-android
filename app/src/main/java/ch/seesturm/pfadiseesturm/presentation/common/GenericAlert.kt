package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun <A> GenericAlert(
    action: A?,
    title: String,
    description: String? = null,
    icon: ImageVector,
    confirmButtonText: String,
    dismissButtonText: String = "Abbrechen",
    onConfirm: (A) -> Unit,
    onDismiss: () -> Unit
) {
    if (action != null) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            },
            title = {
                Text(text = title)
            },
            text = {
                if (description != null) {
                    Text(text = description)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(action)
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
            }
        )
    }
}

@Composable
fun SimpleAlert(
    isShown: Boolean,
    title: String,
    description: String? = null,
    icon: ImageVector,
    confirmButtonText: String,
    dismissButtonText: String = "Abbrechen",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isShown) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            },
            title = {
                Text(text = title)
            },
            text = {
                if (description != null) {
                    Text(text = description)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
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
            }
        )
    }
}

@Preview
@Composable
private fun AccountAlertPreview() {
    SimpleAlert(
        isShown = true,
        title = "Titel",
        description = "Beschreibung",
        icon = Icons.Default.AccountBox,
        confirmButtonText = "Löschen",
        dismissButtonText = "Abbrechen",
        onConfirm = {},
        onDismiss = {}
    )
}