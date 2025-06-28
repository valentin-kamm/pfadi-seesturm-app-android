package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.AktivitaetBearbeitenMode
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components.StufenbereichAnAbmeldungCell
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components.StufenbereichAnAbmeldungLoadingCell
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenu
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenuItem
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.alert.GenericAlert
import ch.seesturm.pfadiseesturm.presentation.common.alert.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.buttons.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.forms.seesturmStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.picker.SeesturmDatePicker
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StufenbereichView(
    stufe: SeesturmStufe,
    viewModel: StufenbereichViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
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
        }
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
    onErrorRetry: () -> Unit,
    onChangeDatePickerVisibility: (Boolean) -> Unit,
    onUpdateSelectedAktivitaetInteraction: (AktivitaetInteractionType) -> Unit,
    isEditButtonLoading: (GoogleCalendarEventWithAnAbmeldungen) -> Boolean,
    onDeleteAnAbmeldungen: (GoogleCalendarEventWithAnAbmeldungen) -> Unit,
    onSendPushNotification: (GoogleCalendarEventWithAnAbmeldungen) -> Unit,
    onEditAktivitaet: (AktivitaetBearbeitenMode) -> Unit,
    onDisplayAktivitaet: (GoogleCalendarEventWithAnAbmeldungen) -> Unit,
    modifier: Modifier = Modifier,
    columnState: LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState()
) {

    val selectedDateFormatted = DateTimeUtil.shared.formatDate(
        date = uiState.selectedDate,
        format = "dd.MM.yyyy",
        type = DateFormattingType.Absolute
    )

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = stufe.stufenName,
        onNavigateBack = {
            accountNavController.navigateUp()
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

        val combinedPadding =
            bottomNavigationInnerPadding.intersectWith(
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

            seesturmStickyHeader(
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
                    if (stufe.allowedAktivitaetInteractions.size > 1) {
                        DropdownButton(
                            title = uiState.selectedAktivitaetInteraction.nomenMehrzahl,
                            contentColor = uiState.selectedAktivitaetInteraction.color,
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
                                                Text(interaction.nomenMehrzahl)
                                            },
                                            onClick = {
                                                dismiss()
                                                onUpdateSelectedAktivitaetInteraction(interaction)
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = interaction.icon,
                                                    tint = interaction.color,
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
                        ErrorCardView(
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
                                    onEditAktivitaet(AktivitaetBearbeitenMode.Update(aktivitaet.event.id))
                                },
                                onClick = {
                                    onDisplayAktivitaet(aktivitaet)
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
            onErrorRetry = {},
            onChangeDatePickerVisibility = {},
            onUpdateSelectedAktivitaetInteraction = {},
            isEditButtonLoading = { false },
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onDisplayAktivitaet = {}
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
            onErrorRetry = {},
            onChangeDatePickerVisibility = {},
            onUpdateSelectedAktivitaetInteraction = {},
            isEditButtonLoading = { false },
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onDisplayAktivitaet = {}
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
            onErrorRetry = {},
            onChangeDatePickerVisibility = {},
            onUpdateSelectedAktivitaetInteraction = {},
            isEditButtonLoading = { false },
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onDisplayAktivitaet = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Success")
@Composable
private fun StufenbereichViewPreview4() {
    PfadiSeesturmTheme {
        StufenbereichContentView(
            stufe = SeesturmStufe.Wolf,
            uiState = StufenbereichState(),
            abmeldungenState = UiState.Success(listOf(
                GoogleCalendarEventWithAnAbmeldungen(
                    event = DummyData.aktivitaet1,
                    anAbmeldungen = listOf(
                        DummyData.abmeldung1
                    )
                ),
                GoogleCalendarEventWithAnAbmeldungen(
                    event = DummyData.aktivitaet2,
                    anAbmeldungen = listOf(
                        DummyData.abmeldung3
                    )
                )
            )),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            onRefresh = {},
            onDeleteAllAbmeldungen = {},
            onErrorRetry = {},
            onChangeDatePickerVisibility = {},
            onUpdateSelectedAktivitaetInteraction = {},
            isEditButtonLoading = { false },
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onDisplayAktivitaet = {}
        )
    }
}