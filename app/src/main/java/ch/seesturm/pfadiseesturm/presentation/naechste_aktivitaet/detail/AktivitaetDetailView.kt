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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.CalendarSubscriptionAlert
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenu
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenuItem
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.subscribeToCalendar

@Composable
fun AktivitaetDetailView(
    viewModel: AktivitaetDetailViewModel,
    stufe: SeesturmStufe,
    location: AktivitaetDetailViewLocation,
    bottomNavigationInnerPadding: PaddingValues,
    onNavigateBack: () -> Unit,
    appStateViewModel: AppStateViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val showGespeichertePersonenDropdown = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    CalendarSubscriptionAlert (
        isShown = uiState.showCalendarSubscriptionAlert,
        title = "Kalender der ${stufe.stufenName} kann nicht abonniert werden",
        calendar = stufe.calendar,
        onDismiss = {
            viewModel.updateCalendarSubscriptionAlertVisibility(false)
        }
    )

    fun showSheet(interaction: AktivitaetInteractionType) {

        viewModel.changeSheetMode(interaction)
        appStateViewModel.updateSheetContent(
            content = BottomSheetContent.Scaffold(
                title = stufe.aktivitaetDescription,
                content = {
                    AktivitaetAnAbmeldenView(
                        viewModel = viewModel,
                        stufe = stufe,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                },
                actions = {
                    if (location is AktivitaetDetailViewLocation.Home) {
                        Box {
                            IconButton(
                                onClick = {
                                    showGespeichertePersonenDropdown.value = true
                                },
                                enabled = uiState.gespeichertePersonenState.isSuccess
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PersonAddAlt,
                                    contentDescription = null
                                )
                            }

                            val personenState = uiState.gespeichertePersonenState

                            if (personenState is UiState.Success) {

                                ThemedDropdownMenu(
                                    expanded = showGespeichertePersonenDropdown.value,
                                    onDismissRequest = {
                                        showGespeichertePersonenDropdown.value = false
                                    }
                                ) {
                                    if (personenState.data.isNotEmpty()) {

                                        personenState.data.forEach { person ->
                                            ThemedDropdownMenuItem(
                                                text = {
                                                    Text(text = person.displayName)
                                                },
                                                onClick = {
                                                    viewModel.setGespeichertePerson(person)
                                                    showGespeichertePersonenDropdown.value = false
                                                }
                                            )
                                        }
                                    }
                                    HorizontalDivider()
                                    ThemedDropdownMenuItem(
                                        text = {
                                            Text("Person hinzufügen")
                                        },
                                        onClick = {
                                            showGespeichertePersonenDropdown.value = false
                                            appStateViewModel.updateSheetContent(null)
                                            location.onNavigateToGespeichertePersonen()
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
                }
            )
        )
    }
    
    AktivitaetDetailContentView(
        loadingState = uiState.loadingState,
        stufe = stufe,
        type = location,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onNavigateBack = onNavigateBack,
        onRetry = {
            viewModel.getAktivitaet()
        },
        onOpenSheet = { interaction ->
            showSheet(interaction)
        },
        onSubscribeToCalendar = {
            val result = subscribeToCalendar(
                subscriptionUrl = stufe.calendar.subscriptionUrl,
                context = context
            )
            if (result is SeesturmResult.Error) {
                viewModel.updateCalendarSubscriptionAlertVisibility(true)
            }
        },
        modifier = modifier
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AktivitaetDetailContentView(
    loadingState: UiState<GoogleCalendarEvent?>,
    stufe: SeesturmStufe,
    type: AktivitaetDetailViewLocation,
    bottomNavigationInnerPadding: PaddingValues,
    onNavigateBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenSheet: (AktivitaetInteractionType) -> Unit,
    onSubscribeToCalendar: () -> Unit,
    modifier: Modifier,
    columnState: LazyListState = rememberLazyListState()
) {
    
    TopBarScaffold(
        title = stufe.aktivitaetDescription,
        topBarStyle = TopBarStyle.Small,
        onNavigateBack = {
            onNavigateBack()
        },
        modifier = modifier,
        actions = {
            IconButton(
                onClick = onSubscribeToCalendar
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null
                )
            }
            if (type is AktivitaetDetailViewLocation.Home) {
                IconButton(
                    onClick = {
                        type.onNavigateToPushNotifications()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null
                    )
                }
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
            when (loadingState) {
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
                        ErrorCardView(
                            errorDescription = loadingState.message,
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
                            aktivitaet = loadingState.data,
                            stufe = stufe,
                            mode = when (type) {
                                is AktivitaetDetailViewLocation.Stufenbereich -> AktivitaetDetailViewMode.ViewOnly
                                is AktivitaetDetailViewLocation.Home -> AktivitaetDetailViewMode.Interactive(
                                    onNavigateToPushNotifications = type.onNavigateToPushNotifications,
                                    onOpenSheet = onOpenSheet
                                )
                            },
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

@Preview("Loading")
@Composable
private fun AktivitaetDetailViewPreview1() {
    PfadiSeesturmTheme {
        AktivitaetDetailContentView(
            loadingState = UiState.Loading,
            stufe = SeesturmStufe.Wolf,
            type = AktivitaetDetailViewLocation.Home(
                getAktivitaet = { SeesturmResult.Success(DummyData.aktivitaet1) },
                eventId = null,
                onNavigateToPushNotifications = {},
                onNavigateToGespeichertePersonen = {}
            ),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateBack = {},
            onRetry = {},
            onOpenSheet = {},
            onSubscribeToCalendar = {},
            modifier = Modifier
        )
    }
}
@Preview("Error")
@Composable
private fun AktivitaetDetailViewPreview2() {
    PfadiSeesturmTheme {
        AktivitaetDetailContentView(
            loadingState = UiState.Error("Schwerer Fehler"),
            stufe = SeesturmStufe.Biber,
            type = AktivitaetDetailViewLocation.Home(
                getAktivitaet = { SeesturmResult.Success(DummyData.aktivitaet1) },
                eventId = null,
                onNavigateToPushNotifications = {},
                onNavigateToGespeichertePersonen = {}
            ),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateBack = {},
            onRetry = {},
            onOpenSheet = {},
            onSubscribeToCalendar = {},
            modifier = Modifier
        )
    }
}
@Preview("Success")
@Composable
private fun AktivitaetDetailViewPreview3() {
    PfadiSeesturmTheme {
        AktivitaetDetailContentView(
            loadingState = UiState.Success(DummyData.aktivitaet1),
            stufe = SeesturmStufe.Pio,
            type = AktivitaetDetailViewLocation.Home(
                getAktivitaet = { SeesturmResult.Success(DummyData.aktivitaet1) },
                eventId = null,
                onNavigateToPushNotifications = {},
                onNavigateToGespeichertePersonen = {}
            ),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onNavigateBack = {},
            onRetry = {},
            onOpenSheet = {},
            onSubscribeToCalendar = {},
            modifier = Modifier
        )
    }
}