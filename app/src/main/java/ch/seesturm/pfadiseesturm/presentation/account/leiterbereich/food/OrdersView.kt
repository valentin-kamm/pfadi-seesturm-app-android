package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.firestore.model.FoodOrder
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichViewModel
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.food.components.FoodOrderCell
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.food.components.FoodOrderLoadingCell
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.alert.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SheetDetents
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SheetScaffoldType
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SimpleModalBottomSheet
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle

@Composable
fun OrdersView(
    viewModel: LeiterbereichViewModel,
    userId: String,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController
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
        },
        isConfirmButtonCritical = true
    )

    SimpleModalBottomSheet(
        show = viewModel.showFoodSheet,
        detents = SheetDetents.LargeOnly,
        type = SheetScaffoldType.Title("Bestellung hinzufügen")
    ) { _, _ ->
        AddOrderView(
            foodItemFieldState = uiState.foodItemState,
            onSubmit = { viewModel.addNewFoodOrder() },
            onNumberPickerValueChange = { newValue ->
                viewModel.updateFoodItemCount(newValue)
            },
            isButtonLoading = uiState.addNewOrderState.isLoading
        )
    }

    OrdersContentView(
        userId = userId,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        accountNavController = accountNavController,
        foodState = uiState.foodResult,
        deleteAllOrdersState = uiState.deleteAllOrdersState,
        onDeleteFromOrder = { orderId ->
            viewModel.deleteFromExistingOrder(orderId)
        },
        onAddToOrder = { orderId ->
            viewModel.addToExistingOrder(orderId)
        },
        onDeleteAllOrders = {
            viewModel.updateDeleteAllOrdersAlertVisibility(true)
        },
        showFoodSheet = viewModel.showFoodSheet
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrdersContentView(
    foodState: UiState<List<FoodOrder>>,
    userId: String,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    deleteAllOrdersState: ActionState<Unit>,
    onDeleteAllOrders: () -> Unit,
    onDeleteFromOrder: (String) -> Unit,
    onAddToOrder: (String) -> Unit,
    showFoodSheet: MutableState<Boolean>,
    columnState: LazyListState = rememberLazyListState()
) {

    val loadingCellNumber = 10

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        navigationAction = TopBarNavigationIcon.Back { accountNavController.navigateUp() },
        title = "Bestellungen",
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
                                onClick = onDeleteAllOrders
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
                    showFoodSheet.value = true
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
                        FoodOrderLoadingCell(
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
                        ErrorCardView(
                            errorDescription = foodState.message,
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                is UiState.Success -> {

                    val relevantOrders = foodState.data.filter { it.totalCount > 0 }

                    if (relevantOrders.isNotEmpty()) {
                        itemsIndexed(
                            items = relevantOrders,
                            key = { _, order ->
                                order.id
                            }
                        ) { index, order ->
                            FoodOrderCell(
                                order = order,
                                orders = relevantOrders,
                                index = index,
                                userId = userId,
                                onDelete = {
                                    onDeleteFromOrder(order.id)
                                },
                                onAdd = {
                                    onAddToOrder(order.id)
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
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Text(
                                        text = "Füge jetzt die erste Bestellung hinzu.",
                                        style = MaterialTheme.typography.bodySmall,
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
                                            showFoodSheet.value = true
                                        },
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
private fun OrdersViewPreview1() {
    PfadiSeesturmTheme {
        OrdersContentView(
            foodState = UiState.Loading,
            userId = "123",
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            deleteAllOrdersState = ActionState.Idle,
            onDeleteAllOrders = {},
            onDeleteFromOrder = {},
            onAddToOrder = {},
            showFoodSheet = mutableStateOf(false)
        )
    }
}
@Preview("Error")
@Composable
private fun OrdersViewPreview2() {
    PfadiSeesturmTheme {
        OrdersContentView(
            foodState = UiState.Error("Schlimmer Fehler"),
            userId = "123",
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            deleteAllOrdersState = ActionState.Idle,
            onDeleteAllOrders = {},
            onDeleteFromOrder = {},
            onAddToOrder = {},
            showFoodSheet = mutableStateOf(false)
        )
    }
}
@Preview("No Orders")
@Composable
private fun OrdersViewPreview3() {
    PfadiSeesturmTheme {
        OrdersContentView(
            foodState = UiState.Success(emptyList<FoodOrder>()),
            userId = "123",
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            deleteAllOrdersState = ActionState.Idle,
            onDeleteAllOrders = {},
            onDeleteFromOrder = {},
            onAddToOrder = {},
            showFoodSheet = mutableStateOf(false)
        )
    }
}
@Preview("Success")
@Composable
private fun OrdersViewPreview4() {
    PfadiSeesturmTheme {
        OrdersContentView(
            foodState = UiState.Success(DummyData.foodOrders),
            userId = "123",
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            deleteAllOrdersState = ActionState.Idle,
            onDeleteAllOrders = {},
            onDeleteFromOrder = {},
            onAddToOrder = {},
            showFoodSheet = mutableStateOf(false)
        )
    }
}