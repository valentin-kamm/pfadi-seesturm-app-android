package ch.seesturm.pfadiseesturm.di

import ch.seesturm.pfadiseesturm.data.storage.StorageApi
import ch.seesturm.pfadiseesturm.data.storage.StorageApiImpl
import ch.seesturm.pfadiseesturm.data.storage.repository.StorageRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.account.service.ProfilePictureService
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.domain.storage.repository.StorageRepository
import com.google.firebase.storage.FirebaseStorage

interface StorageModule {

    val storageApi: StorageApi
    val storageRepository: StorageRepository

    val profilePictureService: ProfilePictureService
}

class StorageModuleImpl(
    private val firestoreRepository: FirestoreRepository
): StorageModule {

    override val storageApi by lazy {
        StorageApiImpl(FirebaseStorage.getInstance())
    }

    override val storageRepository: StorageRepository by lazy {
        StorageRepositoryImpl(storageApi)
    }

    override val profilePictureService: ProfilePictureService by lazy {
        ProfilePictureService(storageRepository, firestoreRepository)
    }
}