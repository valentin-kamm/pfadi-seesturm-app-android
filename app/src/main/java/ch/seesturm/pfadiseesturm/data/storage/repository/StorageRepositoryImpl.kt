package ch.seesturm.pfadiseesturm.data.storage.repository

import android.net.Uri
import ch.seesturm.pfadiseesturm.data.storage.StorageApi
import ch.seesturm.pfadiseesturm.domain.storage.model.DeleteStorageItem
import ch.seesturm.pfadiseesturm.domain.storage.model.UploadStorageItem
import ch.seesturm.pfadiseesturm.domain.storage.repository.StorageRepository
import com.google.firebase.storage.FirebaseStorage

class StorageRepositoryImpl(
    private val api: StorageApi,
    private val storage: FirebaseStorage
): StorageRepository {

    override suspend fun uploadData(
        item: UploadStorageItem
    ): Uri =
        api.uploadData(
            reference = item.getReference(storage),
            data = item.uploadData,
            metadata = item.metadata
        )

    override suspend fun deleteData(item: DeleteStorageItem) =
        api.deleteData(item.getReference(storage))
}