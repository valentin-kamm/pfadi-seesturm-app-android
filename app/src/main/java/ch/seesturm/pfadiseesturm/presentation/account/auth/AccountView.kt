package ch.seesturm.pfadiseesturm.presentation.account.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.main.AuthViewModel
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarContentView
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.types.SeesturmAuthState
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import kotlinx.coroutines.launch

@Composable
fun AccountView(
    authViewModel: AuthViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    leiterbereich: (FirebaseHitobitoUser) -> @Composable () -> Unit
) {

    val authState by authViewModel.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val loginLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        authViewModel.finishAuthFlow(result)
    }

    ObserveAsEvents(
        flow = AuthIntentController.intents
    ) { intent ->
        coroutineScope.launch {
            loginLauncher.launch(intent)
        }
    }

    AccountContentView(
        authState = authState,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        onAuthenticate = {
            authViewModel.startAuthFlow()
        },
        onResetAuthState = {
            authViewModel.resetAuthState()
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

    // animation of the static snackbar
    val infoSnackbarVisibility = remember {
        MutableTransitionState(false).apply { targetState = true }
    }
    LaunchedEffect(key1 = authState) {
        when (authState) {
            is SeesturmAuthState.SignedOut -> {
                infoSnackbarVisibility.targetState = authState.state is ActionState.Idle
            }
            else -> {
                infoSnackbarVisibility.targetState = false
            }
        }
    }

    when (authState) {
        is SeesturmAuthState.SignedOut -> {

            TopBarScaffold(
                topBarStyle = TopBarStyle.Large,
                title = "Account"
            ) { topBarInnerPadding ->

                val combinedPadding = bottomNavigationInnerPadding.intersectWith(
                    other = topBarInnerPadding,
                    layoutDirection = LayoutDirection.Ltr,
                    additionalTopPadding = 16.dp,
                    additionalBottomPadding = 16.dp,
                    additionalEndPadding = 16.dp,
                    additionalStartPadding = 16.dp
                )

                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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
                    AnimatedVisibility(
                        visibleState = infoSnackbarVisibility,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        SeesturmSnackbarContentView(
                            snackbar = SeesturmSnackbar.Info(
                                message = "Die Anmeldung ist nur fürs Leitungsteam der Pfadi Seesturm möglich",
                                onDismiss = {},
                                location = SeesturmSnackbarLocation.Default,
                                allowManualDismiss = false,
                                duration = SnackbarDuration.Indefinite
                            ),
                            onClick = null,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = combinedPadding.calculateBottomPadding())
                        )
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