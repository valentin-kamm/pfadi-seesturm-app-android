package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.templates

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumnItemActionIcon
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumnItemSwipeMode
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.HtmlTextView
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun TemplateListView(
    state: UiState<List<AktivitaetTemplate>>,
    mode: TemplateListViewMode,
    contentPadding: PaddingValues,
    onClick: (AktivitaetTemplate) -> Unit,
    isInEditingMode: Boolean,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {

    val isSwipeableFormItemEnabled: Boolean = when (mode) {
        is TemplateListViewMode.Edit -> {
            isInEditingMode && !mode.deleteState.isLoading && !mode.editState.isLoading
        }
        TemplateListViewMode.Use -> false
    }

    GroupedColumn(
        state = listState,
        userScrollEnabled = !state.scrollingDisabled,
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxSize()
    ) {
        section {
            when (state) {
                UiState.Loading -> {
                    items(
                        count = 3,
                        key = { index ->
                            "TemplateListViewLoadingCell$index"
                        }
                    ) {
                        RedactedText(
                            numberOfLines = 5,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        )
                    }
                }
                is UiState.Error -> {
                    customItem(
                        key = "TemplateListViewErrorCell"
                    ) {
                        ErrorCardView(
                            errorDescription = state.message
                        )
                    }
                }
                is UiState.Success -> {
                    if (state.data.isNotEmpty()) {
                        itemsIndexed(
                            items = state.data.sortedByDescending { it.created },
                            key = { _, template ->
                                "TemplateListViewCell${template.id}"
                            },
                            swipeMode = { _, template ->
                                if (isSwipeableFormItemEnabled) {
                                    GroupedColumnItemSwipeMode.Enabled(
                                        isRevealed = state.data.first { it.id == template.id}.swipeActionsRevealed,
                                        actions = {
                                            GroupedColumnItemActionIcon(
                                                onClick = {
                                                    if (mode is TemplateListViewMode.Edit) {
                                                        mode.onDeleteItem(template)
                                                    }
                                                },
                                                backgroundColor = Color.SEESTURM_RED,
                                                icon = Icons.Filled.Delete,
                                                iconTint = Color.White
                                            )
                                        },
                                        onExpand = {
                                            if (mode is TemplateListViewMode.Edit) {
                                                mode.setSwipeActionsRevealed(template, true)
                                            }
                                        },
                                        onCollapse = {
                                            if (mode is TemplateListViewMode.Edit) {
                                                mode.setSwipeActionsRevealed(template, false)
                                            }
                                        }
                                    )
                                }
                                else {
                                    GroupedColumnItemSwipeMode.Disabled
                                }
                            },
                            onClick = { _, template ->
                                onClick(template)
                            }
                        ) { _, template ->
                            HtmlTextView(
                                html = template.description,
                                openLinks = false
                            )
                        }
                    }
                    else {
                        item(
                            key = "NoTemplatesListViewErrorCell"
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 24.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.NoteAdd,
                                    contentDescription = null,
                                    tint = Color.SEESTURM_GREEN,
                                    modifier = Modifier
                                        .size(50.dp)
                                )
                                Text(
                                    text = "Keine Vorlagen",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                if (mode is TemplateListViewMode.Edit) {
                                    Text(
                                        text = "Füge jetzt eine Vorlage hinzu, damit das Erstellen von Aktivitäten schneller geht.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    SeesturmButton(
                                        type = SeesturmButtonType.Primary,
                                        title = "Vorlage hinzufügen",
                                        onClick = mode.onAddClick,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview("Loading", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Loading", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemplateListViewPreview1() {
    PfadiSeesturmTheme {
        TemplateListView(
            state = UiState.Loading,
            mode = TemplateListViewMode.Use,
            contentPadding = PaddingValues(16.dp),
            onClick = {},
            isInEditingMode = false,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

@Preview("Loading in Sheet", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Loading in Sheet", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemplateListViewPreview2() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            TemplateListView(
                state = UiState.Loading,
                mode = TemplateListViewMode.Use,
                contentPadding = PaddingValues(16.dp),
                onClick = {},
                isInEditingMode = false,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}

@Preview("Error", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Error", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemplateListViewPreview3() {
    PfadiSeesturmTheme {
        TemplateListView(
            state = UiState.Error("Ein ganz schlimmer Fehler"),
            mode = TemplateListViewMode.Use,
            contentPadding = PaddingValues(16.dp),
            onClick = {},
            isInEditingMode = false,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

@Preview("Error in Sheet", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Error in Sheet", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemplateListViewPreview4() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            TemplateListView(
                state = UiState.Error("Ein ganz schlimmer Fehler"),
                mode = TemplateListViewMode.Use,
                contentPadding = PaddingValues(16.dp),
                onClick = {},
                isInEditingMode = false,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}

@Preview("Success", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Success", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemplateListViewPreview5() {
    PfadiSeesturmTheme {
        TemplateListView(
            state = UiState.Success(
                data = listOf(
                    DummyData.aktivitaetTemplate1,
                    DummyData.aktivitaetTemplate2
                )
            ),
            mode = TemplateListViewMode.Use,
            contentPadding = PaddingValues(16.dp),
            onClick = {},
            isInEditingMode = false,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

@Preview("Success in Sheet", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Success in Sheet", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemplateListViewPreview6() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            TemplateListView(
                state = UiState.Success(
                    data = listOf(
                        DummyData.aktivitaetTemplate1,
                        DummyData.aktivitaetTemplate2
                    )
                ),
                mode = TemplateListViewMode.Use,
                contentPadding = PaddingValues(16.dp),
                onClick = {},
                isInEditingMode = false,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}

@Preview("Empty", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Empty", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemplateListViewPreview7() {
    PfadiSeesturmTheme {
        TemplateListView(
            state = UiState.Success(
                data = listOf()
            ),
            mode = TemplateListViewMode.Edit(
                onAddClick = {},
                editState = ActionState.Idle,
                deleteState = ActionState.Idle,
                setSwipeActionsRevealed = { _, _ -> },
                onDeleteItem = {}
            ),
            contentPadding = PaddingValues(16.dp),
            onClick = {},
            isInEditingMode = false,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

@Preview("Empty in sheet", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("Empty in sheet", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemplateListViewPreview8() {
    CompositionLocalProvider(
        LocalScreenContext provides ScreenContext.ModalBottomSheet
    ) {
        PfadiSeesturmTheme {
            TemplateListView(
                state = UiState.Success(
                    data = listOf()
                ),
                mode = TemplateListViewMode.Edit(
                    onAddClick = {},
                    editState = ActionState.Idle,
                    deleteState = ActionState.Idle,
                    setSwipeActionsRevealed = { _, _ -> },
                    onDeleteItem = {}
                ),
                contentPadding = PaddingValues(16.dp),
                onClick = {},
                isInEditingMode = false,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}