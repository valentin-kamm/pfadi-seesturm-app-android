package ch.seesturm.pfadiseesturm.presentation.common.textfield

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState

@Composable
fun SeesturmTextField(
    state: SeesturmTextFieldState,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    hideLabel: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    
    OutlinedTextField(
        value = state.text,
        onValueChange = {
           state.onValueChanged(it)
        },
        label = if (!hideLabel) {
            { Text(state.label) }
        }
        else {
            null
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color.SEESTURM_GREEN
            )
        },
        singleLine = singleLine,
        isError = state.state.isError,
        supportingText = state.state.errorText,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors().copy(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = Color.SEESTURM_RED,
            errorLabelColor = Color.SEESTURM_RED,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            focusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            errorIndicatorColor = Color.SEESTURM_RED,
            focusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            errorContainerColor = MaterialTheme.colorScheme.primaryContainer,
            errorSupportingTextColor = Color.SEESTURM_RED
        )
    )
}

@Preview("Normal", showBackground = true)
@Composable
private fun SeesturmTextFieldPreview1() {
    PfadiSeesturmTheme {
        SeesturmTextField(
            state = SeesturmTextFieldState(
                text = "",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {},
                label = "Vorname"
            ),
            leadingIcon = Icons.Outlined.AccountBox
        )
    }
}

@Preview("Error")
@Composable
private fun SeesturmTextFieldPreview2() {
    PfadiSeesturmTheme {
        SeesturmTextField(
            state = SeesturmTextFieldState(
                text = "",
                state = SeesturmBinaryUiState.Error("Fehler"),
                onValueChanged = {},
                label = "Vorname"
            ),
            leadingIcon = Icons.Outlined.AccountBox
        )
    }
}