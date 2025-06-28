package ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemActionIcon
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTrailingElementType
import ch.seesturm.pfadiseesturm.presentation.common.forms.SwipeableFormItem
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun GespeichertePersonenView(
    viewModel: GespeichertePersonenViewModel,
    appStateViewModel: AppStateViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    GespeichertePersonenContentView(
        personenState = uiState.readingResult,
        isInEditingMode = uiState.isInEditingMode,
        navController = navController,
        onToggleEditingMode = {
            viewModel.toggleEditingMode()
        },
        onUpdateSheetContent = {
            appStateViewModel.updateSheetContent(
                content = BottomSheetContent.Scaffold(
                    title = "Person hinzufügen",
                    content = {
                        GespeichertePersonHinzufuegenView(
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                )
            )
        },
        onToggleSwipeActions = { personId ->
            viewModel.toggleSwipeActions(personId)
        },
        onDeletePerson = { personId ->
            viewModel.deletePerson(personId)
        },
        bottomNavigationInnerPadding = bottomNavigationInnerPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GespeichertePersonenContentView(
    personenState: UiState<List<GespeichertePerson>>,
    isInEditingMode: Boolean,
    navController: NavController,
    onToggleEditingMode: () -> Unit,
    onUpdateSheetContent: () -> Unit,
    onToggleSwipeActions: (String) -> Unit,
    onDeletePerson: (String) -> Unit,
    bottomNavigationInnerPadding: PaddingValues,
    columnState: LazyListState = rememberLazyListState(),
) {

    val loadingCellCount = 5

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        onNavigateBack = {
            navController.navigateUp()
        },
        actions = {
            if (personenState is UiState.Success && personenState.data.isNotEmpty()) {
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
            IconButton(
                onClick = onUpdateSheetContent
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalStartPadding = 16.dp,
            additionalEndPadding = 16.dp,
            additionalTopPadding = 16.dp,
            additionalBottomPadding = 16.dp
        )

        LazyColumn(
            state = columnState,
            userScrollEnabled = !personenState.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            when (personenState) {
                UiState.Loading -> {
                    items(
                        count = loadingCellCount,
                        key = { index ->
                            "GespeichertePersonenLoadingCell$index"
                        }
                    ) { index ->
                        FormItem(
                            items = (0..<loadingCellCount).toList(),
                            index = index,
                            mainContent = FormItemContentType.Text(
                                title = "",
                                isLoading = true
                            ),
                            trailingElement = FormItemTrailingElementType.Blank,
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "GespeichertePersonenErrorCell"
                    ) {
                        ErrorCardView(
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = personenState.message,
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                is UiState.Success -> {

                    val persons = personenState.data

                    if (persons.isNotEmpty()) {
                        itemsIndexed(
                            items = persons,
                            key = { index, _ ->
                                "GespeichertePersonenCell$index"
                            }
                        ) { index, person ->

                            SwipeableFormItem(
                                items = persons,
                                index = index,
                                content = FormItemContentType.Text(
                                    title = person.displayName
                                ),
                                swipeEnabled = isInEditingMode,
                                isRevealed = persons.first { it.id == person.id }.swipeActionsRevealed,
                                actions = {
                                    FormItemActionIcon(
                                        onClick = {
                                            onDeletePerson(person.id)
                                        },
                                        backgroundColor = Color.SEESTURM_RED,
                                        icon = Icons.Filled.Delete,
                                    )
                                },
                                onExpand = {
                                    onToggleSwipeActions(person.id)
                                },
                                onCollapse = {
                                    onToggleSwipeActions(person.id)
                                },
                                modifier = Modifier
                                    .animateItem()
                            )
                        }
                    }
                    else {
                        item(
                            key = "KeineGespeichertePersonenCell"
                        ) {
                            CustomCardView(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                        imageVector = Icons.Outlined.PersonOff,
                                        contentDescription = null,
                                        tint = Color.SEESTURM_GREEN,
                                        modifier = Modifier
                                            .size(50.dp)
                                    )
                                    Text(
                                        text = "Keine Personen gespeichert",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Text(
                                        text = "Füge die Angaben von Personen hinzu, die du of von Aktivitäten abmeldest. So musst du sie nicht jedes Mal neu eintragen.",
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    SeesturmButton(
                                        type = SeesturmButtonType.Primary(
                                            icon = SeesturmButtonIconType.Predefined(
                                                icon = Icons.Default.PersonAddAlt
                                            )
                                        ),
                                        title = "Person hinzufügen",
                                        onClick = onUpdateSheetContent,
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

@Preview("Loading")
@Composable
private fun GespeichertePersonenViewPreview1() {
    PfadiSeesturmTheme {
        GespeichertePersonenContentView(
            personenState = UiState.Loading,
            isInEditingMode = false,
            navController = rememberNavController(),
            onToggleEditingMode = {},
            onUpdateSheetContent = {},
            onToggleSwipeActions = {},
            onDeletePerson = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp)
        )
    }
}
@Preview("Error")
@Composable
private fun GespeichertePersonenViewPreview2() {
    PfadiSeesturmTheme {
        GespeichertePersonenContentView(
            personenState = UiState.Error("Schwerer Fehler"),
            isInEditingMode = false,
            navController = rememberNavController(),
            onToggleEditingMode = {},
            onUpdateSheetContent = {},
            onToggleSwipeActions = {},
            onDeletePerson = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp)
        )
    }
}
@Preview("Empty")
@Composable
private fun GespeichertePersonenViewPreview3() {
    PfadiSeesturmTheme {
        GespeichertePersonenContentView(
            personenState = UiState.Success(emptyList()),
            isInEditingMode = false,
            navController = rememberNavController(),
            onToggleEditingMode = {},
            onUpdateSheetContent = {},
            onToggleSwipeActions = {},
            onDeletePerson = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp)
        )
    }
}
@Preview("Success")
@Composable
private fun GespeichertePersonenViewPreview4() {
    PfadiSeesturmTheme {
        GespeichertePersonenContentView(
            personenState = UiState.Success(listOf(
                DummyData.gespeichertePerson1,
                DummyData.gespeichertePerson2,
                DummyData.gespeichertePerson3
            )),
            isInEditingMode = false,
            navController = rememberNavController(),
            onToggleEditingMode = {},
            onUpdateSheetContent = {},
            onToggleSwipeActions = {},
            onDeletePerson = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp)
        )
    }
}