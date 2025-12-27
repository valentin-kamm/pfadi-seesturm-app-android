package ch.seesturm.pfadiseesturm.domain.storage.model

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser

sealed interface DeleteStorageItem {

    data class ProfilePicture(
        val user: FirebaseHitobitoUser
    ): DeleteStorageItem

    val path: String
        get() = when (this) {
            is ProfilePicture -> {
                user.profilePictureStoragePath
            }
        }
}