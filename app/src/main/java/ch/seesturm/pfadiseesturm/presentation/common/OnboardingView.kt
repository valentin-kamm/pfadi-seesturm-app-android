package ch.seesturm.pfadiseesturm.presentation.common

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarContentView
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarHost
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.AlertWithSettingsAction
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.AlertWithSettingsActionType
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenContentView
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenViewModel
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
fun OnboardingView(
    viewModel: PushNachrichtenVerwaltenViewModel,
    onSetHasSeenOnboardingView: () -> Unit,
    navController: NavController
) {

    BackHandler(enabled = true) {}

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val permissionResult = remember {
        Channel<Boolean>(Channel.RENDEZVOUS)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionResult.trySend(isGranted)
    }
    val requestPermission: suspend () -> Boolean = {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            true
        }
        else {
            suspendCancellableCoroutine { continuation ->
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                viewModel.viewModelScope.launch {
                    val result = permissionResult.receive()
                    continuation.resumeWith(Result.success(result))
                }
            }
        }
    }

    AlertWithSettingsAction(
        isShown = uiState.showSettingsAlert,
        type = AlertWithSettingsActionType.Notifications,
        onDismiss = {
            viewModel.updateAlertVisibility(false)
        }
    )

    // shared preference from previous version of app to determine whether it is an update or a fresh install
    val hadPreviousAppVersionInstalled = remember {
        val prefs = context.getSharedPreferences("ch.seesturm.pfadiseesturm", Context.MODE_PRIVATE)
        prefs.getBoolean("alreadySeenOnboardingScreen", false)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    ObserveAsEvents(
        flow = SnackbarController.events,
        key1 = snackbarHostState
    ) { event ->
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val snackbarResult = snackbarHostState.showSnackbar(
                visuals = event
            )
            if (snackbarResult == SnackbarResult.Dismissed) {
                event.onDismiss
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SeesturmSnackbarHost(
                location = SeesturmSnackbarLocation.Default
            )
        }
    ) { innerPadding ->
        OnboardingContentView(
            onHideOnboardingScreen = {
                onSetHasSeenOnboardingView()
                navController.navigate(AppDestination.MainTabView) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            subscribedTopicsState = uiState.subscribedTopicsState,
            actionState = uiState.actionState,
            onToggle = { topic, isOn ->
                viewModel.toggleTopic(
                    topic = topic,
                    isSwitchingOn = isOn,
                    requestPermission = requestPermission
                )
            },
            hadPreviousAppVersionInstalled = hadPreviousAppVersionInstalled,
            innerPadding = innerPadding
        )
    }
}

@Composable
private fun OnboardingContentView(
    onHideOnboardingScreen: () -> Unit,
    subscribedTopicsState: UiState<Set<SeesturmFCMNotificationTopic>>,
    actionState: ActionState<SeesturmFCMNotificationTopic>,
    onToggle: (SeesturmFCMNotificationTopic, Boolean) -> Unit,
    hadPreviousAppVersionInstalled: Boolean,
    innerPadding: PaddingValues,
    pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 5 }
    ),
    scrollState: ScrollState = rememberScrollState()
) {

    val firstTab = 0
    val lastTab = 4
    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
            ) { index ->

                val topPadding = if (index == 0) {
                    0.dp
                }
                else {
                    innerPadding.calculateTopPadding() + 16.dp
                }
                val startPadding = if (index == 0) {
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr)
                }
                else {
                    innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp
                }
                val endPadding = if (index == 0) {
                    innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                }
                else {
                    innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp
                }

                if (index in 0..3) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(
                                top = topPadding,
                                bottom = 0.dp,
                                start = startPadding,
                                end = endPadding
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp, alignment = Alignment.Top)
                    ) {
                        when (index) {
                            0 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.onboarding_welcome_image),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                Text(
                                    text = "Willkommen in der Pfadi Seesturm App!",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                                Text(
                                    text = "Mit der Pfadi Seesturm App sind alle Informationen zur Pfadi Seesturm nur einen Fingertipp entfernt.\n\nDie wichtigsten Funktionen stellen wir dir nun vor.",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                            }
                            1 -> {
                                Row {
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                    Image(
                                        painter = painterResource(id = R.drawable.onboarding_android_aktivitaet),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .padding(top = 16.dp)
                                            .shadow(
                                                elevation = 5.dp,
                                                shape = RoundedCornerShape(16.dp),
                                                clip = true,
                                                ambientColor = MaterialTheme.colorScheme.inverseSurface.copy(
                                                    alpha = 0.6f
                                                ),
                                                spotColor = MaterialTheme.colorScheme.inverseSurface.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                            .clip(RoundedCornerShape(16.dp))
                                            .heightIn(max = 250.dp)

                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                }
                                Text(
                                    text = "Nächste Aktivität",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                Text(
                                    text = "Unter «Nächste Aktivität» findest du die Infos zu den anstehenden Aktivitäten aller Stufen.\n\nDie Infos werden jeweils im Verlauf der Woche aufgeschaltet und können in deinen persönlichen Kalender importiert werden. So werden sie laufend synchronisiert.",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                            2 -> {
                                Row {
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                    Image(
                                        painter = painterResource(id = R.drawable.onboarding_android_abmelden),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .padding(top = 16.dp)
                                            .shadow(
                                                elevation = 5.dp,
                                                shape = RoundedCornerShape(16.dp),
                                                clip = true,
                                                ambientColor = MaterialTheme.colorScheme.inverseSurface.copy(
                                                    alpha = 0.6f
                                                ),
                                                spotColor = MaterialTheme.colorScheme.inverseSurface.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                            .clip(RoundedCornerShape(16.dp))
                                            .heightIn(max = 250.dp)

                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                }
                                Text(
                                    text = "An- und Abmeldungen",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                Text(
                                    text = "Melde dich direkt in der Pfadi Seesturm App ab, wenn du einmal nicht an eine Aktivität kommen kannst.\n\nDu kannst Personen, die du häufig an- oder abmeldest, in der App speichern. So musst du die Angaben nicht jedes Mal neu eintragen.",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                            3 -> {
                                Row {
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                    Image(
                                        painter = painterResource(id = R.drawable.onboarding_android_anlaesse),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .padding(top = 16.dp)
                                            .shadow(
                                                elevation = 5.dp,
                                                shape = RoundedCornerShape(16.dp),
                                                clip = true,
                                                ambientColor = MaterialTheme.colorScheme.inverseSurface.copy(
                                                    alpha = 0.6f
                                                ),
                                                spotColor = MaterialTheme.colorScheme.inverseSurface.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                            .clip(RoundedCornerShape(16.dp))
                                            .heightIn(max = 250.dp)

                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                }
                                Text(
                                    text = "Anlässe",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                Text(
                                    text = "Der Pfadi-Kalender zeigt dir auf einen Blick alle wichtigen Anlässe der Pfadi Seesturm.\n\nDamit du nie mehr einen Anlass verpasst, kannst du den Kalender abonnieren. So werden alle Anlässe automatisch deinem persönlichen Kalender hinzugefügt.",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                            else -> {

                            }
                        }
                    }
                }
                else {
                    PushNachrichtenVerwaltenContentView(
                        subscribedTopicsState = subscribedTopicsState,
                        actionState = actionState,
                        onToggle = onToggle,
                        contentPadding = PaddingValues(
                            top = topPadding,
                            bottom = 0.dp,
                            start = endPadding,
                            end = startPadding
                        ),
                        additionalTopContent = {
                            Text(
                                text = "Push-Nachrichten",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )
                        },
                        additionalBottomContent = if (hadPreviousAppVersionInstalled) {
                            {
                                SeesturmSnackbarContentView(
                                    snackbar = SeesturmSnackbar.Info(
                                        message = "Push-Nachrichten müssen neu abonniert werden, da eine neue App Version installiert wurde.",
                                        onDismiss = {},
                                        location = SeesturmSnackbarLocation.Default,
                                        allowManualDismiss = false,
                                        duration = SnackbarDuration.Indefinite
                                    ),
                                    onClick = null,
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                )
                            }
                        }
                        else null
                    )
                }
            }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                if (pagerState.currentPage > firstTab) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        content = {
                            Text("Zurück")
                        }
                    )
                }
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
                if (pagerState.currentPage < lastTab) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        content = {
                            Text("Weiter")
                        }
                    )
                }
                else {
                    SeesturmButton(
                        type = SeesturmButtonType.Primary,
                        title = "Fertig",
                        onClick = onHideOnboardingScreen
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentSize()
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    }
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingViewPreview() {
    PfadiSeesturmTheme {
        OnboardingContentView(
            onHideOnboardingScreen = {},
            subscribedTopicsState = UiState.Success(emptySet()),
            actionState = ActionState.Idle,
            onToggle = { _, _ -> },
            hadPreviousAppVersionInstalled = true,
            innerPadding = PaddingValues(0.dp)
        )
    }
}