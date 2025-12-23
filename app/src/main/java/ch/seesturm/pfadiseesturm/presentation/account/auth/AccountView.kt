package ch.seesturm.pfadiseesturm.presentation.account.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarHostType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.types.SeesturmAuthState
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import kotlinx.coroutines.launch

@Composable
fun AccountView(
    appStateViewModel: AppStateViewModel,
    bottomNavigationInnerPadding: PaddingValues,
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
        authState = appState.authState,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onAuthenticate = {
            appStateViewModel.startAuthFlow()
        },
        onResetAuthState = {
            appStateViewModel.resetAuthState()
        },
        leiterbereich = { user ->
            leiterbereich(user)()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountContentView(
    authState: SeesturmAuthState,
    bottomNavigationInnerPadding: PaddingValues,
    onAuthenticate: () -> Unit,
    onResetAuthState: () -> Unit,
    leiterbereich: @Composable (FirebaseHitobitoUser) -> Unit,
    columnState: LazyListState = rememberLazyListState()
) {

    when (authState) {
        is SeesturmAuthState.SignedOut -> {

            TopBarScaffold(
                topBarStyle = TopBarStyle.Large,
                title = "Account",
                snackbarType = when (authState.state) {
                    ActionState.Idle -> SeesturmSnackbarHostType.StaticInfoSnackbar(
                        message = "Die Anmeldung ist nur fürs Leitungsteam der Pfadi Seesturm möglich"
                    )
                    else -> SeesturmSnackbarHostType.Default
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
                    when (authState.state) {
                        is ActionState.Success, is ActionState.Loading, ActionState.Idle -> {
                            item(
                                key = "AccountSignedOutView"
                            ) {
                                LoggedOutView(
                                    authState = authState,
                                    onAuthenticate = onAuthenticate,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                        is ActionState.Error -> {
                            item(
                                key = "AccountErrorView"
                            ) {
                                AuthErrorView(
                                    message = authState.state.message,
                                    onResetAuthState = onResetAuthState,
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
            leiterbereich(authState.user)
        }
    }
}

@Preview("Idle")
@Composable
private fun AccountViewPreview1() {
    PfadiSeesturmTheme {
        AccountContentView(
            authState = SeesturmAuthState.SignedOut(ActionState.Idle),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onAuthenticate = {},
            onResetAuthState = {},
            leiterbereich = {}
        )
    }
}
@Preview("Loading")
@Composable
private fun AccountViewPreview2() {
    PfadiSeesturmTheme {
        AccountContentView(
            authState = SeesturmAuthState.SignedOut(ActionState.Loading(Unit)),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onAuthenticate = {},
            onResetAuthState = {},
            leiterbereich = {}
        )
    }
}
@Preview("Error")
@Composable
private fun AccountViewPreview3() {
    PfadiSeesturmTheme {
        AccountContentView(
            authState = SeesturmAuthState.SignedOut(ActionState.Error(Unit, "Hallo")),
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            onAuthenticate = {},
            onResetAuthState = {},
            leiterbereich = {}
        )
    }
}