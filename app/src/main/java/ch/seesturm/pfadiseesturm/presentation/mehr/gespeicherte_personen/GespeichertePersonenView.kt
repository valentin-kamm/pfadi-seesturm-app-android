package ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.data.data_store.repository.GespeichertePersonenRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.data_store.service.GespeichertePersonenService
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemActionIcon
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemTertiaryElementType
import ch.seesturm.pfadiseesturm.presentation.common.forms.SwipeableFormItem
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun GespeichertePersonenView(
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    viewModel: GespeichertePersonenViewModel,
    appStateViewModel: AppStateViewModel,
    columnState: LazyListState = rememberLazyListState(),
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val loadingCellCount = 5

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        backNavigationAction = {
            navController.popBackStack()
        },
        actions = {
            when (val localState = uiState.readingResult) {
                is UiState.Success -> {
                    if (localState.data.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.toggleEditingMode()
                            }
                        ) {
                            Text(
                                if (uiState.isInEditingMode) {
                                    "Fertig"
                                }
                                else {
                                    "Bearbeiten"
                                }
                            )
                        }
                    }
                }
                else -> {}
            }
            IconButton(
                onClick = {
                    appStateViewModel.updateSheetContent(
                        BottomSheetContent.Scaffold(
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
                }
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
            userScrollEnabled = !uiState.readingResult.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            
            when (val localState = uiState.readingResult) {
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
                            trailingElement = FormItemTertiaryElementType.Blank,
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "GespeichertePersonenErrorCell"
                    ) {
                        CardErrorView(
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = localState.message,
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                is UiState.Success -> {
                    val persons = localState.data
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
                                mainContent = FormItemContentType.Text(
                                    title = person.displayName
                                ),
                                enabled = uiState.isInEditingMode,
                                isRevealed = persons.first { it.id == person.id }.swipeActionsRevealed,
                                actions = {
                                    FormItemActionIcon(
                                        onClick = {
                                            viewModel.deletePerson(person.id)
                                        },
                                        backgroundColor = Color.SEESTURM_RED,
                                        icon = Icons.Filled.Delete,
                                    )
                                },
                                onCollapsed = {
                                    viewModel.disableSwipeActions(person.id)
                                },
                                trailingElement = FormItemTertiaryElementType.Custom(
                                    content = {
                                        if (uiState.isInEditingMode) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.toggleSwipeActionsEnabled(person.id)
                                                },
                                                colors = IconButtonColors(
                                                    containerColor = Color.SEESTURM_RED,
                                                    contentColor = Color.White,
                                                    disabledContentColor = Color.White,
                                                    disabledContainerColor = Color.SEESTURM_RED
                                                ),
                                                modifier = Modifier
                                                    .size(20.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Remove,
                                                    tint = Color.White,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                ),
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
                                        onClick = {
                                            appStateViewModel.updateSheetContent(
                                                BottomSheetContent.Scaffold(
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
                                        }
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

/*
@Preview
@Composable
fun GespeichertePersonenViewPreview() {
    GespeichertePersonenView(
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        navController = rememberNavController(),
        viewModel = viewModel<GespeichertePersonenViewModel>(
            factory = viewModelFactoryHelper {
                GespeichertePersonenViewModel(
                    service = GespeichertePersonenService(
                        repository = GespeichertePersonenRepositoryImpl(
                            dataStore = FakeDataStore(
                                initialValue = SeesturmPreferencesDao()
                            )
                        )
                    ),
                    updateSheetVisibility = {}
                )
            }
        )
    )
}

 */

class FakeDataStore<T>(
    initialValue: T
) : DataStore<T> {
    private val _data = MutableStateFlow(initialValue)
    override val data: Flow<T> = _data
    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        val newValue = transform(_data.value)
        _data.update { newValue }
        return newValue
    }
}