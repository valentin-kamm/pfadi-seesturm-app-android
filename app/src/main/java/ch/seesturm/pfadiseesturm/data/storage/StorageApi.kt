package ch.seesturm.pfadiseesturm.data.storage

import android.net.Uri
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

interface StorageApi {

    suspend fun uploadData(reference: StorageReference, data: ByteArray, metadata: StorageMetadata?): Uri
    suspend fun deleteData(reference: StorageReference)
}

class StorageApiImpl: StorageApi {

    override suspend fun uploadData(reference: StorageReference, data: ByteArray, metadata: StorageMetadata?): Uri {

        if (metadata != null) {
            reference.putBytes(data, metadata).await()
        }
        else {
            reference.putBytes(data).await()
        }

        return reference.downloadUrl.await()
    }

    override suspend fun deleteData(reference: StorageReference) {
        reference.delete().await()
    }
}