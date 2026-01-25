package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.domain.wordpress.model.groupedByYearAndMonth
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.AktivitaetBearbeitenMode
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components.StufenbereichAnAbmeldungCell
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components.StufenbereichAnAbmeldungLoadingCell
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components.StufenbereichAnAbmeldungSheet
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.alert.GenericAlert
import ch.seesturm.pfadiseesturm.presentation.common.alert.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.lists.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.lists.seesturmStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.picker.SeesturmDatePicker
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ModalBottomSheetKeyboardResponse
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ModalBottomSheetWithItem
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SheetDetents
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SheetScaffoldType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StufenbereichView(
    stufe: SeesturmStufe,
    viewModel: StufenbereichViewModel,
    appStateViewModel: AppStateViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val appState by appStateViewModel.state.collectAsStateWithLifecycle()

    val isDatePickerShown = rememberSaveable { mutableStateOf(false) }

    SimpleAlert(
        isShown = uiState.showDeleteAllAbmeldungenAlert,
        title = "An- und Abmeldungen löschen",
        description = "Die An- und Abmeldungen aller vergangenen Aktivitäten werden gelöscht. Fortfahren?",
        icon = Icons.Default.Delete,
        confirmButtonText = "Löschen",
        onConfirm = {
            viewModel.deleteAllAnAbmeldungen()
        },
        onDismiss = {
            viewModel.updateShowDeleteAllAbmeldungenAlert(false)
        },
        isConfirmButtonCritical = true
    )
    GenericAlert(
        action = uiState.showDeleteAbmeldungenAlert,
        title = "An- und Abmeldungen löschen",
        description = "Die An- und Abmeldungen der ausgewählten Aktivität werden gelöscht. Fortfahren?",
        icon = Icons.Default.Delete,
        confirmButtonText = "Löschen",
        onConfirm = { aktivitaet ->
            viewModel.deleteAnAbmeldungenForAktivitaet(aktivitaet)
        },
        onDismiss = { 
            viewModel.updateShowDeleteAbmeldungenAlert(null)
        },
        isConfirmButtonCritical = true
    )
    GenericAlert(
        action = uiState.showSendPushNotificationAlert,
        title = "Push-Nachricht senden",
        description = "Für die ausgewählte Aktivität wird eine Push-Nachricht versendet. Fortfahren?",
        icon = Icons.Default.Notifications,
        confirmButtonText = "Senden",
        onConfirm = { aktivitaet ->
            viewModel.sendPushNotification(aktivitaet)
        },
        onDismiss = {
            viewModel.updateShowSendPushNotificationAlert(null)
        },
        isConfirmButtonCritical = true
    )

    SeesturmDatePicker(
        isShown = isDatePickerShown.value,
        onDismiss = { isDatePickerShown.value = false },
        onConfirm = { year, month, dayOfMonth ->
            viewModel.updateSelectedDate(year, month, dayOfMonth)
        },
        initialSelectedDate = uiState.selectedDate,
        dismissOnClickOutside = true,
        dismissOnBackPress = true
    )

    val sheetItem = retain { mutableStateOf<AnAbmeldungenSheetContent?>(null) }

    ModalBottomSheetWithItem(
        item = sheetItem,
        detents = SheetDetents.All,
        type = SheetScaffoldType.Title(
            if (sheetItem.value?.event?.event?.startDateFormatted == null) {
                ""
            }
            else {
                "Aktivität vom ${sheetItem.value?.event?.event?.startDateFormatted ?: ""}"
            }
        ),
        appStateViewModel = appStateViewModel,
        keyboardResponse = ModalBottomSheetKeyboardResponse.ScrollContent,
        onDismiss = { sheetItem.value = null }
    ) { item, _, _ ->
        StufenbereichAnAbmeldungSheet(
            initialInteraction = item.type,
            aktivitaet = item.event,
            stufe = stufe,
            modifier = Modifier
                .fillMaxSize()
        )
    }
    
    StufenbereichContentView(
        stufe = stufe,
        uiState = uiState,
        abmeldungenState = viewModel.abmeldungenState,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        accountNavController = accountNavController,
        modifier = modifier,
        onRefresh = {
            viewModel.refresh()
        },
        onDeleteAllAbmeldungen = {
            viewModel.updateShowDeleteAllAbmeldungenAlert(true)
        },
        onChangeDatePickerVisibility = { isVisible ->
            isDatePickerShown.value = isVisible
        },
        onRetry = {
            viewModel.loadData(false)
        },
        onDeleteAnAbmeldungen = { aktivitaet ->
            viewModel.updateShowDeleteAbmeldungenAlert(aktivitaet)
        },
        onSendPushNotification = { aktivitaet ->
            viewModel.updateShowSendPushNotificationAlert(aktivitaet)
        },
        isEditButtonLoading = { aktivitaet ->
            viewModel.isEditButtonLoading(aktivitaet)
        },
        onEditAktivitaet = { mode ->
            when (mode) {
                AktivitaetBearbeitenMode.Insert -> {
                    accountNavController.navigate(
                        AppDestination.MainTabView.Destinations.Account.Destinations.NewAktivitaet(
                            stufe = stufe
                        )
                    )
                }
                is AktivitaetBearbeitenMode.Update -> {
                    accountNavController.navigate(
                        AppDestination.MainTabView.Destinations.Account.Destinations.UpdateAktivitaet(
                            stufe = stufe,
                            id = mode.id
                        )
                    )
                }
            }
        },
        onDisplayAktivitaet = { event ->
            accountNavController.navigate(
                AppDestination.MainTabView.Destinations.Account.Destinations.DisplayAktivitaet(
                    stufe = stufe,
                    id = event.event.id
                )
            )
        },
        onOpenAbAbmeldungenSheet = { sheetItem.value = it },
        isDarkTheme = appState.theme.isDarkTheme
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StufenbereichContentView(
    stufe: SeesturmStufe,
    uiState: StufenbereichState,
    abmeldungenState: UiState<List<GoogleCalendarEventWithAnAbmeldungen>>,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    onRefresh: () -> Unit,
    onDeleteAllAbmeldungen: () -> Unit,
    onRetry: () -> Unit,
    onChangeDatePickerVisibility: (Boolean) -> Unit,
    isEditButtonLoading: (GoogleCalendarEventWithAnAbmeldungen) -> Boolean,
    onDeleteAnAbmeldungen: (GoogleCalendarEventWithAnAbmeldungen) -> Unit,
    onSendPushNotification: (GoogleCalendarEventWithAnAbmeldungen) -> Unit,
    onEditAktivitaet: (AktivitaetBearbeitenMode) -> Unit,
    onDisplayAktivitaet: (GoogleCalendarEventWithAnAbmeldungen) -> Unit,
    onOpenAbAbmeldungenSheet: (AnAbmeldungenSheetContent) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    columnState: LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState()
) {

    val selectedDateFormatted = remember(uiState.selectedDate) {
        DateTimeUtil.shared.formatDate(
            date = uiState.selectedDate,
            format = "dd.MM.yyyy",
            type = DateFormattingType.Absolute
        )
    }

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = stufe.stufenName,
        navigationAction = TopBarNavigationIcon.Back { accountNavController.navigateUp() },
        actions = {
            if (abmeldungenState.isSuccess) {
                when (uiState.deleteAllAbmeldungenState) {
                    is ActionState.Loading -> {
                        CircularProgressIndicator(
                            color = Color.SEESTURM_GREEN,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    }
                    else -> {
                        IconButton(
                            onClick = onDeleteAllAbmeldungen
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            IconButton(
                onClick = {
                    onEditAktivitaet(AktivitaetBearbeitenMode.Insert)
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
            additionalBottomPadding = 16.dp
        )

        val stickyOffsets = rememberStickyHeaderOffsets(columnState, 0)

        LazyColumn(
            state = columnState,
            userScrollEnabled = !abmeldungenState.scrollingDisabled,
            contentPadding = combinedPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxSize()
                .pullToRefresh(
                    isRefreshing = uiState.refreshing,
                    state = refreshState,
                    onRefresh = onRefresh
                )
                .background(MaterialTheme.colorScheme.background)
        ) {

            item(
                key = "StufenbereichFilterItem"
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Aktivitäten ab",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .alpha(0.4f)
                    )
                    SeesturmButton(
                        type = SeesturmButtonType.Primary,
                        colors = SeesturmButtonColor.Custom(
                            buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = Color.SEESTURM_GREEN,
                        ),
                        icon = SeesturmButtonIconType.Predefined(
                            icon = Icons.Outlined.CalendarMonth
                        ),
                        title = selectedDateFormatted,
                        onClick = {
                            onChangeDatePickerVisibility(true)
                        },
                        enabled = abmeldungenState.isSuccess,
                        modifier = Modifier
                            .wrapContentWidth(),
                        isLoading = false
                    )
                }
            }

            when (abmeldungenState) {
                UiState.Loading -> {

                    (0..<2).toList().forEach { headerIndex ->
                        seesturmStickyHeader(
                            uniqueKey = "StufenbereichLoadingHeader$headerIndex",
                            stickyOffsets = stickyOffsets
                        ) {
                            BasicListHeader(
                                mode = BasicListHeaderMode.Loading,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                            )
                        }
                        items(
                            count = 4,
                            key = { index -> "StufenbereichLoadingCell$headerIndex$index" }
                        ) {
                            StufenbereichAnAbmeldungLoadingCell(
                                stufe = stufe,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItem()
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "StufenbereichErrorCell"
                    ) {
                        ErrorCardView(
                            errorDescription = abmeldungenState.message,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        ) {
                            onRetry()
                        }
                    }
                }
                is UiState.Success -> {

                    val filteredData = abmeldungenState.data.filter { it.event.end >= uiState.selectedDate }.sortedByDescending { it.event.start }

                    if (filteredData.isEmpty()) {
                        item(
                            key = "StufenbereichKeineDatenCell"
                        ) {
                            Text(
                                "Keine Daten vorhanden",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 75.dp)
                                    .padding(horizontal = 16.dp)
                                    .alpha(0.4f)
                                    .animateItem()
                            )
                        }
                    }
                    else {

                        filteredData.groupedByYearAndMonth.forEachIndexed { _, (startDate, events) ->

                            val headerTitle = DateTimeUtil.shared.formatDate(
                                date = startDate,
                                format = "MMMM yyyy",
                                type = DateFormattingType.Absolute
                            )

                            seesturmStickyHeader(
                                uniqueKey = headerTitle,
                                stickyOffsets = stickyOffsets
                            ) { _ ->
                                BasicListHeader(
                                    mode = BasicListHeaderMode.Normal(headerTitle),
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                )
                            }

                            itemsIndexed(
                                items = events,
                                key = { _, aktivitaet ->
                                    "StufenbereichCell${aktivitaet.event.id}"
                                }
                            ) { _, aktivitaet ->
                                StufenbereichAnAbmeldungCell(
                                    aktivitaet = aktivitaet,
                                    stufe = stufe,
                                    isBearbeitenButtonLoading = isEditButtonLoading(aktivitaet),
                                    onDeleteAnAbmeldungen = {
                                        onDeleteAnAbmeldungen(aktivitaet)
                                    },
                                    onSendPushNotification = {
                                        onSendPushNotification(aktivitaet)
                                    },
                                    onEditAktivitaet = {
                                        onEditAktivitaet(AktivitaetBearbeitenMode.Update(aktivitaet.event.id))
                                    },
                                    onClick = {
                                        onDisplayAktivitaet(aktivitaet)
                                    },
                                    isDarkTheme = isDarkTheme,
                                    onOpenSheet = { interaction ->
                                        onOpenAbAbmeldungenSheet(
                                            AnAbmeldungenSheetContent(
                                                event = aktivitaet,
                                                type = interaction
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = combinedPadding.calculateTopPadding())
        ) {
            PullToRefreshDefaults.Indicator(
                state = refreshState,
                isRefreshing = uiState.refreshing,
                color = Color.SEESTURM_GREEN,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

private data class AnAbmeldungenSheetContent(
    val event: GoogleCalendarEventWithAnAbmeldungen,
    val type: AktivitaetInteractionType
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Loading")
@Composable
private fun StufenbereichViewPreview1() {
    PfadiSeesturmTheme {
        StufenbereichContentView(
            stufe = SeesturmStufe.Biber,
            uiState = StufenbereichState(),
            abmeldungenState = UiState.Loading,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            onRefresh = {},
            onDeleteAllAbmeldungen = {},
            onRetry = {},
            onChangeDatePickerVisibility = {},
            isEditButtonLoading = { false },
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onDisplayAktivitaet = {},
            isDarkTheme = false,
            onOpenAbAbmeldungenSheet = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Error")
@Composable
private fun StufenbereichViewPreview2() {
    PfadiSeesturmTheme {
        StufenbereichContentView(
            stufe = SeesturmStufe.Biber,
            uiState = StufenbereichState(),
            abmeldungenState = UiState.Error("Schwerer Fehler"),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            onRefresh = {},
            onDeleteAllAbmeldungen = {},
            onRetry = {},
            onChangeDatePickerVisibility = {},
            isEditButtonLoading = { false },
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onDisplayAktivitaet = {},
            isDarkTheme = false,
            onOpenAbAbmeldungenSheet = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Empty")
@Composable
private fun StufenbereichViewPreview3() {
    PfadiSeesturmTheme {
        StufenbereichContentView(
            stufe = SeesturmStufe.Biber,
            uiState = StufenbereichState(),
            abmeldungenState = UiState.Success(emptyList()),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            onRefresh = {},
            onDeleteAllAbmeldungen = {},
            onRetry = {},
            onChangeDatePickerVisibility = {},
            isEditButtonLoading = { false },
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onDisplayAktivitaet = {},
            isDarkTheme = false,
            onOpenAbAbmeldungenSheet = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Success")
@Composable
private fun StufenbereichViewPreview4() {
    PfadiSeesturmTheme {
        StufenbereichContentView(
            stufe = SeesturmStufe.Biber,
            uiState = StufenbereichState(),
            abmeldungenState = UiState.Success(listOf(
                GoogleCalendarEventWithAnAbmeldungen(
                    event = DummyData.aktivitaet1.copy(end = ZonedDateTime.now()),
                    anAbmeldungen = listOf(
                        DummyData.abmeldung1
                    )
                ),
                GoogleCalendarEventWithAnAbmeldungen(
                    event = DummyData.aktivitaet2.copy(end = ZonedDateTime.now()),
                    anAbmeldungen = listOf(
                        DummyData.abmeldung3
                    )
                )
            )),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            onRefresh = {},
            onDeleteAllAbmeldungen = {},
            onRetry = {},
            onChangeDatePickerVisibility = {},
            isEditButtonLoading = { false },
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onDisplayAktivitaet = {},
            isDarkTheme = false,
            onOpenAbAbmeldungenSheet = {}
        )
    }
}