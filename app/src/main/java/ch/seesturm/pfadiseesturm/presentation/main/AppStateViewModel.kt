package ch.seesturm.pfadiseesturm.presentation.main

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.service.AuthService
import ch.seesturm.pfadiseesturm.domain.data_store.service.SelectedThemeService
import ch.seesturm.pfadiseesturm.presentation.account.auth.components.AuthIntentController
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.AktuellListState
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarEvent
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarType
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.SeesturmAuthState
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppStateViewModel(
    private val authService: AuthService,
    private val themeService: SelectedThemeService
): ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    init {
        startListeningToSelectedTheme()
        reauthenticate()
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

    fun finishAuthFlow(result: ActivityResult) {
        viewModelScope.launch {
            when (val result = authService.finishAuthFlow(result)) {
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

    private fun reauthenticate() {
        viewModelScope.launch {
            updateAuthState(SeesturmAuthState.SignedOut(state = ActionState.Loading(Unit)))
            when (val result = authService.reauthenticate()) {
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
    }

    fun signOut(user: FirebaseHitobitoUser) {
        updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, state = ActionState.Loading(Unit)))
        when (val result = authService.signOut()) {
            is SeesturmResult.Error -> {
                updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Error(Unit, result.error.defaultMessage)))
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = result.error.defaultMessage,
                            duration = SnackbarDuration.Indefinite,
                            type = SnackbarType.Error,
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

    fun deleteAccount(user: FirebaseHitobitoUser) {
        updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Loading(Unit)))
        viewModelScope.launch {
            when (val result = authService.deleteUser(user)) {
                is SeesturmResult.Error -> {
                    updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Error(Unit, result.error.defaultMessage)))
                    SnackbarController.sendEvent(
                        event = SeesturmSnackbarEvent(
                            message = result.error.defaultMessage,
                            duration = SnackbarDuration.Indefinite,
                            type = SnackbarType.Error,
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
                            type = SnackbarType.Error,
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
}