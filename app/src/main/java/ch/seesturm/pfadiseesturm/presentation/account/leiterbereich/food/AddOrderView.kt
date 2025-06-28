package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichViewModel
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTrailingElementType
import ch.seesturm.pfadiseesturm.presentation.common.picker.NumberPickerView
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextField
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState

@Composable
fun AddOrderView(
    viewModel: LeiterbereichViewModel,
    modifier: Modifier
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    AddOrderContentView(
        foodItemFieldState = uiState.foodItemState,
        modifier = modifier,
        onSubmit = {
            viewModel.addNewFoodOrder()
        },
        isButtonLoading = uiState.addNewOrderState.isLoading,
        onNumberPickerValueChange = { newValue ->
            viewModel.updateFoodItemCount(newValue)
        }
    )
}

@Composable
private fun AddOrderContentView(
    foodItemFieldState: SeesturmTextFieldState,
    onSubmit: () -> Unit,
    onNumberPickerValueChange: (Int) -> Unit,
    isButtonLoading: Boolean,
    modifier: Modifier,
    columnState: LazyListState = rememberLazyListState()
) {
    
    val textFieldCount = 2

    LazyColumn(
        state = columnState,
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            FormItem(
                items = (0..<textFieldCount).toList(),
                index = 0,
                mainContent = FormItemContentType.Custom(
                    content = {
                        SeesturmTextField(
                            state = foodItemFieldState,
                            leadingIcon = Icons.Default.Fastfood,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    },
                    contentPadding = PaddingValues(16.dp)
                )
            )
            FormItem(
                items = (0..<textFieldCount).toList(),
                index = 1,
                mainContent = FormItemContentType.Text(
                    title = "Anzahl"
                ),
                trailingElement = FormItemTrailingElementType.Custom(
                    content = {
                        NumberPickerView(
                            minValue = 1,
                            maxValue = 10,
                            initialValue = 1
                        ) { value ->
                            onNumberPickerValueChange(value)
                        }
                    }
                )
            )
        }
        item {
            SeesturmButton(
                type = SeesturmButtonType.Primary(
                    icon = SeesturmButtonIconType.None
                ),
                isLoading = isButtonLoading,
                title = "Speichern",
                onClick = {
                    onSubmit()
                }
            )
        }
    }
}

@Preview("Idle")
@Composable
private fun BestellungHinzufuegenViewPreview1() {
    PfadiSeesturmTheme {
        AddOrderContentView(
            foodItemFieldState = SeesturmTextFieldState(
                text = "Dürüm",
                label = "Bestellung",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            onSubmit = {},
            onNumberPickerValueChange = {},
            isButtonLoading = false,
            modifier = Modifier
        )
    }
}
@Preview("Error")
@Composable
private fun BestellungHinzufuegenViewPreview2() {
    PfadiSeesturmTheme {
        AddOrderContentView(
            foodItemFieldState = SeesturmTextFieldState(
                text = "Dürüm",
                label = "Bestellung",
                state = SeesturmBinaryUiState.Error("Schlimmer Fehler"),
                onValueChanged = {}
            ),
            onSubmit = {},
            onNumberPickerValueChange = {},
            isButtonLoading = false,
            modifier = Modifier
        )
    }
}
@Preview("Loading")
@Composable
private fun BestellungHinzufuegenViewPreview3() {
    PfadiSeesturmTheme {
        AddOrderContentView(
            foodItemFieldState = SeesturmTextFieldState(
                text = "Dürüm",
                label = "Bestellung",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            onSubmit = {},
            onNumberPickerValueChange = {},
            isButtonLoading = true,
            modifier = Modifier
        )
    }
}