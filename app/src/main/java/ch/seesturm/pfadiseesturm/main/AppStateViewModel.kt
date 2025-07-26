package ch.seesturm.pfadiseesturm.main

import androidx.activity.result.ActivityResult
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.service.AuthService
import ch.seesturm.pfadiseesturm.domain.data_store.service.OnboardingService
import ch.seesturm.pfadiseesturm.domain.data_store.service.SelectedThemeService
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.domain.fcm.service.FCMService
import ch.seesturm.pfadiseesturm.presentation.account.auth.AuthIntentController
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarType
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.SeesturmAppTheme
import ch.seesturm.pfadiseesturm.util.types.SeesturmAuthState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppStateViewModel(
    private val authService: AuthService,
    private val themeService: SelectedThemeService,
    private val fcmService: FCMService,
    private val onboardingService: OnboardingService,
    private val wordpressApi: WordpressApi,
    private val currentAppBuild: Int?
): ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    init {
        runOnAppStart()
    }

    private fun runOnAppStart() {

        startListeningToSelectedTheme()
        navigateToOnboardingViewIfNecessary()

        viewModelScope.launch {

            val reauthJob = async { reauthenticateOnAppStart() }
            val versionCheckJob = async { checkMinimumRequiredAppBuild() }

            awaitAll(reauthJob, versionCheckJob)

            // re-subscribe to schöepflialarm topic in the background after re-auth is complete
            val isHitobitoUser = authService.isCurrentUserHitobitoUser()
            if (isHitobitoUser) {
                fcmService.subscribe(
                    topic = SeesturmFCMNotificationTopic.Schoepflialarm,
                    requestPermission = { true }
                )
            }
        }
    }

    fun startAuthFlow() {
        updateAuthState(SeesturmAuthState.SignedOut(state = ActionState.Loading(Unit)))
        viewModelScope.launch {
            when (val result = authService.startAuthFlow()) {
                is SeesturmResult.Error -> {
                    updateAuthState(SeesturmAuthState.SignedOut(state = ActionState.Error(action = Unit, message = result.error.defaultMessage)))
                }
                is SeesturmResult.Success -> {
                    AuthIntentController.launchIntent(result.data)
                }
            }
        }
    }

    fun finishAuthFlow(activityResult: ActivityResult) {

        viewModelScope.launch {
            when (val result = authService.finishAuthFlow(activityResult)) {
                is SeesturmResult.Error -> {
                    when (result.error) {
                        is DataError.AuthError.CANCELLED -> {
                            resetAuthState()
                        }
                        else -> {
                            updateAuthState(SeesturmAuthState.SignedOut(state = ActionState.Error(action = Unit, message = result.error.defaultMessage)))
                        }
                    }
                }
                is SeesturmResult.Success -> {
                    updateAuthState(SeesturmAuthState.SignedInWithHitobito(user = result.data, state = ActionState.Idle))
                }
            }
        }
    }

    private suspend fun reauthenticateOnAppStart() {

        updateAuthState(SeesturmAuthState.SignedOut(state = ActionState.Loading(Unit)))
        when (val result = authService.reauthenticateOnAppStart()) {
            is SeesturmResult.Error -> {
                updateAuthState(
                    SeesturmAuthState.SignedOut(
                        state = ActionState.Idle
                    )
                )
            }
            is SeesturmResult.Success -> {
                updateAuthState(
                    SeesturmAuthState.SignedInWithHitobito(
                        user = result.data,
                        state = ActionState.Idle
                    )
                )
            }
        }
    }

    fun signOut(user: FirebaseHitobitoUser) {

        updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, state = ActionState.Loading(Unit)))
        viewModelScope.launch {
            when (val result = authService.signOut()) {
                is SeesturmResult.Error -> {
                    updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Error(Unit, result.error.defaultMessage)))
                    viewModelScope.launch {
                        SnackbarController.sendEvent(
                            event = SeesturmSnackbarEvent(
                                message = result.error.defaultMessage,
                                duration = SnackbarDuration.Indefinite,
                                type = SeesturmSnackbarType.Error,
                                allowManualDismiss = true,
                                onDismiss = {
                                    updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Idle))
                                },
                                showInSheetIfPossible = false
                            )
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    updateAuthState(SeesturmAuthState.SignedOut(ActionState.Idle))
                }
            }
        }
    }

    fun deleteAccount(user: FirebaseHitobitoUser) {

        updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Loading(Unit)))
        viewModelScope.launch {
            when (val result = authService.deleteAccount(user)) {
                is SeesturmResult.Error -> {
                    updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Error(Unit, result.error.defaultMessage)))
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = result.error.defaultMessage,
                            duration = SnackbarDuration.Indefinite,
                            type = SeesturmSnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {
                                updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Idle))
                            },
                            showInSheetIfPossible = false
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    updateAuthState(SeesturmAuthState.SignedOut(ActionState.Idle))
                }
            }
        }
    }

    fun resetAuthState() {
        updateAuthState(SeesturmAuthState.SignedOut(state = ActionState.Idle))
    }

    private fun updateAuthState(newAuthState: SeesturmAuthState) {
        _state.update {
            it.copy(
                authState = newAuthState
            )
        }
    }

    val isSheetVisibile: Boolean
        get() = state.value.sheetContent != null

    fun updateSheetContent(content: BottomSheetContent?) {
        _state.update {
            it.copy(
                sheetContent = content
            )
        }
    }

    private fun navigateToOnboardingViewIfNecessary() {
        viewModelScope.launch {
            val result = onboardingService.mustShowOnboardingView()
            if (result is SeesturmResult.Success && result.data) {
                OnboardingController.showOnboardingView()
            }
        }
    }
    fun setHasSeenOnboarding() {
        viewModelScope.launch {
            onboardingService.setMustShowOnboardingView(mustShow = false)
        }
    }

    private fun startListeningToSelectedTheme() {
        themeService.readTheme().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            theme = SeesturmAppTheme.System
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            theme = result.data
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateTheme(theme: SeesturmAppTheme) {
        viewModelScope.launch {
            when (themeService.updateTheme(theme)) {
                is SeesturmResult.Error -> {
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = "Das Erscheinungsbild konnte nicht geändert werden. Versuche es später erneut.",
                            duration = SnackbarDuration.Long,
                            type = SeesturmSnackbarType.Error,
                            allowManualDismiss = true,
                            onDismiss = {},
                            showInSheetIfPossible = false
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    // do nothing
                }
            }
        }
    }

    private suspend fun checkMinimumRequiredAppBuild() {

        try {
            val minimimRequiredBuild = wordpressApi.getMinimumRequiredAppBuild()

            if (currentAppBuild != null && currentAppBuild < minimimRequiredBuild.android) {
                _state.update {
                    it.copy(
                        showAppVersionCheckOverlay = true
                    )
                }
            }
        }
        catch (e: Exception) {
            println("Minimum required app build could not be checked.")
        }
    }

    fun updateAllowedOrientation(orientation: AllowedOrientation) {
        _state.update {
            it.copy(
                allowedOrientation = orientation
            )
        }
    }
}