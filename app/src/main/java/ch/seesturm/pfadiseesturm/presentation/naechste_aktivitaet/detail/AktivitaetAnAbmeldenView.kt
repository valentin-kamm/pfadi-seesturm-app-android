package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.TagFaces
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenu
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenuItem
import ch.seesturm.pfadiseesturm.presentation.common.buttons.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTrailingElementType
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextField
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

@Composable
fun AktivitaetAnAbmeldenView(
    viewModel: AktivitaetDetailViewModel,
    stufe: SeesturmStufe,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    AktivitaetAnAbmeldenContentView(
        stufe = stufe,
        selectedSheetMode = uiState.selectedSheetMode,
        anAbmeldenState = uiState.anAbmeldenState,
        vornameState = uiState.vornameState,
        nachnameState = uiState.nachnameState,
        pfadinameState = uiState.pfadinameState,
        bemerkungState = uiState.bemerkungState,
        onChangeSheetMode = { interaction ->
            viewModel.changeSheetMode(interaction)
        },
        onSubmit = {
            viewModel.sendAnAbmeldung()
        },
        modifier = modifier
    )
}

@Composable
private fun AktivitaetAnAbmeldenContentView(
    stufe: SeesturmStufe,
    selectedSheetMode: AktivitaetInteractionType,
    anAbmeldenState: ActionState<AktivitaetInteractionType>,
    vornameState: SeesturmTextFieldState,
    nachnameState: SeesturmTextFieldState,
    pfadinameState: SeesturmTextFieldState,
    bemerkungState: SeesturmTextFieldState,
    onChangeSheetMode: (AktivitaetInteractionType) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier,
    columnState: LazyListState = rememberLazyListState(),
) {

    val firstTextFieldCount = 3

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
                items = (0..<firstTextFieldCount).toList(),
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
                items = (0..<firstTextFieldCount).toList(),
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
                items = (0..<firstTextFieldCount).toList(),
                index = 2,
                mainContent = FormItemContentType.Custom(
                    content = {
                        SeesturmTextField(
                            state = pfadinameState,
                            leadingIcon = Icons.Outlined.TagFaces,
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
        }
        item {
            BasicListHeader(
                mode = BasicListHeaderMode.Normal("Bemerkung (optional)")
            )
            FormItem(
                items = (0..<1).toList(),
                index = 0,
                mainContent = FormItemContentType.Custom(
                    content = {
                        SeesturmTextField(
                            state = bemerkungState,
                            leadingIcon = Icons.AutoMirrored.Outlined.Comment,
                            singleLine = false,
                            hideLabel = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    },
                    contentPadding = PaddingValues(16.dp)
                )
            )
        }
        item {
            FormItem(
                items = (0..<1).toList(),
                index = 0,
                mainContent = FormItemContentType.Text(
                    title = "An-/Abmeldung"
                ),
                trailingElement = FormItemTrailingElementType.Custom(
                    content = {
                        DropdownButton(
                            title = selectedSheetMode.nomen,
                            contentColor = selectedSheetMode.color,
                            dropdown = { isShown, dismiss ->
                                ThemedDropdownMenu(
                                    expanded = isShown,
                                    onDismissRequest = {
                                        dismiss()
                                    }
                                ) {
                                    stufe.allowedAktivitaetInteractions.forEach { interaction ->
                                        ThemedDropdownMenuItem(
                                            text = {
                                                Text(interaction.nomen)
                                            },
                                            onClick = {
                                                dismiss()
                                                onChangeSheetMode(interaction)
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = interaction.icon,
                                                    contentDescription = null
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                )
            )
        }
        item {
            SeesturmButton(
                type = SeesturmButtonType.Primary(
                    buttonColor = selectedSheetMode.color
                ),
                title = "${selectedSheetMode.nomen} senden",
                isLoading = anAbmeldenState.isLoading,
                onClick = onSubmit
            )
        }
    }
}

@Preview("Loading")
@Composable
private fun AktivitaetAnAbmeldenViewPreview1() {
    PfadiSeesturmTheme {
        AktivitaetAnAbmeldenContentView(
            stufe = SeesturmStufe.Biber,
            selectedSheetMode = AktivitaetInteractionType.ABMELDEN,
            anAbmeldenState = ActionState.Loading(AktivitaetInteractionType.ABMELDEN),
            vornameState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            nachnameState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            pfadinameState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            bemerkungState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            onChangeSheetMode = {},
            onSubmit = {},
            modifier = Modifier
        )
    }
}
@Preview("Error")
@Composable
private fun AktivitaetAnAbmeldenViewPreview2() {
    PfadiSeesturmTheme {
        AktivitaetAnAbmeldenContentView(
            stufe = SeesturmStufe.Biber,
            selectedSheetMode = AktivitaetInteractionType.ABMELDEN,
            anAbmeldenState = ActionState.Idle,
            vornameState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Error("Schwerer Fehler"),
                onValueChanged = {}
            ),
            nachnameState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Error("Schwerer Fehler"),
                onValueChanged = {}
            ),
            pfadinameState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Error("Schwerer Fehler"),
                onValueChanged = {}
            ),
            bemerkungState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Error("Schwerer Fehler"),
                onValueChanged = {}
            ),
            onChangeSheetMode = {},
            onSubmit = {},
            modifier = Modifier
        )
    }
}
@Preview("Idle")
@Composable
private fun AktivitaetAnAbmeldenViewPreview3() {
    PfadiSeesturmTheme {
        AktivitaetAnAbmeldenContentView(
            stufe = SeesturmStufe.Biber,
            selectedSheetMode = AktivitaetInteractionType.ANMELDEN,
            anAbmeldenState = ActionState.Idle,
            vornameState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            nachnameState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            pfadinameState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            bemerkungState = SeesturmTextFieldState(
                text = "",
                label = "Vorname",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            onChangeSheetMode = {},
            onSubmit = {},
            modifier = Modifier
        )
    }
}