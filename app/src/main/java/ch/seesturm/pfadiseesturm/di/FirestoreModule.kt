package ch.seesturm.pfadiseesturm.di

import android.content.Context
import ch.seesturm.pfadiseesturm.data.firestore.FirestoreApi
import ch.seesturm.pfadiseesturm.data.firestore.FirestoreApiImpl
import ch.seesturm.pfadiseesturm.data.firestore.repository.FirestoreRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

interface FirestoreModule {
    val db: FirebaseFirestore

    val firestoreApi: FirestoreApi

    val firestoreRepository: FirestoreRepository
}

class FirestoreModuleImpl(
    private val appContext: Context
): FirestoreModule {

    override val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    override val firestoreApi: FirestoreApi by lazy {
        FirestoreApiImpl(db)
    }

    override val firestoreRepository: FirestoreRepository by lazy {
        FirestoreRepositoryImpl(firestoreApi, db)
    }
}