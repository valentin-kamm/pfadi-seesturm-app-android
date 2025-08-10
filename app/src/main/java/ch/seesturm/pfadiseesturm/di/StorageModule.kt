package ch.seesturm.pfadiseesturm.di

import ch.seesturm.pfadiseesturm.data.storage.StorageApi
import ch.seesturm.pfadiseesturm.data.storage.StorageApiImpl
import ch.seesturm.pfadiseesturm.data.storage.repository.StorageRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.storage.repository.StorageRepository
import com.google.firebase.storage.FirebaseStorage

interface StorageModule {

    val storageApi: StorageApi
    val storageRepository: StorageRepository
}

class StorageModuleImpl(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
): StorageModule {

    override val storageApi: StorageApi by lazy {
        StorageApiImpl()
    }
    override val storageRepository: StorageRepository by lazy {
        StorageRepositoryImpl(storageApi, storage)
    }
}