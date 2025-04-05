package ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.data.fcm.FCMApiImpl
import ch.seesturm.pfadiseesturm.data.fcm.repository.FCMRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.fcm.service.FCMSubscriptionService
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.forms.BasicListHeader
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormSection
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.FakeDataStore
import ch.seesturm.pfadiseesturm.util.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.state.UiState
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushNachrichtenVerwaltenView(
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    viewModel: PushNachrichtenVerwaltenViewModel,
    columnState: LazyListState = rememberLazyListState(),
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    // to request notification permission
    val permissionResult = remember {
        Channel<Boolean>(Channel.RENDEZVOUS)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionResult.trySend(isGranted)
    }
    val requestPermission: suspend () -> Boolean = {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                viewModel.viewModelScope.launch {
                    val result = permissionResult.receive()
                    continuation.resumeWith(Result.success(result))
                }
            }
        }
        else {
            true
        }
    }

    // alert to prompt user to enable notifications in settings
    PushNotificationsSettingsAlert(
        isShown = uiState.showSettingsAlert,
        onDismiss = {
            viewModel.updateAlertVisibility(false)
        }
    )

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

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = "Push-Nachrichten",
        backNavigationAction = {
            navController.popBackStack()
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

            when (val localState = uiState.readingState) {
                is UiState.Error -> {
                    item(
                        key = "PushNachrichtenVerwaltenErrorCell"
                    ) {
                        CardErrorView(
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = localState.message,
                            modifier = Modifier
                                .animateItem()
                        )
                    }
                }
                is UiState.Loading -> {
                    sections.forEach { (section, topics) ->
                        item(
                            key = "PushNachrichtenVerwaltenHeader${section.header}"
                        ) {
                            BasicListHeader(
                                title = section.header.uppercase(),
                                modifier = Modifier
                                    .animateItem()
                            )
                        }
                        items(
                            items = topics,
                            key = { topic ->
                                "PushNachrichtenVerwaltenCell${topic.topic}"
                            }
                        ) { topic ->
                            PushNotificationToggle(
                                items = topics,
                                topic = topic,
                                state = uiState.readingState,
                                actionState = uiState.actionState,
                                isOn = false,
                                modifier = Modifier
                                    .animateItem()
                            ) { }
                        }
                        item(
                            key = "PushNachrichtenVerwaltenFooter${section.header}"
                        ) {
                            Text(
                                text = section.footer,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                                    .padding(bottom = 16.dp)
                                    .alpha(0.4f)
                            )
                        }
                    }
                }
                is UiState.Success -> {
                    sections.forEach { (section, topics) ->
                        item(
                            key = "PushNachrichtenVerwaltenHeader${section.header}"
                        ) {
                            BasicListHeader(
                                title = section.header.uppercase(),
                                modifier = Modifier
                                    .animateItem()
                            )
                        }
                        items(
                            items = topics,
                            key = { topic ->
                                "PushNachrichtenVerwaltenCell${topic.topic}"
                            }
                        ) { topic ->
                            PushNotificationToggle(
                                items = topics,
                                topic = topic,
                                state = uiState.readingState,
                                actionState = uiState.actionState,
                                isOn = localState.data.contains(topic),
                                modifier = Modifier
                                    .animateItem()
                            ) { newState ->
                                viewModel.toggleTopic(
                                    topic = topic,
                                    isSwitchingOn = newState,
                                    requestPermission = requestPermission
                                )
                            }
                        }
                        item(
                            key = "PushNachrichtenVerwaltenFooter${section.header}"
                        ) {
                            Text(
                                text = section.footer,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                                    .padding(bottom = 16.dp)
                                    .alpha(0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
private fun PushNachrichtenVerwaltenViewPreview() {
    PushNachrichtenVerwaltenView(
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        navController = rememberNavController(),
        viewModel = PushNachrichtenVerwaltenViewModel(
            service = FCMSubscriptionService(
                repository = FCMRepositoryImpl(
                    api = FCMApiImpl(
                        messaging = Firebase.messaging
                    ),
                    dataStore = FakeDataStore(
                        initialValue = SeesturmPreferencesDao()
                    )
                ),
                context = LocalContext.current
            )
        )
    )
}