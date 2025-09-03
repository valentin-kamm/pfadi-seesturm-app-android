package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import android.net.Uri
import ch.seesturm.pfadiseesturm.domain.storage.model.JPGData
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.AnonymousActionState

data class EditProfileState(
    val selectedImageUri: Uri? = null,
    val imageSelectionState: AnonymousActionState<JPGData> = AnonymousActionState.Idle,
    val imageUploadState: ActionState<Unit> = ActionState.Idle,
    val imageDeleteState: ActionState<Unit> = ActionState.Idle,
    val showDeleteImageAlert: Boolean = false
)