package ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListFooter
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.lists.BasicListHeaderMode
import ch.seesturm.pfadiseesturm.presentation.common.lists.FormSection
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushNachrichtenVerwaltenView(
    viewModel: PushNachrichtenVerwaltenViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

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

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = "Push-Nachrichten",
        navigationAction = TopBarNavigationIcon.Back { navController.navigateUp() }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalStartPadding = 16.dp,
            additionalEndPadding = 16.dp,
            additionalTopPadding = 16.dp,
            additionalBottomPadding = 16.dp
        )

        PushNachrichtenVerwaltenContentView(
            subscribedTopicsState = uiState.subscribedTopicsState,
            actionState = uiState.actionState,
            onToggle = { topic, isOn ->
                viewModel.toggleTopic(
                    topic = topic,
                    isSwitchingOn = isOn,
                    requestPermission = requestPermission
                )
            },
            contentPadding = combinedPadding
        )
    }
}

@Composable
fun PushNachrichtenVerwaltenContentView(
    subscribedTopicsState: UiState<Set<SeesturmFCMNotificationTopic>>,
    actionState: ActionState<SeesturmFCMNotificationTopic>,
    onToggle: (SeesturmFCMNotificationTopic, Boolean) -> Unit,
    contentPadding: PaddingValues,
    additionalTopContent: (@Composable () -> Unit)? = null,
    additionalBottomContent: (@Composable () -> Unit)? = null,
    columnState: LazyListState = rememberLazyListState(),
) {

    val sections: Map<FormSection, List<SeesturmFCMNotificationTopic>> =
        mapOf(
            FormSection(
                header = "Aktuell",
                footer = "Erhalte eine Benachrichtigung wenn ein neuer Post veröffentlicht wird"
            )  to listOf(SeesturmFCMNotificationTopic.Aktuell),
            FormSection(
                header = "Nächste Aktivität",
                footer = "Erhalte eine Benachrichtigung wenn die Infos zur nächsten Aktivität veröffentlicht werden"
            ) to listOf(
                SeesturmFCMNotificationTopic.BiberAktivitaeten,
                SeesturmFCMNotificationTopic.WolfAktivitaeten,
                SeesturmFCMNotificationTopic.PfadiAktivitaeten,
                SeesturmFCMNotificationTopic.PioAktivitaeten
            )
        )

    GroupedColumn(
        state = columnState,
        contentPadding = contentPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        if (additionalTopContent != null) {
            section {
                customItem(
                    key = "additionalTopContent"
                ) {
                    additionalTopContent()
                }
            }
        }

        when (subscribedTopicsState) {
            is UiState.Error -> {
                section {
                    customItem(
                        key = "PushNachrichtenVerwaltenErrorCell"
                    ) {
                        ErrorCardView(
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = subscribedTopicsState.message
                        )
                    }
                }
            }
            is UiState.Loading, is UiState.Success -> {
                sections.forEach { (section, topics) ->
                    section(
                        header = {
                            BasicListHeader(
                                mode = BasicListHeaderMode.Normal(section.header.uppercase())
                            )
                        },
                        footer = {
                            BasicListFooter(
                                mode = BasicListHeaderMode.Normal(
                                    text = section.footer
                                ),
                                maxLines = Int.MAX_VALUE,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    ) {
                        topics.forEach { topic ->
                            pushNotifcationToggle(
                                topic = topic,
                                state = subscribedTopicsState,
                                actionState = actionState,
                                onToggle = { isOn ->
                                    onToggle(topic, isOn)
                                },
                                key = "PushNachrichtenVerwaltenCell${topic.topic}"
                            )
                        }
                    }
                }
            }
        }

        if (additionalBottomContent != null) {
            section {
                customItem(
                    key = "additionalBottomContent"
                ) {
                    additionalBottomContent()
                }
            }
        }
    }
}

@Preview("Loading")
@Composable
private fun PushNachrichtenVerwaltenViewPreview1() {
    PfadiSeesturmTheme {
        PushNachrichtenVerwaltenContentView(
            subscribedTopicsState = UiState.Loading,
            actionState = ActionState.Idle,
            onToggle = { _, _ -> },
            contentPadding = PaddingValues(16.dp)
        )
    }
}
@Preview("Error")
@Composable
private fun PushNachrichtenVerwaltenViewPreview2() {
    PfadiSeesturmTheme {
        PushNachrichtenVerwaltenContentView(
            subscribedTopicsState = UiState.Error("Schwerer Fehler"),
            actionState = ActionState.Idle,
            onToggle = { _, _ -> },
            contentPadding = PaddingValues(16.dp)
        )
    }
}
@Preview("Success")
@Composable
private fun PushNachrichtenVerwaltenViewPreview3() {
    PfadiSeesturmTheme {
        PushNachrichtenVerwaltenContentView(
            subscribedTopicsState = UiState.Success(
                setOf(
                    SeesturmFCMNotificationTopic.Aktuell,
                    SeesturmFCMNotificationTopic.PfadiAktivitaeten
                )
            ),
            actionState = ActionState.Loading(SeesturmFCMNotificationTopic.WolfAktivitaeten),
            onToggle = { _, _ -> },
            contentPadding = PaddingValues(16.dp)
        )
    }
}