package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.sheet.AllowedSheetDetents
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SheetScaffoldType
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SimpleModalBottomSheet
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle

@Composable
fun TemplateEditListView(
    viewModel: TemplateViewModel,
    stufe: SeesturmStufe,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    fun showTemplateSheet(mode: TemplateEditMode) {
        viewModel.updateRichTextState(mode)
        viewModel.showTemplateSheet.value = false
        viewModel.setSheetMode(mode)
        viewModel.showTemplateSheet.value = true
    }
    SimpleModalBottomSheet(
        show = viewModel.showTemplateSheet,
        detents = AllowedSheetDetents.LargeOnly,
        type = SheetScaffoldType.Title(uiState.templateEditMode.navigationTitle)
    ) { _, _ ->
        TemplateEditView(
            mode = uiState.templateEditMode,
            editState = uiState.editState,
            richTextState = uiState.richTextState
        )
    }

    TemplateEditListContentView(
        state = uiState.templatesState,
        stufe = stufe,
        mode = TemplateListViewMode.Edit(
            onAddClick = {
                showTemplateSheet(
                    TemplateEditMode.Insert(
                        onSubmit = { newDescription ->
                            viewModel.insertTemplate(newDescription)
                        }
                    )
                )
            },
            editState = uiState.editState,
            deleteState = uiState.deleteState,
            onCollapseItem = { template -> 
                viewModel.toggleSwipeActionsEnabled(template)
            },
            onExpandItem = { template ->
                viewModel.toggleSwipeActionsEnabled(template)
            },
            onDeleteItem = { template ->
                viewModel.deleteTemplate(template)
            }
        ),
        onNavigateBack = {
            accountNavController.navigateUp()
        },
        isInEditingMode = uiState.isInEditingMode,
        onToggleEditingMode = {
            viewModel.toggleEditingMode()
        },
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onElementClick = { template ->
            showTemplateSheet(
                TemplateEditMode.Update(
                    description = template.description,
                    onSubmit = { newDescription ->
                        viewModel.updateTemplate(template.id, newDescription)
                    }
                )
            )
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplateEditListContentView(
    state: UiState<List<AktivitaetTemplate>>,
    stufe: SeesturmStufe,
    mode: TemplateListViewMode,
    onNavigateBack: () -> Unit,
    onToggleEditingMode: () -> Unit,
    bottomNavigationInnerPadding: PaddingValues,
    isInEditingMode: Boolean,
    onElementClick: (AktivitaetTemplate) -> Unit,
    modifier: Modifier
) {

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = "Vorlagen ${stufe.stufenName}",
        navigationAction = TopBarNavigationIcon.Back { onNavigateBack() },
        modifier = modifier,
        actions = {
            if (mode is TemplateListViewMode.Edit && state is UiState.Success && state.data.isNotEmpty()) {
                TextButton(
                    onClick = onToggleEditingMode
                ) {
                    Text(
                        if (isInEditingMode) {
                            "Fertig"
                        }
                        else {
                            "Bearbeiten"
                        }
                    )
                }
            }
            if (mode is TemplateListViewMode.Edit) {
                IconButton(
                    onClick = mode.onAddClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }
    ) { topBarInnerPadding ->

        val contentPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalStartPadding = 16.dp,
            additionalTopPadding = 16.dp,
            additionalEndPadding = 16.dp,
            additionalBottomPadding = 16.dp
        )

        TemplateListView(
            state = state,
            mode = mode,
            contentPadding = contentPadding,
            onClick = { template ->
                onElementClick(template)
            },
            isInEditingMode = isInEditingMode
        )
    }
}

@Preview("Loading")
@Composable
private fun TemplateEditListViewPreview1() {
    PfadiSeesturmTheme {
        TemplateEditListContentView(
            state = UiState.Loading,
            stufe = SeesturmStufe.Pio,
            mode = TemplateListViewMode.Edit(
                onAddClick = {},
                editState = ActionState.Idle,
                deleteState = ActionState.Idle,
                onCollapseItem = {},
                onExpandItem = {},
                onDeleteItem = {}
            ),
            onNavigateBack = {},
            onToggleEditingMode = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            isInEditingMode = false,
            onElementClick = {},
            modifier = Modifier
        )
    }
}
@Preview("Error")
@Composable
private fun TemplateEditListViewPreview2() {
    PfadiSeesturmTheme {
        TemplateEditListContentView(
            state = UiState.Error("Schwerer Fehler"),
            stufe = SeesturmStufe.Pio,
            mode = TemplateListViewMode.Edit(
                onAddClick = {},
                editState = ActionState.Idle,
                deleteState = ActionState.Idle,
                onCollapseItem = {},
                onExpandItem = {},
                onDeleteItem = {}
            ),
            onNavigateBack = {},
            onToggleEditingMode = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            isInEditingMode = false,
            onElementClick = {},
            modifier = Modifier
        )
    }
}
@Preview("Empty")
@Composable
private fun TemplateEditListViewPreview3() {
    PfadiSeesturmTheme {
        TemplateEditListContentView(
            state = UiState.Success(emptyList()),
            stufe = SeesturmStufe.Pio,
            mode = TemplateListViewMode.Edit(
                onAddClick = {},
                editState = ActionState.Idle,
                deleteState = ActionState.Idle,
                onCollapseItem = {},
                onExpandItem = {},
                onDeleteItem = {}
            ),
            onNavigateBack = {},
            onToggleEditingMode = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            isInEditingMode = false,
            onElementClick = {},
            modifier = Modifier
        )
    }
}
@Preview("Success")
@Composable
private fun TemplateEditListViewPreview4() {
    PfadiSeesturmTheme {
        TemplateEditListContentView(
            state = UiState.Success(listOf(DummyData.aktivitaetTemplate1, DummyData.aktivitaetTemplate2)),
            stufe = SeesturmStufe.Pio,
            mode = TemplateListViewMode.Use,
            onNavigateBack = {},
            onToggleEditingMode = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            isInEditingMode = false,
            onElementClick = {},
            modifier = Modifier
        )
    }
}