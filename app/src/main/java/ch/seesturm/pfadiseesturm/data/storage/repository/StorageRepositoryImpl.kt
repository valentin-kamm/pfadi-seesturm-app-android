package ch.seesturm.pfadiseesturm.data.storage.repository

import android.content.Context
import android.net.Uri
import ch.seesturm.pfadiseesturm.data.storage.StorageApi
import ch.seesturm.pfadiseesturm.domain.storage.model.StorageItem
import ch.seesturm.pfadiseesturm.domain.storage.repository.StorageRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata

class StorageRepositoryImpl(
    private val api: StorageApi,
    private val storage: FirebaseStorage
): StorageRepository {

    override suspend fun uploadData(item: StorageItem, context: Context, metadata: StorageMetadata?, onProgress: (Double) -> Unit): Uri =
        api.uploadData(
            reference = item.getReference(storage),
            data = item.getData(context),
            metadata = metadata,
            onProgress = onProgress
        )
}