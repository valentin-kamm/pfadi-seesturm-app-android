package ch.seesturm.pfadiseesturm.di

import ch.seesturm.pfadiseesturm.data.firestore.FirestoreApi
import ch.seesturm.pfadiseesturm.data.firestore.FirestoreApiImpl
import ch.seesturm.pfadiseesturm.data.firestore.repository.FirestoreRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

interface FirestoreModule {

    val firestoreApi: FirestoreApi
    val firestoreRepository: FirestoreRepository
}

class FirestoreModuleImpl(
    private val db: FirebaseFirestore = Firebase.firestore
): FirestoreModule {

    override val firestoreApi: FirestoreApi by lazy {
        FirestoreApiImpl(db)
    }

    override val firestoreRepository: FirestoreRepository by lazy {
        FirestoreRepositoryImpl(firestoreApi, db)
    }
}