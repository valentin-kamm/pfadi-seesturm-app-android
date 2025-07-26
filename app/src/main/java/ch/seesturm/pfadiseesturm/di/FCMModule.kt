package ch.seesturm.pfadiseesturm.di

import android.content.Context
import androidx.datastore.core.DataStore
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.data.fcm.FCMApi
import ch.seesturm.pfadiseesturm.data.fcm.FCMApiImpl
import ch.seesturm.pfadiseesturm.data.fcm.repository.FCMRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.fcm.repository.FCMRepository
import ch.seesturm.pfadiseesturm.domain.fcm.service.FCMService
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging

interface FCMModule {

    val fcmApi: FCMApi
    val fcmRepository: FCMRepository
    val fcmService: FCMService
}

class FCMModuleImpl(
    private val appContext: Context,
    private val dataStore: DataStore<SeesturmPreferencesDao>,
    private val firestoreRepository: FirestoreRepository,
    private val messaging: FirebaseMessaging = Firebase.messaging
): FCMModule {

    override val fcmApi: FCMApi by lazy {
        FCMApiImpl(messaging)
    }

    override val fcmRepository: FCMRepository by lazy {
        FCMRepositoryImpl(
            api = fcmApi,
            dataStore = dataStore,
            firestoreRepository = firestoreRepository
        )
    }
    override val fcmService: FCMService by lazy {
        FCMService(
            fcmRepository = fcmRepository,
            firestoreRepository = firestoreRepository,
            context = appContext
        )
    }
}