package ch.seesturm.pfadiseesturm.util.types

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.util.state.ActionState

sealed class SeesturmAuthState {
    data class SignedOut(val state: ActionState<Unit>) : SeesturmAuthState()
    data class SignedInWithHitobito(val user: FirebaseHitobitoUser, val state: ActionState<Unit>) :
        SeesturmAuthState()

    val signInButtonIsLoading: Boolean
        get() = when (this) {
            is SignedOut -> {
                when (this.state) {
                    is ActionState.Loading -> {
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
            else -> {
                false
            }
        }
    val deleteAccountButtonLoading: Boolean
        get() = when (this) {
            is SignedInWithHitobito -> {
                when (this.state) {
                    is ActionState.Loading -> {
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            else -> {
                false
            }
        }
}