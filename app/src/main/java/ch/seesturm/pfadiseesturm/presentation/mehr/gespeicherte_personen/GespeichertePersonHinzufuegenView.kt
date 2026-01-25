package ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListFooter
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
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

    GroupedColumn(
        state = columnState,
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        section(
            footer = {
                BasicListFooter(
                    mode = BasicListHeaderMode.Normal(
                        "Füge die Angaben von Personen hinzu, die du oft von Aktivitäten abmeldest. So musst du sie nicht jedes Mal neu eintragen."
                    ),
                    maxLines = Int.MAX_VALUE
                )
            }
        ) {
            item {
                SeesturmTextField(
                    state = vornameState,
                    leadingIcon = Icons.Outlined.AccountBox,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            item {
                SeesturmTextField(
                    state = nachnameState,
                    leadingIcon = Icons.Filled.AccountBox,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            item {
                SeesturmTextField(
                    state = pfadinameState,
                    leadingIcon = Icons.Outlined.TagFaces,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
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
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Default.PersonAddAlt
                        ),
                        title = "Speichern",
                        onClick = onInsert,
                        isLoading = false
                    )
                }
            }
        }
    }
}

@Preview("Error", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Error", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GespeichertePersonHinzufuegenViewPreview1() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
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
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}
@Preview("Normal", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Normal", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GespeichertePersonHinzufuegenViewPreview2() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
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
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}