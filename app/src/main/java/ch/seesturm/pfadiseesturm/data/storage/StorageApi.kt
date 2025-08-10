package ch.seesturm.pfadiseesturm.data.storage

import android.net.Uri
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

interface StorageApi {

    suspend fun uploadData(reference: StorageReference, data: ByteArray, metadata: StorageMetadata? = null, onProgress: (Double) -> Unit): Uri
}

class StorageApiImpl: StorageApi {

    override suspend fun uploadData(reference: StorageReference, data: ByteArray, metadata: StorageMetadata?, onProgress: (Double) -> Unit): Uri {

        if (metadata != null) {
            reference.putBytes(data, metadata).addOnProgressListener {
                onProgress(it.bytesTransferred.toDouble() / it.totalByteCount.toDouble())
            }.await()
        }
        else {
            reference.putBytes(data).addOnProgressListener {
                onProgress(it.bytesTransferred.toDouble() / it.totalByteCount.toDouble())
            }.await()
        }

        return reference.downloadUrl.await()
    }
}