package ch.seesturm.pfadiseesturm.domain.storage.model

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser

sealed interface UploadStorageItem {

    data class ProfilePicture(
        val user: FirebaseHitobitoUser,
        val data: ch.seesturm.pfadiseesturm.domain.storage.model.ProfilePicture
    ): UploadStorageItem

    val path: String
        get() = when (this) {
            is ProfilePicture -> {
                user.profilePictureStoragePath
            }
        }

    val byteArray: ByteArray
        get() = when (this) {
            is ProfilePicture -> {
                data.compressedByteArray
            }
        }

    val contentType: String
        get() = when (this) {
            is ProfilePicture -> {
                "image/jpeg"
            }
        }
}