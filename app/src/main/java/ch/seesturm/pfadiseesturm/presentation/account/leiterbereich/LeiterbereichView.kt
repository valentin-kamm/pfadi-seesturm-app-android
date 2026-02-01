package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.SendToMobile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.firestore.model.FoodOrder
import ch.seesturm.pfadiseesturm.domain.firestore.model.Schoepflialarm
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.main.AuthViewModel
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.CircleProfilePictureView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.CircleProfilePictureViewType
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.LeiterbereichStufeLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.LeiterbereichStufenScrollView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.LeiterbereichTopHorizontalScrollView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.SchoepflialarmCardView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.SchoepflialarmLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components.SchoepflialarmSheet
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassCardView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.AnlassLoadingCardView
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.MainSectionHeader
import ch.seesturm.pfadiseesturm.presentation.common.MainSectionHeaderType
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.alert.SimpleAlert
import ch.seesturm.pfadiseesturm.presentation.common.lists.rememberStickyHeaderOffsets
import ch.seesturm.pfadiseesturm.presentation.common.lists.seesturmStickyHeader
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ModalBottomSheetKeyboardResponse
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SheetDetents
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SheetScaffoldType
import ch.seesturm.pfadiseesturm.presentation.common.sheet.SimpleModalBottomSheet
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.AlertWithSettingsAction
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.AlertWithSettingsActionType
import ch.seesturm.pfadiseesturm.util.Binding
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
fun LeiterbereichView(
    leiterbereichViewModel: LeiterbereichViewModel,
    user: FirebaseHitobitoUser,
    appStateViewModel: AppStateViewModel,
    authViewModel: AuthViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController
) {

    val uiState by leiterbereichViewModel.state.collectAsStateWithLifecycle()
    val appState by appStateViewModel.state.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    // request permissions for notifications and location
    val locationPermissionResult = remember {
        Channel<Boolean>(Channel.RENDEZVOUS)
    }
    val notificationsPermissionResult = remember {
        Channel<Boolean>(Channel.RENDEZVOUS)
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionResult.trySend(isGranted)
    }
    val notificationsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsPermissionResult.trySend(isGranted)
    }
    val requestLocationPermission: suspend () -> Boolean = {
        suspendCancellableCoroutine { continuation ->
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            leiterbereichViewModel.viewModelScope.launch {
                val result = locationPermissionResult.receive()
                continuation.resumeWith(Result.success(result))
            }
        }
    }
    val requestNotificationsPermission: suspend () -> Boolean = {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                leiterbereichViewModel.viewModelScope.launch {
                    val result = notificationsPermissionResult.receive()
                    continuation.resumeWith(Result.success(result))
                }
            }
        }
        else {
            true
        }
    }

    LaunchedEffect(Unit) {
        leiterbereichViewModel.requestNotificationPermissionIfNecessary(requestNotificationsPermission)
    }

    SimpleAlert(
        isShown = uiState.showConfirmSchoepflialarmAlert,
        title = "Schöpflialarm",
        description = leiterbereichViewModel.schoepflialarmConfirmationText,
        icon = Icons.AutoMirrored.Outlined.SendToMobile,
        confirmButtonText = "Senden",
        onConfirm = {
            leiterbereichViewModel.sendSchoepflialarm(
                requestMessagingPermission = requestNotificationsPermission,
                requestLocationPermission = requestLocationPermission
            )
        },
        onDismiss = {
            leiterbereichViewModel.updateConfirmSchoepflialarmAlertVisibility(false)
        },
        isConfirmButtonCritical = true
    )
    AlertWithSettingsAction(
        isShown = uiState.showNotificationSettingsAlert,
        type = AlertWithSettingsActionType.Notifications,
        onDismiss = {
            leiterbereichViewModel.updateNotificationSettingsAlert(false)
        }
    )
    AlertWithSettingsAction(
        isShown = uiState.showLocationSettingsAlert,
        type = AlertWithSettingsActionType.Location,
        onDismiss = {
            leiterbereichViewModel.updateLocationSettingsAlert(false)
        }
    )

    val showEditProfileSheet = rememberSaveable { mutableStateOf(false) }
    val showSchoepflialarmSheet = rememberSaveable { mutableStateOf(false) }

    SimpleAlert(
        isShown = uiState.showSignOutAlert,
        title = "Möchtest du dich wirklich abmelden?",
        icon = Icons.Outlined.AccountBox,
        confirmButtonText = "Abmelden",
        onConfirm = {
            showEditProfileSheet.value = false
            authViewModel.signOut(user)
        },
        onDismiss = {
            leiterbereichViewModel.updateSignOutAlertVisibility(false)
        },
        isConfirmButtonCritical = true
    )
    SimpleAlert(
        isShown = uiState.showDeleteAccountAlert,
        title = "Möchtest du deinen Account wirklich löschen?",
        icon = Icons.Outlined.AccountBox,
        confirmButtonText = "Löschen",
        onConfirm = {
            showEditProfileSheet.value = false
            authViewModel.deleteAccount(user)
        },
        onDismiss = {
            leiterbereichViewModel.updateDeleteAccountAlertVisibility(false)
        },
        isConfirmButtonCritical = true
    )

    SimpleModalBottomSheet(
        show = Binding(
            get = { showEditProfileSheet.value },
            set = { showEditProfileSheet.value = it }
        ),
        detents = SheetDetents.LargeOnly,
        type = SheetScaffoldType.Title("Account"),
        appStateViewModel = appStateViewModel,
        keyboardResponse = ModalBottomSheetKeyboardResponse.None
    ) { _, viewModelStoreOwner ->
        EditProfileView(
            user = user,
            viewModel = viewModel<EditProfileViewModel>(
                viewModelStoreOwner = viewModelStoreOwner,
                factory = viewModelFactoryHelper {
                    EditProfileViewModel()
                }
            ),
            appStateViewModel = appStateViewModel,
            authViewModel = authViewModel,
            onDeleteAccount = {
                leiterbereichViewModel.updateDeleteAccountAlertVisibility(true)
            },
            onSignOut = {
                leiterbereichViewModel.updateSignOutAlertVisibility(true)
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }

    SimpleModalBottomSheet(
        show = Binding(
            get = { showSchoepflialarmSheet.value },
            set = { showSchoepflialarmSheet.value = it }
        ),
        detents = SheetDetents.All,
        type = SheetScaffoldType.Title("Schöpflialarm"),
        appStateViewModel = appStateViewModel,
        keyboardResponse = ModalBottomSheetKeyboardResponse.ScrollContent
    ) { _, _ ->
        SchoepflialarmSheet(
            schoepflialarmResult = leiterbereichViewModel.schoepflialarmResult,
            user = user,
            newSchoepflialarmMessage = uiState.schoepflialarmMessage,
            onSubmit = {
                leiterbereichViewModel.trySendSchoepflialarm()
            },
            onReaction = { reaction ->
                leiterbereichViewModel.sendSchoepflialarmReaction(reaction)
            },
            isSubmitButtonLoading = uiState.sendSchoepflialarmState.isLoading,
            isReactionButtonLoading = { reaction ->
                when (val localState = uiState.sendSchoepflialarmReactionState) {
                    is ActionState.Loading -> {
                        localState.action == reaction
                    }
                    else -> false
                }
            },
            onPushNotificationToggle = { isSwitchingOn ->
                leiterbereichViewModel.toggleNotificationTopic(
                    isSwitchingOn = isSwitchingOn,
                    requestPermission = requestNotificationsPermission
                )
            },
            notificationTopicsReadingState = uiState.notificationTopicsReadingState,
            togglePushNotificationState = uiState.toggleSchoepflialarmReactionsPushNotificationState
        )
    }

    LeiterbereichContentView(
        user = user,
        selectedStufen = uiState.selectedStufen,
        foodState = uiState.foodResult,
        termineState = uiState.termineState,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        accountNavController = accountNavController,
        isEditAccountButtonLoading = authState.deleteAccountButtonLoading,
        onRetryEvents = {
            leiterbereichViewModel.fetchNext3Events()
        },
        onToggleStufe = { stufe ->
            leiterbereichViewModel.toggleStufe(stufe)
        },
        schoepflialarmState = leiterbereichViewModel.schoepflialarmResult,
        showSchoeflialarmSheet = showSchoepflialarmSheet,
        showEditProfileSheet = showEditProfileSheet,
        isDarkTheme = appState.theme.isDarkTheme
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeiterbereichContentView(
    user: FirebaseHitobitoUser,
    selectedStufen: UiState<Set<SeesturmStufe>>,
    foodState: UiState<List<FoodOrder>>,
    termineState: UiState<List<GoogleCalendarEvent>>,
    schoepflialarmState: UiState<Schoepflialarm>,
    isEditAccountButtonLoading: Boolean,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    onRetryEvents: () -> Unit,
    onToggleStufe: (SeesturmStufe) -> Unit,
    showSchoeflialarmSheet: MutableState<Boolean>,
    showEditProfileSheet: MutableState<Boolean>,
    isDarkTheme: Boolean,
    calendar: SeesturmCalendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
    columnState: LazyListState = rememberLazyListState(),
    screenWidth: Dp = LocalConfiguration.current.screenWidthDp.dp
) {

    val stufenForDropdown: List<SeesturmStufe> = when (selectedStufen) {
        is UiState.Success -> selectedStufen.data.toList()
        else -> emptyList()
    }

    TopBarScaffold(
        topBarStyle = TopBarStyle.Large,
        title = "Schöpfli"
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalBottomPadding = 16.dp
        )

        val stickyOffsets = rememberStickyHeaderOffsets(columnState, 0)

        LazyColumn(
            state = columnState,
            contentPadding = combinedPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            item(
                key = "LeiterbereichProfileHeader"
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CircleProfilePictureView(
                        type = if (isEditAccountButtonLoading) {
                            CircleProfilePictureViewType.Loading
                        } else {
                            CircleProfilePictureViewType.Idle(user)
                        },
                        size = 60.dp,
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        showEditBadge = true,
                        onClick = {
                            showEditProfileSheet.value = true
                        }
                    )
                    Text(
                        text = "Willkommen, ${user.displayNameShort}!",
                        maxLines = 2,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    if (user.email != null) {
                        Text(
                            text = user.email,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }

            item(
                key = "LeiterbereichTopHorizontalScrollView"
            ) {
                LeiterbereichTopHorizontalScrollView(
                    onNavigateToFood = {
                        accountNavController.navigate(
                            AppDestination.MainTabView.Destinations.Account.Destinations.Food(
                                userId = user.userId,
                                userDisplayNameShort = user.displayNameShort,
                                calendar = calendar
                            )
                        )
                    },
                    foodState = foodState
                )
            }

            // schöpflialarm
            seesturmStickyHeader(
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

            when (schoepflialarmState) {
                UiState.Loading -> {
                    item(
                        key = "LeiterbereichSchoepflialarmLoadingView"
                    ) {
                        SchoepflialarmLoadingCardView(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "LeiterbereichSchoepflialarmErrorView"
                    ) {
                        ErrorCardView(
                            errorDescription = schoepflialarmState.message,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }
                }
                is UiState.Success -> {
                    item(
                        key = "LeiterbereichSchoepflialarmView"
                    ) {
                        SchoepflialarmCardView(
                            schoepflialarm = schoepflialarmState.data,
                            user = user,
                            onClick = { showSchoeflialarmSheet.value = true },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }
                }
            }

            // stufenbereich
            seesturmStickyHeader(
                uniqueKey = "leiterbereichStufenSection",
                stickyOffsets = stickyOffsets
            ) { _ ->
                MainSectionHeader(
                    sectionTitle = "Stufen",
                    icon = Icons.Default.Group,
                    type = MainSectionHeaderType.StufenButton(
                        selectedStufen = stufenForDropdown,
                        onToggle = onToggleStufe,
                        enabled = selectedStufen.isSuccess
                    ),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                )
            }

            when (selectedStufen) {
                UiState.Loading -> {
                    item(
                        key = "LeiterbereichStufenHorizontalScrollErrorView"
                    ) {
                        CustomCardView(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
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
                                        width = 0.9 * (screenWidth - 32.dp - 48.dp) / 2
                                    )
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "LeiterbereichStufenHorizontalScrollErrorView"
                    ) {
                        ErrorCardView(
                            errorDescription = selectedStufen.message,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }
                }
                is UiState.Success -> {
                    item(
                        key = "LeiterbereichStufenHorizontalScrollView"
                    ) {
                        LeiterbereichStufenScrollView(
                            stufen = selectedStufen.data,
                            totalContentWidth = screenWidth - 32.dp,
                            accountNavController = accountNavController,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
            }

            // termine
            seesturmStickyHeader(
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
            when (termineState) {
                UiState.Loading -> {
                    items(
                        count = 3,
                        key = { index ->
                            "LeiterbereichTermineLoadingCell$index"
                        }
                    ) {
                        AnlassLoadingCardView(
                            onAppear = null,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "LeiterbereichAnlaesseErrorCell"
                    ) {
                        ErrorCardView(
                            errorDescription = termineState.message,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        ) {
                            onRetryEvents()
                        }
                    }
                }
                is UiState.Success -> {
                    if (termineState.data.isNotEmpty()) {
                        items(
                            items = termineState.data,
                            key = { event ->
                                event.id
                            }
                        ) { event ->
                            AnlassCardView(
                                event = event,
                                calendar = calendar,
                                onClick = {
                                    accountNavController.navigate(
                                        AppDestination.MainTabView.Destinations.Account.Destinations.AccountTermineDetail(
                                            calendar = calendar,
                                            eventId = event.id,
                                            cacheIdentifier = MemoryCacheIdentifier.TryGetFromHomeCache
                                        )
                                    )
                                },
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
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

@SuppressLint("UnrememberedMutableState")
@Preview("Loading", uiMode = UI_MODE_NIGHT_NO)
@Preview("Loading", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LeiterbereichViewPreview1() {
    PfadiSeesturmTheme {
        LeiterbereichContentView(
            user = DummyData.user1,
            selectedStufen = UiState.Loading,
            foodState = UiState.Loading,
            termineState = UiState.Loading,
            schoepflialarmState = UiState.Loading,
            isEditAccountButtonLoading = true,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            onRetryEvents = {},
            onToggleStufe = {},
            showSchoeflialarmSheet = mutableStateOf(false),
            showEditProfileSheet = mutableStateOf(false),
            isDarkTheme = false
        )
    }
}
@SuppressLint("UnrememberedMutableState")
@Preview("Error", uiMode = UI_MODE_NIGHT_NO)
@Preview("Error", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LeiterbereichViewPreview2() {
    PfadiSeesturmTheme {
        LeiterbereichContentView(
            user = DummyData.user1,
            selectedStufen = UiState.Error("Schwerer Fehler"),
            foodState = UiState.Error("Schwerer Fehler"),
            termineState = UiState.Error("Schwerer Fehler"),
            schoepflialarmState = UiState.Error("Schwerer Fehler"),
            isEditAccountButtonLoading = false,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            onRetryEvents = {},
            onToggleStufe = {},
            showSchoeflialarmSheet = mutableStateOf(false),
            showEditProfileSheet = mutableStateOf(false),
            isDarkTheme = false
        )
    }
}
@SuppressLint("UnrememberedMutableState")
@Preview("Success (termine empty)", uiMode = UI_MODE_NIGHT_NO)
@Preview("Success (termine empty)", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LeiterbereichViewPreview3() {
    PfadiSeesturmTheme {
        LeiterbereichContentView(
            user = DummyData.user1,
            selectedStufen = UiState.Success(setOf(SeesturmStufe.Biber, SeesturmStufe.Wolf)),
            foodState = UiState.Success(DummyData.foodOrders),
            termineState = UiState.Success(emptyList()),
            schoepflialarmState = UiState.Success(DummyData.schoepflialarm),
            isEditAccountButtonLoading = false,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            onRetryEvents = {},
            onToggleStufe = {},
            showSchoeflialarmSheet = mutableStateOf(false),
            showEditProfileSheet = mutableStateOf(false),
            isDarkTheme = false
        )
    }
}
@SuppressLint("UnrememberedMutableState")
@Preview("Success", uiMode = UI_MODE_NIGHT_NO)
@Preview("Success", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LeiterbereichViewPreview4() {
    PfadiSeesturmTheme {
        LeiterbereichContentView(
            user = DummyData.user3,
            selectedStufen = UiState.Success(setOf(SeesturmStufe.Biber, SeesturmStufe.Wolf)),
            foodState = UiState.Success(DummyData.foodOrders),
            termineState = UiState.Success(listOf(DummyData.oneDayEvent, DummyData.multiDayEvent, DummyData.allDayOneDayEvent)),
            schoepflialarmState = UiState.Success(DummyData.schoepflialarm),
            isEditAccountButtonLoading = false,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            accountNavController = rememberNavController(),
            onRetryEvents = {},
            onToggleStufe = {},
            showSchoeflialarmSheet = mutableStateOf(false),
            showEditProfileSheet = mutableStateOf(false),
            isDarkTheme = false
        )
    }
}