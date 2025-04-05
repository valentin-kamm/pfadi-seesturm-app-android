package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.AktivitaetBearbeitenView
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.AktivitaetBearbeitenViewModel
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components.StufenbereichAnAbmeldungCell
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components.StufenbereichAnAbmeldungLoadingCell
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.GenericAlert
import ch.seesturm.pfadiseesturm.presentation.common.SeesturmDatePicker
import ch.seesturm.pfadiseesturm.presentation.common.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.components.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.myStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.accountModule
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.AktivitaetInteraction
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StufenbereichView(
    stufe: SeesturmStufe,
    viewModel: StufenbereichViewModel,
    appStateViewModel: AppStateViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    openSheetUponNavigation: Boolean,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
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
        }
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
        }
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
        }
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
        onUpdateSelectedAktivitaetInteraction = { interaction ->
            viewModel.updateSelectedAktivitaetInteraction(interaction)
        },
        onErrorRetry = {
            viewModel.getAktivitaeten(false)
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
        selectedDateString = viewModel.selectedDateFormatted,
        openSheetUponNavigation = openSheetUponNavigation,
        onUpdateSheetContent = { content ->
            appStateViewModel.updateSheetContent(content)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StufenbereichContentView(
    stufe: SeesturmStufe,
    uiState: StufenbereichState,
    abmeldungenState: UiState<List<GoogleCalendarEventWithAnAbmeldungen>>,
    selectedDateString: String,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    onRefresh: () -> Unit,
    onDeleteAllAbmeldungen: () -> Unit,
    onErrorRetry: () -> Unit,
    onChangeDatePickerVisibility: (Boolean) -> Unit,
    onUpdateSelectedAktivitaetInteraction: (AktivitaetInteraction) -> Unit,
    isEditButtonLoading: (GoogleCalendarEventWithAnAbmeldungen) -> Boolean,
    onDeleteAnAbmeldungen: (GoogleCalendarEventWithAnAbmeldungen) -> Unit,
    onSendPushNotification: (GoogleCalendarEventWithAnAbmeldungen) -> Unit,
    onUpdateSheetContent: (BottomSheetContent?) -> Unit,
    openSheetUponNavigation: Boolean,
    modifier: Modifier = Modifier,
    columnState: LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState()
) {

    val sheetKey = rememberSaveable { mutableStateOf("") }
    val hasSheetOpenedUponNavigation = rememberSaveable { mutableStateOf(false) }

    fun openSheet(mode: StufenbereichSheetMode) {
        sheetKey.value = UUID.randomUUID().toString()
        onUpdateSheetContent(
            BottomSheetContent.Custom(
                content = { snackbarHost ->
                    AktivitaetBearbeitenView(
                        viewModel = viewModel<AktivitaetBearbeitenViewModel>(
                            key = sheetKey.value,
                            factory = viewModelFactoryHelper {
                                AktivitaetBearbeitenViewModel(
                                    selectedSheetMode = mode,
                                    service = accountModule.stufenbereichService,
                                    stufe = stufe,
                                    onDismiss = { onUpdateSheetContent(null) }
                                )
                            }
                        ),
                        stufe = stufe,
                        selectedSheetMode = mode,
                        snackbarHost = snackbarHost
                    )
                }
            )
        )
    }

    LaunchedEffect(Unit) {
        if (openSheetUponNavigation && !hasSheetOpenedUponNavigation.value) {
            hasSheetOpenedUponNavigation.value = true
            openSheet(StufenbereichSheetMode.Insert)
        }
    }

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = stufe.stufenName,
        backNavigationAction = {
            accountNavController.popBackStack()
        },
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
                    openSheet(mode = StufenbereichSheetMode.Insert)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { topBarInnerPadding ->

        val combinedPadding =
            bottomNavigationInnerPadding.intersectWith(
                other = topBarInnerPadding,
                layoutDirection = LayoutDirection.Ltr
            )

        val stickyOffsets = rememberStickyHeaderOffsets(columnState, 0)

        LazyColumn(
            state = columnState,
            userScrollEnabled = !abmeldungenState.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = modifier
                .fillMaxSize()
                .pullToRefresh(
                    isRefreshing = uiState.refreshing,
                    state = refreshState,
                    onRefresh = {
                        onRefresh()
                    }
                )
                .background(MaterialTheme.colorScheme.background)
        ) {

            myStickyHeader(
                uniqueKey = "StufenbereichFilterHeader",
                stickyOffsets = stickyOffsets
            ) { _ ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
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
                            type = SeesturmButtonType.Primary(
                                buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = Color.SEESTURM_GREEN,
                                icon = SeesturmButtonIconType.Predefined(
                                    icon = Icons.Outlined.CalendarMonth
                                )
                            ),
                            title = selectedDateString,
                            onClick = {
                                onChangeDatePickerVisibility(true)
                            },
                            enabled = abmeldungenState.isSuccess,
                            modifier = Modifier
                                .wrapContentWidth()
                        )
                    }
                    DropdownButton(
                        title = uiState.selectedAktivitaetInteraction.nomenMehrzahl,
                        contentColor = uiState.selectedAktivitaetInteraction.color,
                        dropdown = { isShown, dismiss ->
                            DropdownMenu(
                                expanded = isShown,
                                onDismissRequest = {
                                    dismiss()
                                }
                            ) {
                                stufe.allowedAktivitaetInteractions.forEach { interaction ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(interaction.nomenMehrzahl)
                                        },
                                        onClick = {
                                            dismiss()
                                            onUpdateSelectedAktivitaetInteraction(interaction)
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
            }

            when (abmeldungenState) {
                UiState.Loading -> {
                    items(
                        count = 5,
                        key = { index ->
                            "StufenbereichLoadingCell$index"
                        }
                    ) { index ->
                        StufenbereichAnAbmeldungLoadingCell(
                            modifier = Modifier
                                .padding(top = if (index == 0) 16.dp else 0.dp)
                                .animateItem()
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "StufenbereichErrorCell"
                    ) {
                        CardErrorView(
                            errorDescription = abmeldungenState.message,
                            modifier = Modifier
                                .padding(16.dp)
                                .animateItem()
                        ) {
                            onErrorRetry()
                        }
                    }
                }
                is UiState.Success -> {
                    val filteredData = abmeldungenState.data.filter { it.event.endDate > uiState.selectedDate }.sortedByDescending { it.event.startDate }
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
                        itemsIndexed(
                            items = filteredData,
                            key = { _, aktivitaet ->
                                "StufenbereichCell${aktivitaet.event.id}"
                            }
                        ) { index, aktivitaet ->
                            StufenbereichAnAbmeldungCell(
                                aktivitaet = aktivitaet,
                                stufe = stufe,
                                selectedAktivitaetInteraction = uiState.selectedAktivitaetInteraction,
                                isBearbeitenButtonLoading = isEditButtonLoading(aktivitaet),
                                onChangeSelectedAktivitaetInteraction = { interaction ->
                                    onUpdateSelectedAktivitaetInteraction(interaction)
                                },
                                onDeleteAnAbmeldungen = {
                                    onDeleteAnAbmeldungen(aktivitaet)
                                },
                                onSendPushNotification = {
                                    onSendPushNotification(aktivitaet)
                                },
                                onEditAktivitaet = {
                                    openSheet(mode = StufenbereichSheetMode.Update(id = aktivitaet.event.id))
                                },
                                modifier = Modifier
                                    .padding(top = if (index == 0) 16.dp else 0.dp)
                                    .animateItem()
                            )
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
                color = Color.SEESTURM_GREEN
            )
        }
    }
}

sealed class StufenbereichSheetMode {
    data object Insert: StufenbereichSheetMode()
    data class Update(
        val id: String
    ): StufenbereichSheetMode()

    val verb: String
        get() = when (this) {
            Insert -> "veröffentlichen"
            is Update -> "aktualisieren"
        }
    val verbPassiv: String
        get() = when (this) {
            Insert -> "veröffentlicht"
            is Update -> "aktualisiert"
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun StufenbereichViewPreview() {
    StufenbereichContentView(
        stufe = SeesturmStufe.Biber,
        uiState = StufenbereichState(),
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        accountNavController = rememberNavController(),
        abmeldungenState = UiState.Success(emptyList()),
        onRefresh = {},
        onDeleteAllAbmeldungen = {},
        onChangeDatePickerVisibility = {},
        onUpdateSelectedAktivitaetInteraction = {},
        onErrorRetry = {},
        isEditButtonLoading = { false },
        selectedDateString = "23.12.2024",
        onDeleteAnAbmeldungen = {},
        onSendPushNotification = {},
        onUpdateSheetContent = {},
        openSheetUponNavigation = false
    )
}