package ch.seesturm.pfadiseesturm.domain.storage.repository

import android.content.Context
import android.net.Uri
import ch.seesturm.pfadiseesturm.domain.storage.model.StorageItem
import com.google.firebase.storage.StorageMetadata

interface StorageRepository {

    suspend fun uploadData(item: StorageItem, context: Context, metadata: StorageMetadata? = null, onProgress: (Double) -> Unit): Uri
}