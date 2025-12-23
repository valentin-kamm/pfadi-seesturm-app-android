package ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.TagFaces
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListFooter
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextField
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState

@Composable
fun GespeichertePersonHinzufuegenView(
    vornameState: SeesturmTextFieldState,
    nachnameState: SeesturmTextFieldState,
    pfadinameState: SeesturmTextFieldState,
    onInsert: () -> Unit,
    modifier: Modifier,
    columnState: LazyListState = rememberLazyListState()
) {

    val textFieldCount = 3

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
                            state = vornameState,
                            leadingIcon = Icons.Outlined.AccountBox,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
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
                mainContent = FormItemContentType.Custom(
                    content = {
                        SeesturmTextField(
                            state = nachnameState,
                            leadingIcon = Icons.Filled.AccountBox,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
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
                index = 2,
                mainContent = FormItemContentType.Custom(
                    content = {
                        SeesturmTextField(
                            state = pfadinameState,
                            leadingIcon = Icons.Outlined.TagFaces,
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
            BasicListFooter(
                mode = BasicListHeaderMode.Normal(
                    "Füge die Angaben von Personen hinzu, die du of von Aktivitäten abmeldest. So musst du sie nicht jedes Mal neu eintragen."
                ),
                maxLines = Int.MAX_VALUE
            )
        }
        item {
            SeesturmButton(
                type = SeesturmButtonType.Primary(
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Default.PersonAddAlt
                    )
                ),
                title = "Speichern",
                onClick = onInsert,
                isLoading = false
            )
        }
    }
}

@Preview("Error")
@Composable
private fun GespeichertePersonHinzufuegenViewPreview1() {
    PfadiSeesturmTheme {
        GespeichertePersonHinzufuegenView(
            vornameState = SeesturmTextFieldState(
                text = "Sepp",
                label = "Vorname",
                state = SeesturmBinaryUiState.Error("Fehler"),
                onValueChanged = {}
            ),
            nachnameState = SeesturmTextFieldState(
                text = "Müller",
                label = "Nachname",
                state = SeesturmBinaryUiState.Error("Fehler"),
                onValueChanged = {}
            ),
            pfadinameState = SeesturmTextFieldState(
                text = "Quasli",
                label = "Pfadiname",
                state = SeesturmBinaryUiState.Error("Fehler"),
                onValueChanged = {}
            ),
            onInsert = {},
            modifier = Modifier
        )
    }
}
@Preview("Normal")
@Composable
private fun GespeichertePersonHinzufuegenViewPreview2() {
    PfadiSeesturmTheme {
        GespeichertePersonHinzufuegenView(
            vornameState = SeesturmTextFieldState(
                text = "Sepp",
                label = "Vorname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            nachnameState = SeesturmTextFieldState(
                text = "Müller",
                label = "Nachname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            pfadinameState = SeesturmTextFieldState(
                text = "Quasli",
                label = "Pfadiname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            onInsert = {},
            modifier = Modifier
        )
    }
}