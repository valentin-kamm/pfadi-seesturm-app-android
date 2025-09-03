package ch.seesturm.pfadiseesturm.domain.storage.model

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

sealed class DeleteStorageItem {
    data class ProfilePicture(
        val userId: String
    ): DeleteStorageItem()

    fun getReference(storage: FirebaseStorage): StorageReference {
        return when (this) {
            is ProfilePicture -> {
                storage.getReference().child("profilePictures/${userId}.jpg")
            }
        }
    }
}