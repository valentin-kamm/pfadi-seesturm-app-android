package ch.seesturm.pfadiseesturm.presentation.common.rich_text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.outlined.AddLink
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.isValidUrl
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState

@Composable
fun SeesturmHTMLAddLinkView(
    isShown: Boolean,
    state: RichTextState,
    onDismiss: () -> Unit
) {

    val text = rememberSaveable { mutableStateOf("") }
    val url = rememberSaveable { mutableStateOf("") }
    val isUrlError = rememberSaveable { mutableStateOf(false) }
    
    fun onSubmit() {
        if (url.value.isValidUrl) {
            state.addLink(text.value.trim(), url.value.trim())
            onDismiss()
            text.value = ""
            url.value = ""
        }
        else {
            isUrlError.value = true
        }
    }

    if (isShown) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.AddLink,
                    contentDescription = null,
                    tint = Color.SEESTURM_GREEN
                )
            },
            title = {
                Column {
                    Text(
                        text = "Link einfügen",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    OutlinedTextField(
                        value = text.value,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        onValueChange = {
                            text.value = it
                        },
                        label = { Text("Text") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = null,
                                tint = Color.SEESTURM_GREEN
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = url.value,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        onValueChange = {
                            isUrlError.value = false
                            url.value = it
                        },
                        label = { Text("URL") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = Color.SEESTURM_GREEN
                            )
                        },
                        isError = isUrlError.value,
                        supportingText = {
                            if (isUrlError.value) {
                                Text("Die URL ist ungültig")
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSubmit()
                    },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = Color.SEESTURM_GREEN
                    )
                ) {
                    Text("Einfügen")
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
private fun SeesturmHTMLAddLinkViewPreview() {
    PfadiSeesturmTheme {
        SeesturmHTMLAddLinkView(
            state = rememberRichTextState(),
            isShown = true,
            onDismiss = {}
        )
    }
}