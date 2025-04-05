package ch.seesturm.pfadiseesturm.presentation.account.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.presentation.account.auth.components.AuthIntentController
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichViewModel
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffoldStaticSnackbarType
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.ObserveAsEvents
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarType
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.main.AppState
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.accountModule
import ch.seesturm.pfadiseesturm.util.SeesturmAuthState
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import ch.seesturm.pfadiseesturm.util.state.ActionState
import kotlinx.coroutines.launch

@Composable
fun AccountView(
    appStateViewModel: AppStateViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    calendar: SeesturmCalendar,
    accountNavController: NavController,
    leiterbereich: (FirebaseHitobitoUser) -> @Composable () -> Unit
) {

    val appState by appStateViewModel.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val loginLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        appStateViewModel.finishAuthFlow(result)
    }

    ObserveAsEvents(
        flow = AuthIntentController.intents
    ) { intent ->
        coroutineScope.launch {
            loginLauncher.launch(intent)
        }
    }

    AccountContentView(
        appState = appState,
        buttonIsLoading = appState.authState.signInButtonIsLoading,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        accountNavController = accountNavController,
        onStartAuthentication = {
            appStateViewModel.startAuthFlow()
        },
        onErrorButtonClick = {
            appStateViewModel.resetAuthState()
        },
        leiterbereich = { user ->
            leiterbereich(user)()
        }
    )
}

@Composable
private fun AccountContentView(
    appState: AppState,
    buttonIsLoading: Boolean,
    bottomNavigationInnerPadding: PaddingValues,
    accountNavController: NavController,
    onStartAuthentication: () -> Unit,
    onErrorButtonClick: () -> Unit,
    leiterbereich: @Composable (FirebaseHitobitoUser) -> Unit,
    columnState: LazyListState = rememberLazyListState()
) {
    when (appState.authState) {
        is SeesturmAuthState.SignedOut -> {
            TopBarScaffold(
                topBarStyle = TopBarStyle.Large,
                title = "Account",
                staticSnackbar = when(appState.authState.state) {
                    is ActionState.Idle -> {
                        TopBarScaffoldStaticSnackbarType.Show(
                            snackbarEvent = SeesturmSnackbarEvent(
                                message = "Die Anmeldung ist nur fürs Leitungsteam der Pfadi Seesturm möglich",
                                duration = SnackbarDuration.Indefinite,
                                type = SnackbarType.Info,
                                allowManualDismiss = false,
                                onDismiss = {},
                                showInSheetIfPossible = false
                            ),
                            additionalBottomPadding = bottomNavigationInnerPadding.calculateBottomPadding()
                        )
                    }
                    else -> {
                        TopBarScaffoldStaticSnackbarType.None
                    }
                }
            ) { topBarInnerPadding ->

                val combinedPadding = bottomNavigationInnerPadding.intersectWith(
                    other = topBarInnerPadding,
                    layoutDirection = LayoutDirection.Ltr,
                    additionalTopPadding = 16.dp,
                    additionalBottomPadding = 16.dp,
                    additionalEndPadding = 16.dp,
                    additionalStartPadding = 16.dp
                )

                LazyColumn(
                    state = columnState,
                    contentPadding = combinedPadding,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    when (appState.authState.state) {
                        is ActionState.Success, is ActionState.Loading, ActionState.Idle -> {
                            item(
                                key = "AccountSignedOutView"
                            ) {
                                LoggedOutView(
                                    isLoading = buttonIsLoading,
                                    onLogin = {
                                        onStartAuthentication()
                                    }
                                )
                            }
                        }
                        is ActionState.Error -> {
                            item(
                                key = "AccountErrorView"
                            ) {
                                AuthErrorView(
                                    message = appState.authState.state.message,
                                    onButtonClick = {
                                        onErrorButtonClick()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
        is SeesturmAuthState.SignedInWithHitobito -> {
            leiterbereich(appState.authState.user)
        }
    }
}

@Preview
@Composable
private fun AccountViewPreview() {
    AccountContentView(
        appState = AppState(
            authState = SeesturmAuthState.SignedOut(state = ActionState.Idle)
        ),
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        accountNavController = rememberNavController(),
        buttonIsLoading = false,
        onStartAuthentication = {},
        onErrorButtonClick = {},
        leiterbereich = {}
    )
}