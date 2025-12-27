package ch.seesturm.pfadiseesturm.data.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

interface StorageApi {

    suspend fun uploadData(path: String, data: ByteArray, contentType: String): Uri
    suspend fun deleteData(path: String)
}

class StorageApiImpl(
    private val storage: FirebaseStorage
): StorageApi {

    override suspend fun uploadData(path: String, data: ByteArray, contentType: String): Uri {

        val reference = getReference(path)
        val metadata = StorageMetadata
            .Builder()
            .setContentType(contentType)
            .build()

        reference.putBytes(data, metadata).await()

        return reference.downloadUrl.await()
    }

    override suspend fun deleteData(path: String) {

        val reference = getReference(path)
        reference.delete().await()
    }

    private fun getReference(path: String): StorageReference {
        return storage.getReference(path)
    }
}