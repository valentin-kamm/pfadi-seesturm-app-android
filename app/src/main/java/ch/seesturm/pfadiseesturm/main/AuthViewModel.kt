package ch.seesturm.pfadiseesturm.main

import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.service.AuthService
import ch.seesturm.pfadiseesturm.presentation.account.auth.AuthIntentController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.SeesturmAuthState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authService: AuthService
): ViewModel() {

    private val _state = MutableStateFlow<SeesturmAuthState>(SeesturmAuthState.SignedOut(state = ActionState.Idle))
    val state = _state.asStateFlow()

    private var userListeningJob: Job? = null

    init {
        viewModelScope.launch {
            reauthenticateOnAppStart()
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
                    startListeningToUser(userId = result.data.userId)
                }
            }
        }
    }

    private suspend fun reauthenticateOnAppStart() {

        updateAuthState(SeesturmAuthState.SignedOut(state = ActionState.Loading(Unit)))
        when (val result = authService.reauthenticate(resubscribeToSchoepflialarm = true)) {
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
                startListeningToUser(userId = result.data.userId)
            }
        }
    }

    fun signOut(user: FirebaseHitobitoUser) {

        stopListeningToUser()
        updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, state = ActionState.Loading(Unit)))

        viewModelScope.launch {
            when (val result = authService.signOut()) {
                is SeesturmResult.Error -> {
                    startListeningToUser(userId = user.userId)
                    updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Error(Unit, result.error.defaultMessage)))
                    viewModelScope.launch {
                        SnackbarController.sendSnackbar(
                            SeesturmSnackbar.Error(
                                message = result.error.defaultMessage,
                                onDismiss = {
                                    updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Idle))
                                },
                                location = SeesturmSnackbarLocation.Default,
                                allowManualDismiss = true
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

        stopListeningToUser()
        updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Loading(Unit)))

        viewModelScope.launch {
            when (val result = authService.deleteAccount(user)) {
                is SeesturmResult.Error -> {
                    startListeningToUser(userId = user.userId)
                    updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Error(Unit, result.error.defaultMessage)))
                    SnackbarController.sendSnackbar(
                        SeesturmSnackbar.Error(
                            message = result.error.defaultMessage,
                            onDismiss = {
                                updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Idle))
                            },
                            location = SeesturmSnackbarLocation.Default,
                            allowManualDismiss = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    updateAuthState(SeesturmAuthState.SignedOut(ActionState.Idle))
                }
            }
        }
    }

    private fun startListeningToUser(userId: String) {

        stopListeningToUser()

        userListeningJob = viewModelScope.launch {
            authService.listenToUser(userId).collect { result ->
                when (result) {
                    is SeesturmResult.Error -> {
                        updateAuthState(SeesturmAuthState.SignedOut(ActionState.Error(Unit, "Der Benutzer konnte nicht von der Datenbank gelesen werden.")))
                    }
                    is SeesturmResult.Success -> {
                        updateAuthState(SeesturmAuthState.SignedInWithHitobito(result.data, ActionState.Idle))
                    }
                }
            }
        }
    }

    private fun stopListeningToUser() {

        userListeningJob?.cancel()
        userListeningJob = null
    }

    fun resetAuthState() {
        stopListeningToUser()
        updateAuthState(SeesturmAuthState.SignedOut(state = ActionState.Idle))
    }

    private fun updateAuthState(newAuthState: SeesturmAuthState) {
        _state.update {
            newAuthState
        }
    }
}