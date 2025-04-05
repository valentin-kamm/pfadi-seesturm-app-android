package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.presentation.common.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.LeiterbereichProfileHeaderView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.LeiterbereichStufeLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.LeiterbereichStufenScrollView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.LeiterbereichTopHorizontalScrollView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassCardView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.components.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.components.MainSectionHeader
import ch.seesturm.pfadiseesturm.presentation.common.components.MainSectionHeaderType
import ch.seesturm.pfadiseesturm.presentation.common.forms.myStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.forms.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.main.AppState
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import ch.seesturm.pfadiseesturm.util.state.UiState
import java.time.ZonedDateTime

@Composable
fun LeiterbereichView(
    user: FirebaseHitobitoUser,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    viewModel: LeiterbereichViewModel,
    appStateViewModel: AppStateViewModel
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val appState by appStateViewModel.state.collectAsStateWithLifecycle()

    SimpleAlert(
        isShown = uiState.showSignOutAlert,
        title = "Möchtest du dich wirklich abmelden?",
        icon = Icons.Outlined.AccountBox,
        confirmButtonText = "Abmelden",
        onConfirm = {
            appStateViewModel.signOut(user)
        },
        onDismiss = {
            viewModel.updateSignOutAlertVisibility(false)
        }
    )
    SimpleAlert(
        isShown = uiState.showDeleteAccountAlert,
        title = "Möchtest du deinen Account wirklich löschen?",
        icon = Icons.Outlined.AccountBox,
        confirmButtonText = "Löschen",
        onConfirm = {
            appStateViewModel.deleteAccount(user)
        },
        onDismiss = {
            viewModel.updateDeleteAccountAlertVisibility(false)
        }
    )

    LeiterbereichContentView(
        user = user,
        uiState = uiState,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        accountNavController = accountNavController,
        appState = appState,
        onDeleteAccount = {
            viewModel.updateDeleteAccountAlertVisibility(true)
        },
        onSignOut = {
            viewModel.updateSignOutAlertVisibility(true)
        },
        onRetryEvents = {
            viewModel.fetchEvents()
        },
        stufenDropdownText = viewModel.stufenDropdownText,
        onToggleStufe = { stufe ->
            viewModel.toggleStufe(stufe)
        }
    )
}

