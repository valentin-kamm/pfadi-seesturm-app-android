package ch.seesturm.pfadiseesturm.presentation.account.food

import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.data.data_store.repository.SelectedStufenRepositoryImpl
import ch.seesturm.pfadiseesturm.data.firestore.repository.FirestoreRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.AnlaesseRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.account.service.LeiterbereichService
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AnlaesseRepository
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichState
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichViewModel
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTertiaryElementType
import ch.seesturm.pfadiseesturm.presentation.common.forms.NumberPickerView
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.SeesturmTextField
import ch.seesturm.pfadiseesturm.util.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState

@Composable
fun BestellungHinzufuegenView(
    viewModel: LeiterbereichViewModel,
    modifier: Modifier
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    BestellungHinzufuegenContentView(
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
private fun BestellungHinzufuegenContentView(
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
                            icon = Icons.Default.Fastfood,
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
                trailingElement = FormItemTertiaryElementType.Custom(
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

@Preview
@Composable
private fun BestellungHinzufuegenViewPreview() {
    BestellungHinzufuegenContentView(
        foodItemFieldState = SeesturmTextFieldState(
            text = "",
            label = "Bestellung",
            state = SeesturmBinaryUiState.Success(Unit),
            onValueChanged = {}
        ),
        modifier = Modifier,
        onSubmit = {},
        isButtonLoading = false,
        onNumberPickerValueChange = {}
    )
}