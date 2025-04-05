package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.CalendarSubscriptionAlert
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.util.AktivitaetInteraction
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.SeesturmTextFieldState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmBinaryUiState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.subscribeToCalendar

@Composable
fun AktivitaetDetailView(
    stufe: SeesturmStufe,
    bottomNavigationInnerPadding: PaddingValues,
    homeNavController: NavController,
    viewModel: AktivitaetDetailViewModel,
    appStateViewModel: AppStateViewModel
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    AktivitaetDetailContentView(
        uiState = uiState,
        stufe = stufe,
        homeNavController = homeNavController,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onUpdateAlertVisibility = { isVisible ->
            viewModel.updateCalendarSubscriptionAlertVisibility(isVisible)
        },
        onChangeSheetMode = { interaction ->
            viewModel.changeSheetMode(interaction)
        },
        onRetry = {
            viewModel.getAktivitaet()
        },
        onSetGespeichertePerson = { person ->
            viewModel.setGespeichertePerson(person)
        },
        sheetContent = { aktivitaet ->
            AktivitaetAnAbmeldenView(
                viewModel = viewModel,
                aktivitaet = aktivitaet,
                stufe = stufe,
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        onUpdateSheetContent = { content ->
            appStateViewModel.updateSheetContent(content)
        }
    )
}

@Composable
private fun AktivitaetDetailContentView(
    uiState: AktivitaetDetailState,
    stufe: SeesturmStufe,
    homeNavController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    onUpdateAlertVisibility: (Boolean) -> Unit,
    onChangeSheetMode: (AktivitaetInteraction) -> Unit,
    onRetry: () -> Unit,
    onSetGespeichertePerson: (GespeichertePerson) -> Unit,
    sheetContent: @Composable (GoogleCalendarEvent) -> Unit,
    onUpdateSheetContent: (BottomSheetContent?) -> Unit,
    columnState: LazyListState = rememberLazyListState()
) {

    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    CalendarSubscriptionAlert (
        isShown = uiState.showCalendarSubscriptionAlert,
        title = "Kalender der ${stufe.stufenName} kann nicht abonniert werden",
        calendar = stufe.calendar,
        onDismiss = {
            onUpdateAlertVisibility(false)
        }
    )

    TopBarScaffold(
        title = stufe.aktivitaetDescription,
        topBarStyle = TopBarStyle.Small,
        backNavigationAction = {
            homeNavController.popBackStack()
        },
        actions = {
            IconButton(
                onClick = {
                    when(subscribeToCalendar(subscriptionUrl = stufe.calendar.subscriptionUrl, context = context)) {
                        is SeesturmResult.Error -> {
                            onUpdateAlertVisibility(true)
                        }
                        is SeesturmResult.Success -> {
                            // do nothing
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = {
                    homeNavController.navigate(
                        AppDestination.MainTabView.Destinations.Home.Destinations.PushNotifications
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
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
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val localState = uiState.loadingState) {
                UiState.Loading -> {
                    item(
                        key = "NächsteAktivitätDetailLoadingCard"
                    ) {
                        AktivitaetDetailLoadingCardView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "NächsteAktivitätDetailErrorCard"
                    ) {
                        CardErrorView(
                            errorDescription = localState.message,
                            retryAction = {
                                onRetry()
                            },
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                is UiState.Success -> {
                    item(
                        key = "NächsteAktivitätDetailCard"
                    ) {
                        AktivitaetDetailCardView(
                            stufe = stufe,
                            aktivitaet = localState.data,
                            type = AktivitaetDetailCardViewType.Normal(
                                navController = homeNavController,
                                openSheet = { interaction ->
                                    if (localState.data != null) {
                                        onChangeSheetMode(interaction)
                                        onUpdateSheetContent(
                                            BottomSheetContent.Scaffold(
                                                title = stufe.aktivitaetDescription,
                                                content = { sheetContent(localState.data) },
                                                actions = {
                                                    Box {
                                                        IconButton(
                                                            onClick = {
                                                                showMenu = true
                                                            },
                                                            enabled = uiState.gespeichertePersonenState.isSuccess
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Outlined.PersonAddAlt,
                                                                contentDescription = null
                                                            )
                                                        }
                                                        if (uiState.gespeichertePersonenState is UiState.Success) {
                                                            DropdownMenu(
                                                                expanded = showMenu,
                                                                onDismissRequest = {
                                                                    showMenu = false
                                                                }
                                                            ) {
                                                                if (uiState.gespeichertePersonenState.data.isNotEmpty()) {
                                                                    uiState.gespeichertePersonenState.data.forEach { person ->
                                                                        DropdownMenuItem(
                                                                            text = {
                                                                                Text(text = person.displayName)
                                                                            },
                                                                            onClick = {
                                                                                onSetGespeichertePerson(person)
                                                                                showMenu = false
                                                                            }
                                                                        )
                                                                    }
                                                                    HorizontalDivider()
                                                                }
                                                                DropdownMenuItem(
                                                                    text = {
                                                                        Text("Person hinzufügen")
                                                                    },
                                                                    onClick = {
                                                                        showMenu = false
                                                                        onUpdateSheetContent(null)
                                                                        homeNavController.navigate(
                                                                            AppDestination.MainTabView.Destinations.Home.Destinations.GespeichertePersonen
                                                                        )
                                                                    },
                                                                    trailingIcon = {
                                                                        Icon(
                                                                            imageVector = Icons.Outlined.PersonAddAlt,
                                                                            contentDescription = null
                                                                        )
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            )
                                        )
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AktivitaetDetailViewPreview() {
    AktivitaetDetailContentView(
        uiState = AktivitaetDetailState(
            loadingState = UiState.Success(
                data = null
            ),
            anAbmeldenState = ActionState.Idle,
            showSheet = false,
            selectedSheetMode = AktivitaetInteraction.ABMELDEN,
            vornameState = SeesturmTextFieldState(
                text = "",
                label = "",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            nachnameState = SeesturmTextFieldState(
                text = "",
                label = "",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            pfadinameState = SeesturmTextFieldState(
                text = "",
                label = "",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            bemerkungState = SeesturmTextFieldState(
                text = "",
                label = "",
                state = SeesturmBinaryUiState.Success(Unit),
                onValueChanged = {}
            ),
            showCalendarSubscriptionAlert = false,
            gespeichertePersonenState = UiState.Loading
        ),
        stufe = SeesturmStufe.Biber,
        homeNavController = rememberNavController(),
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        onUpdateAlertVisibility = {},
        onChangeSheetMode = {},
        onRetry = {},
        onSetGespeichertePerson = {},
        sheetContent = {},
        onUpdateSheetContent = {}
    )
}