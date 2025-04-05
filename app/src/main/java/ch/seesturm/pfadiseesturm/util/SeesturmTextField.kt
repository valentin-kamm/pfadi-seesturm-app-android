package ch.seesturm.pfadiseesturm.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState

@Composable
fun SeesturmTextField(
    state: SeesturmTextFieldState,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    hideLabel: Boolean = false,
    enabled: Boolean = true
) {
    
    OutlinedTextField(
        value = state.text,
        onValueChange = {
           state.onValueChanged(it)
        },
        label = {
            if (!hideLabel) {
                Text(state.label)
            }
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.SEESTURM_GREEN
            )
        },
        singleLine = singleLine,
        isError = state.state.isError,
        supportingText = state.state.errorText,
        enabled = enabled,
        modifier = modifier
    )
}

@Preview("Normal")
@Composable
private fun SeesturmTextFieldPreview1() {
    SeesturmTextField(
        state = SeesturmTextFieldState(
            text = "",
            state = SeesturmBinaryUiState.Success(Unit),
            onValueChanged = {},
            label = "Vorname"
        ),
        icon = Icons.Outlined.AccountBox
    )
}
@Preview("Error")
@Composable
private fun SeesturmTextFieldPreview2() {
    SeesturmTextField(
        state = SeesturmTextFieldState(
            text = "",
            state = SeesturmBinaryUiState.Error("Fehler"),
            onValueChanged = {},
            label = "Vorname"
        ),
        icon = Icons.Outlined.AccountBox
    )
}