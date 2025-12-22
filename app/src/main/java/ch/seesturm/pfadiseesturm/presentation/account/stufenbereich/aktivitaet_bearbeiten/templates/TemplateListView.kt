package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemActionIcon
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.SwipeableFormItem
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.HtmlTextView
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
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

    LazyColumn(
        state = listState,
        userScrollEnabled = !state.scrollingDisabled,
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxSize()
    ) {

        when (state) {
            UiState.Loading -> {
                items(
                    count = 3,
                    key = { index ->
                        "TemplateListViewLoadingCell$index"
                    }
                ) { index ->
                    FormItem(
                        items = (0..<3).toList(),
                        index = index,
                        modifier = Modifier
                            .animateItem(),
                        mainContent = FormItemContentType.Custom(
                            content = {
                                RedactedText(
                                    numberOfLines = 5,
                                    textStyle = MaterialTheme.typography.bodyLarge
                                )
                            },
                            contentPadding = PaddingValues(16.dp)
                        )
                    )
                }
            }
            is UiState.Error -> {
                item(key = "TemplateListViewErrorCell") {
                    ErrorCardView(
                        errorDescription = state.message,
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
            is UiState.Success -> {
                if (state.data.isNotEmpty()) {
                    itemsIndexed(
                        items = state.data.sortedByDescending { it.created },
                        key = { _, template ->
                            "TemplateListViewCell${template.id}"
                        }
                    ) { index, template ->
                        SwipeableFormItem(
                            items = state.data,
                            index = index,
                            content = FormItemContentType.Custom(
                                content = {
                                    HtmlTextView(
                                        html = template.description,
                                        openLinks = false
                                    )
                                },
                                contentPadding = PaddingValues(16.dp)
                            ),
                            swipeEnabled = isSwipeableFormItemEnabled,
                            isRevealed = state.data.first { it.id == template.id}.swipeActionsRevealed,
                            actions = {
                                FormItemActionIcon(
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
                                    mode.onExpandItem(template)
                                }
                            },
                            onCollapse = {
                                if (mode is TemplateListViewMode.Edit) {
                                    mode.onCollapseItem(template)
                                }
                            },
                            onClick = {
                                onClick(template)
                            },
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                else {
                    item(
                        key = "NoTemplatesListViewErrorCell"
                    ) {
                        FormItem(
                            items = listOf(1),
                            index = 0,
                            mainContent = FormItemContentType.Custom(
                                contentPadding = PaddingValues(16.dp),
                                content = {
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
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )
                                            SeesturmButton(
                                                type = SeesturmButtonType.Primary(),
                                                title = "Vorlage hinzufügen",
                                                onClick = mode.onAddClick,
                                                isLoading = false
                                            )
                                        }
                                    }
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}