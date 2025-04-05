package ch.seesturm.pfadiseesturm.presentation.account.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import ch.seesturm.pfadiseesturm.domain.firestore.model.FoodOrder
import ch.seesturm.pfadiseesturm.presentation.account.food.components.EssenBestellungCell
import ch.seesturm.pfadiseesturm.presentation.account.food.components.EssenBestellungLoadingCell
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichViewModel
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun EssenBestellenView(
    userId: String,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    viewModel: LeiterbereichViewModel,
    appStateViewModel: AppStateViewModel
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    SimpleAlert(
        isShown = uiState.showDeleteAllOrdersAlert,
        title = "Möchtest du alle Bestellungen löschen?",
        icon = Icons.Default.Delete,
        confirmButtonText = "Löschen",
        onConfirm = {
            viewModel.deleteAllOrders()
        },
        onDismiss = {
            viewModel.updateDeleteAllOrdersAlertVisibility(false)
        }
    )

    EssenBestellenContentView(
        userId = userId,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        accountNavController = accountNavController,
        foodState = uiState.foodResult,
        deleteAllOrdersState = uiState.deleteAllOrdersState,
        bestellungHinzufuegenView = {
            BestellungHinzufuegenView(
                viewModel = viewModel,
                modifier = Modifier
            )
        },
        onDeleteButtonClick = { orderId ->
            viewModel.deleteFromExistingOrder(orderId)
        },
        onAddButtonClick = { orderId ->
            viewModel.addToExistingOrder(orderId)
        },
        onDeleteAllOrdersClick = {
            viewModel.updateDeleteAllOrdersAlertVisibility(true)
        },
        onUpdateSheetVisibility = { content ->
            appStateViewModel.updateSheetContent(content)
        }
    )
}

@Composable
fun EssenBestellenContentView(
    userId: String,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    foodState: UiState<List<FoodOrder>>,
    deleteAllOrdersState: ActionState<Unit>,
    bestellungHinzufuegenView: @Composable () -> Unit,
    onDeleteAllOrdersClick: () -> Unit,
    onDeleteButtonClick: (String) -> Unit,
    onAddButtonClick: (String) -> Unit,
    onUpdateSheetVisibility: (BottomSheetContent?) -> Unit,
    columnState: LazyListState = rememberLazyListState()
) {

    val loadingCellNumber: Int = 15

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        backNavigationAction = {
            accountNavController.popBackStack()
        },
        actions = {
            if (foodState is UiState.Success) {
                if (foodState.data.any { it.totalCount > 0 }) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        if (deleteAllOrdersState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.SEESTURM_GREEN,
                                modifier = Modifier
                                    .size(18.dp)
                            )
                        }
                        else {
                            IconButton(
                                onClick = onDeleteAllOrdersClick
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
            IconButton(
                onClick = {
                    onUpdateSheetVisibility(
                        BottomSheetContent.Scaffold(
                            title = "Bestellung hinzufügen",
                            content = bestellungHinzufuegenView
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
            userScrollEnabled = !foodState.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (foodState) {
                UiState.Loading -> {
                    items(
                        count = loadingCellNumber,
                        key = { index ->
                            "EssenBestellenLoadingCell$index"
                        }
                    ) { index ->
                        EssenBestellungLoadingCell(
                            items = (0..<loadingCellNumber).toList(),
                            index = index,
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "EssenBestellenErrorCell"
                    ) {
                        CardErrorView(
                            errorDescription = "Die Bestellungen konnten nicht geladen werden. ${foodState.message}. Überprüfe deine Internetverbindung oder versuche es später erneut.",
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                is UiState.Success -> {
                    if (foodState.data.any { it.totalCount > 0 }) {
                        itemsIndexed(
                            items = foodState.data.filter { it.totalCount > 0 },
                            key = { _, order ->
                                "EssenBestellenCell${order.id}"
                            }
                        ) { index, order ->
                            EssenBestellungCell(
                                order = order,
                                orders = foodState.data.filter { it.totalCount > 0 },
                                index = index,
                                userId = userId,
                                onDeleteButtonClick = {
                                    onDeleteButtonClick(order.id)
                                },
                                onAddButtonClick = {
                                    onAddButtonClick(order.id)
                                },
                                modifier = Modifier
                                    .animateItem()
                            )
                        }
                    }
                    else {
                        item(
                            key = "KeineBestellungCell"
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
                                        imageVector = Icons.Outlined.NoFood,
                                        contentDescription = null,
                                        tint = Color.SEESTURM_GREEN,
                                        modifier = Modifier
                                            .size(50.dp)
                                    )
                                    Text(
                                        text = "Keine Bestellungen",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Text(
                                        text = "Füge jetzt die erste Bestellung hinzu.",
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    SeesturmButton(
                                        type = SeesturmButtonType.Primary(
                                            icon = SeesturmButtonIconType.Predefined(
                                                icon = Icons.Default.Fastfood
                                            )
                                        ),
                                        title = "Bestellung hinzufügen",
                                        onClick = {
                                            onUpdateSheetVisibility(
                                                BottomSheetContent.Scaffold(
                                                    title = "Bestellung hinzufügen",
                                                    content = bestellungHinzufuegenView
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

@Preview
@Composable
private fun EssenBestellenViewPreview() {
    EssenBestellenContentView(
        userId = "123",
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        accountNavController = rememberNavController(),
        deleteAllOrdersState = ActionState.Loading(Unit),
        foodState = UiState.Success(
            listOf(
                FoodOrder(
                    id = "123",
                    itemDescription = "Dürüm",
                    userIds = listOf("123"),
                    ordersString = "",
                    totalCount = 2,
                    users = listOf(null)
                )
            )
        ),
        bestellungHinzufuegenView = {},
        onDeleteButtonClick = {},
        onAddButtonClick = {},
        onDeleteAllOrdersClick = {},
        onUpdateSheetVisibility = {}
    )
}