@Composable
private fun LeiterbereichContentView(
    user: FirebaseHitobitoUser,
    uiState: LeiterbereichState,
    appState: AppState,
    calendar: SeesturmCalendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    onDeleteAccount: () -> Unit,
    onSignOut: () -> Unit,
    onRetryEvents: () -> Unit,
    stufenDropdownText: String,
    onToggleStufe: (SeesturmStufe) -> Unit,
    columnState: LazyListState = rememberLazyListState(),
    screenWidth: Dp = LocalConfiguration.current.screenWidthDp.dp
) {

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = "Schöpfli"
    ) { topBarInnerPadding ->
        val combinedPadding = bottomNavigationInnerPadding.intersectWith(topBarInnerPadding, LayoutDirection.Ltr)

        val stickyOffsets = rememberStickyHeaderOffsets(columnState, 0)

        LazyColumn(
            state = columnState,
            userScrollEnabled = true,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            item(
                key = "LeiterbereichProfileHeader"
            ) {
                LeiterbereichProfileHeaderView(
                    user = user,
                    isLoading = appState.authState.deleteAccountButtonLoading,
                    onSignOut = onSignOut,
                    onDeleteAccount = onDeleteAccount
                )
            }

            item(
                key = "LeiterbereichTopHorizontalScrollView"
            ) {
                LeiterbereichTopHorizontalScrollView(
                    onNavigateToFood = {
                        accountNavController.navigate(
                            AppDestination.MainTabView.Destinations.Account.Destinations.Food(
                                userId = user.userId,
                                calendar = calendar
                            )
                        )
                    },
                    foodState = uiState.foodResult
                )
            }

            // schöpflialarm
            myStickyHeader(
                uniqueKey = "leiterbereichSchöpflialarmSection",
                stickyOffsets = stickyOffsets
            ) { _ ->
                MainSectionHeader(
                    sectionTitle = "Schöpflialarm",
                    icon = Icons.Default.NotificationsActive,
                    type = MainSectionHeaderType.Blank,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                )
            }

            // stufenbereich
            myStickyHeader(
                uniqueKey = "leiterbereichStufenSection",
                stickyOffsets = stickyOffsets
            ) { _ ->
                MainSectionHeader(
                    sectionTitle = "Stufen",
                    icon = Icons.Default.Group,
                    type = MainSectionHeaderType.Custom(
                        content = {
                            DropdownButton(
                                title = stufenDropdownText,
                                enabled = uiState.selectedStufen.isSuccess,
                                dropdown = { isShown, dismiss ->
                                    DropdownMenu(
                                        expanded = isShown,
                                        onDismissRequest = {
                                            dismiss()
                                        }
                                    ) {
                                        SeesturmStufe.entries.sortedBy { it.id }.forEach { stufe ->
                                            DropdownMenuItem(
                                                text = { Text(text = stufe.stufenName) },
                                                onClick = {
                                                    onToggleStufe(stufe)
                                                    dismiss()
                                                },
                                                trailingIcon = {
                                                    when (val localState = uiState.selectedStufen) {
                                                        is UiState.Success -> {
                                                            if (localState.data.contains(stufe)) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Check,
                                                                    contentDescription = null
                                                                )
                                                            }
                                                        }
                                                        else -> {
                                                            // nothing
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    ),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                )
            }

            when (uiState.selectedStufen) {
                UiState.Loading -> {
                    item(
                        key = "LeiterbereichStufenHorizontalScrollErrorView"
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top,
                            contentPadding = PaddingValues(16.dp),
                            userScrollEnabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        ) {
                            items(3) {
                                LeiterbereichStufeLoadingCardView(
                                    width = 0.85 * (screenWidth - 48.dp) / 2
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "LeiterbereichStufenHorizontalScrollErrorView"
                    ) {
                        CardErrorView(
                            errorDescription = uiState.selectedStufen.message,
                            modifier = Modifier
                                .padding(16.dp)
                                .animateItem()
                        )
                    }
                }
                is UiState.Success -> {
                    item(
                        key = "LeiterbereichStufenHorizontalScrollView"
                    ) {
                        LeiterbereichStufenScrollView(
                            selectedStufen = uiState.selectedStufen.data,
                            screenWidth = screenWidth,
                            accountNavController = accountNavController,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
            }

            // termine
            myStickyHeader(
                uniqueKey = "leiterbereichTermineSection",
                stickyOffsets = stickyOffsets
            ) { _ ->
                MainSectionHeader(
                    sectionTitle = "Termine",
                    icon = Icons.Default.CalendarMonth,
                    type = MainSectionHeaderType.Button(
                        buttonTitle = "Alle",
                        buttonIcon = Icons.AutoMirrored.Default.ArrowForwardIos,
                        buttonAction = {
                            accountNavController.navigate(
                                AppDestination.MainTabView.Destinations.Account.Destinations.AccountTermine
                            )
                        }
                    ),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                )
            }
            when (val termineState = uiState.termineResult) {
                UiState.Loading -> {
                    items(
                        count = 3,
                        key = { index ->
                            "LeiterbereichTermineLoadingCell$index"
                        }
                    ) { index ->
                        AnlassLoadingCardView(
                            onAppear = null,
                            modifier = Modifier
                                .padding(top = if (index == 0) 16.dp else 0.dp)
                                .animateItem()
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "LeiterbereichAnlaesseErrorCell"
                    ) {
                        CardErrorView(
                            errorDescription = termineState.message,
                            modifier = Modifier
                                .padding(16.dp)
                                .animateItem()
                        ) {
                            onRetryEvents()
                        }
                    }
                }
                is UiState.Success -> {
                    if (termineState.data.isNotEmpty()) {
                        itemsIndexed(
                            termineState.data,
                            key = { _, event ->
                                event.id
                            }
                        ) { index, item ->
                            AnlassCardView(
                                event = item,
                                calendar = calendar,
                                onClick = {
                                    accountNavController.navigate(
                                        AppDestination.MainTabView.Destinations.Account.Destinations.AccountTermineDetail(
                                            calendar = calendar,
                                            eventId = item.id,
                                            cacheIdentifier = MemoryCacheIdentifier.Home
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .padding(top = if (index == 0) 16.dp else 0.dp)
                                    .animateItem()
                            )
                        }
                    }
                    else {
                        item(
                            key = "LeiterbereichKeineBevorstehendenAnlaesseCell"
                        ) {
                            Text(
                                "Keine bevorstehenden Termine",
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
                }
            }
        }
    }
}

@Preview
@Composable
private fun LeiterbereichViewPreview() {
    LeiterbereichContentView(
        uiState = LeiterbereichState.create(
            onFoodItemValueChanged = {}
        ),
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        onDeleteAccount = {},
        onSignOut = {},
        onRetryEvents = {},
        appState = AppState(),
        accountNavController = rememberNavController(),
        stufenDropdownText = "Alle",
        onToggleStufe = {},
        user = FirebaseHitobitoUser(
            userId = "123",
            vorname = "Sepp",
            nachname = "Meier",
            pfadiname = "Tarantula",
            email = "sepp.meier@gmail.com",
            created = ZonedDateTime.now(),
            createdFormatted = "",
            modified = ZonedDateTime.now(),
            modifiedFormatted = ""
        )
    )
}