package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.food

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenu
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenuItem
import ch.seesturm.pfadiseesturm.presentation.common.buttons.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumnItemTrailingContentType
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextField
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState

@Composable
fun AddOrderView(
    foodItemFieldState: SeesturmTextFieldState,
    onSubmit: () -> Unit,
    selectedNumberOfItems: Int,
    onNumberPickerValueChange: (Int) -> Unit,
    isButtonLoading: Boolean,
    modifier: Modifier = Modifier,
    columnState: LazyListState = rememberLazyListState()
) {

    GroupedColumn(
        state = columnState,
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        section {
            item {
                SeesturmTextField(
                    state = foodItemFieldState,
                    leadingIcon = Icons.Default.Fastfood,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            textItem(
                text = "Anzahl",
                trailingContent = GroupedColumnItemTrailingContentType.Custom {
                    DropdownButton(
                        title = "$selectedNumberOfItems",
                        colors = SeesturmButtonColor.Custom(
                            buttonColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentColor = Color.SEESTURM_GREEN
                        ),
                        dropdown = { isShown, dismiss ->
                            ThemedDropdownMenu(
                                expanded = isShown,
                                onDismissRequest = {
                                    dismiss()
                                }
                            ) {
                                (1..10).forEach { anzahl ->
                                    ThemedDropdownMenuItem(
                                        text = {
                                            Text("$anzahl")
                                        },
                                        onClick = {
                                            onNumberPickerValueChange(anzahl)
                                            dismiss()
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            )
        }
        section {
            customItem {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SeesturmButton(
                        type = SeesturmButtonType.Primary,
                        icon = SeesturmButtonIconType.None,
                        isLoading = isButtonLoading,
                        title = "Speichern",
                        onClick = {
                            onSubmit()
                        }
                    )
                }
            }
        }
    }
}

@Preview("Idle", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Idle", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BestellungHinzufuegenViewPreview1() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            AddOrderView(
                foodItemFieldState = SeesturmTextFieldState(
                    text = "Dürüm",
                    label = "Bestellung",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = {}
                ),
                onSubmit = {},
                selectedNumberOfItems = 1,
                onNumberPickerValueChange = {},
                isButtonLoading = false,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}
@Preview("Error", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Error", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BestellungHinzufuegenViewPreview2() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            AddOrderView(
                foodItemFieldState = SeesturmTextFieldState(
                    text = "Dürüm",
                    label = "Bestellung",
                    state = SeesturmBinaryUiState.Error("Schlimmer Fehler"),
                    onValueChanged = {}
                ),
                onSubmit = {},
                selectedNumberOfItems = 1,
                onNumberPickerValueChange = {},
                isButtonLoading = false,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}
@Preview("Loading", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Loading", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BestellungHinzufuegenViewPreview3() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            AddOrderView(
                foodItemFieldState = SeesturmTextFieldState(
                    text = "Dürüm",
                    label = "Bestellung",
                    state = SeesturmBinaryUiState.Success(Unit),
                    onValueChanged = {}
                ),
                onSubmit = {},
                selectedNumberOfItems = 1,
                onNumberPickerValueChange = {},
                isButtonLoading = true,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}