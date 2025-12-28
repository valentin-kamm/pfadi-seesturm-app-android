package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import android.net.Uri
import ch.seesturm.pfadiseesturm.domain.storage.model.PickedGalleryItem
import ch.seesturm.pfadiseesturm.util.state.AnonymousActionState

data class EditProfileState(
    val selectedImageUri: Uri? = null,
    val imageSelectionState: AnonymousActionState<PickedGalleryItem> = AnonymousActionState.Idle,
    val showDeleteImageAlert: Boolean = false
)