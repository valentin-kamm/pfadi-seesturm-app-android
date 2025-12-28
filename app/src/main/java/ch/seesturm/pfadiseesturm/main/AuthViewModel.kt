package ch.seesturm.pfadiseesturm.main

import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.account.service.ProfilePictureService
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.service.AuthService
import ch.seesturm.pfadiseesturm.domain.storage.model.ProfilePicture
import ch.seesturm.pfadiseesturm.presentation.account.auth.AuthIntentController
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.SeesturmAuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authService: AuthService,
    private val profilePictureService: ProfilePictureService
): ViewModel() {

    private val _state = MutableStateFlow<SeesturmAuthState>(SeesturmAuthState.SignedOut(state = ActionState.Idle))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            reauthenticateWithHitobito()
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



    private suspend fun reauthenticateWithHitobito() {

        updateAuthState(SeesturmAuthState.SignedOut(state = ActionState.Loading(Unit)))
        when (val result = authService.reauthenticateWithHitobito(resubscribeToSchoepflialarm = true)) {
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
                        SnackbarController.showSnackbar(
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

        updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Loading(Unit)))
        viewModelScope.launch {
            when (val result = authService.deleteAccount(user)) {
                is SeesturmResult.Error -> {
                    updateAuthState(SeesturmAuthState.SignedInWithHitobito(user, ActionState.Error(Unit, result.error.defaultMessage)))
                    SnackbarController.showSnackbar(
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

    fun resetAuthState() {
        updateAuthState(SeesturmAuthState.SignedOut(state = ActionState.Idle))
    }

    private fun updateAuthState(newAuthState: SeesturmAuthState) {
        _state.update {
            newAuthState
        }
    }

    suspend fun uploadProfilePicture(picture: ProfilePicture): SeesturmResult<Unit, DataError.Storage> {

        when (val localAuthState = state.value) {
            is SeesturmAuthState.SignedInWithHitobito -> {

                val user = localAuthState.user

                when (val result = profilePictureService.uploadProfilePicture(user, picture)) {
                    is SeesturmResult.Error -> {
                        return SeesturmResult.Error(result.error)
                    }
                    is SeesturmResult.Success -> {
                        updateLocalProfilePictureUrl(result.data)
                        return SeesturmResult.Success(Unit)
                    }
                }
            }
            else -> {
                return SeesturmResult.Error(DataError.Storage.UNAUTHENTICATED("Du bist nicht angemeldet und kannst somit keine Profilbild hochladen."))
            }
        }
    }

    suspend fun deleteProfilePicture(): SeesturmResult<Unit, DataError.Storage> {

        when (val localAuthState = state.value) {
            is SeesturmAuthState.SignedInWithHitobito -> {

                val user = localAuthState.user

                when (val result = profilePictureService.deleteProfilePicture(user)) {
                    is SeesturmResult.Error -> {
                        return SeesturmResult.Error(result.error)
                    }
                    is SeesturmResult.Success -> {
                        updateLocalProfilePictureUrl(null)
                        return SeesturmResult.Success(Unit)
                    }
                }
            }
            else -> {
                return SeesturmResult.Error(DataError.Storage.UNAUTHENTICATED("Du bist nicht angemeldet und kannst dein Profilbild somit nicht löschen."))
            }
        }
    }

    private fun updateLocalProfilePictureUrl(url: Uri?) {
        when (val localAuthState = state.value) {
            is SeesturmAuthState.SignedInWithHitobito -> {
                val newUser = FirebaseHitobitoUser.from(localAuthState.user, url?.toString())
                updateAuthState(SeesturmAuthState.SignedInWithHitobito(newUser, localAuthState.state))
            }
            else -> {}
        }
    }
}