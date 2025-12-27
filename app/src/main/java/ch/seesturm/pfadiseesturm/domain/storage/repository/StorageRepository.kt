package ch.seesturm.pfadiseesturm.domain.storage.repository

import android.net.Uri
import ch.seesturm.pfadiseesturm.domain.storage.model.DeleteStorageItem
import ch.seesturm.pfadiseesturm.domain.storage.model.UploadStorageItem

interface StorageRepository {

    suspend fun uploadData(item: UploadStorageItem): Uri
    suspend fun deleteData(item: DeleteStorageItem)
}