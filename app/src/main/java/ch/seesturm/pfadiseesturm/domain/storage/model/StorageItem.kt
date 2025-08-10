package ch.seesturm.pfadiseesturm.domain.storage.model

import android.content.Context
import android.net.Uri
import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

sealed class StorageItem {
    data class ProfilePicture(
        val user: FirebaseHitobitoUser,
        val uri: Uri
    ): StorageItem()

    fun getReference(storage: FirebaseStorage): StorageReference {
        return when (this) {
            is ProfilePicture -> {
                storage.getReference().child("profilePictures/${user.userId}.jpg")
            }
        }
    }

    suspend fun getData(context: Context): ByteArray {
        return when (this) {
            is ProfilePicture -> {
                JPGByteArray.fromUri(this.uri, context).wrappedByteArray
            }
        }
    }
}