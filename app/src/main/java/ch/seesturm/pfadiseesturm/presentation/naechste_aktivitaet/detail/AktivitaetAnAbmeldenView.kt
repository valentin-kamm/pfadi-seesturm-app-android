package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumnItemTrailingContentType
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextField
import ch.seesturm.pfadiseesturm.presentation.common.textfield.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe


@Composable
fun AktivitaetAnAbmeldenView(
    stufe: SeesturmStufe,
    selectedSheetMode: AktivitaetInteractionType,
    anAbmeldenState: ActionState<AktivitaetInteractionType>,
    vornameState: SeesturmTextFieldState,
    nachnameState: SeesturmTextFieldState,
    pfadinameState: SeesturmTextFieldState,
    bemerkungState: SeesturmTextFieldState,
    onChangeSheetMode: (AktivitaetInteractionType) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    columnState: LazyListState = rememberLazyListState(),
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
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        section(
            header = {
                BasicListHeader(
                    mode = BasicListHeaderMode.Normal("Bemerkung (optional)")
                )
            }
        ) {
            item {
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
            }
        }
        if (stufe.allowedAktivitaetInteractions.count() > 1) {
            section {
                textItem(
                    text = "An-/Abmeldung",
                    trailingContent = GroupedColumnItemTrailingContentType.Custom {
                        DropdownButton(
                            title = selectedSheetMode.nomen,
                            colors = SeesturmButtonColor.Custom(
                                contentColor = selectedSheetMode.color,
                                buttonColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
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
                                                    contentDescription = null,
                                                    tint = interaction.color
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
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
                        colors = SeesturmButtonColor.Custom(
                            buttonColor = selectedSheetMode.color,
                            contentColor = Color.White
                        ),
                        title = "${selectedSheetMode.nomen} senden",
                        isLoading = anAbmeldenState.isLoading,
                        onClick = onSubmit
                    )
                }
            }
        }
    }
}

@Preview("Loading", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Loading", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AktivitaetAnAbmeldenViewPreview1() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            AktivitaetAnAbmeldenView(
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
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}
@Preview("Error", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Error", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AktivitaetAnAbmeldenViewPreview2() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            AktivitaetAnAbmeldenView(
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
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}
@Preview("Idle", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Idle", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AktivitaetAnAbmeldenViewPreview3() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            AktivitaetAnAbmeldenView(
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
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}