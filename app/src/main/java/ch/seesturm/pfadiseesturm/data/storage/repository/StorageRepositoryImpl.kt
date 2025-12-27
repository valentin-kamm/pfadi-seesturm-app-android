package ch.seesturm.pfadiseesturm.data.storage.repository

import android.net.Uri
import ch.seesturm.pfadiseesturm.data.storage.StorageApi
import ch.seesturm.pfadiseesturm.domain.storage.model.DeleteStorageItem
import ch.seesturm.pfadiseesturm.domain.storage.model.UploadStorageItem
import ch.seesturm.pfadiseesturm.domain.storage.repository.StorageRepository

class StorageRepositoryImpl(
    private val api: StorageApi
): StorageRepository {

    override suspend fun uploadData(item: UploadStorageItem): Uri =
        api.uploadData(
            path = item.path,
            data = item.byteArray,
            contentType = item.contentType
        )

    override suspend fun deleteData(item: DeleteStorageItem) =
        api.deleteData(
            path = item.path
        )
}