package ch.seesturm.pfadiseesturm.domain.storage.model

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference

sealed class UploadStorageItem {
    data class ProfilePicture(
        val data: JPGData,
        val userId: String
    ): UploadStorageItem()

    fun getReference(storage: FirebaseStorage): StorageReference {
        return when (this) {
            is ProfilePicture -> {
                storage.getReference().child("profilePictures/${userId}.jpg")
            }
        }
    }

    val uploadData: ByteArray
        get() = when (this) {
            is ProfilePicture -> data.compressedByteArray
        }

    val metadata: StorageMetadata
        get() {
            return when (this) {
                is ProfilePicture -> {
                    StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build()
                }
            }
        }
}