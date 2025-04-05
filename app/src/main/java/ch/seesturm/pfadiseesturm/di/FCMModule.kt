package ch.seesturm.pfadiseesturm.di

import android.content.Context
import androidx.activity.ComponentActivity
import ch.seesturm.pfadiseesturm.data.fcm.FCMApi
import ch.seesturm.pfadiseesturm.data.fcm.FCMApiImpl
import ch.seesturm.pfadiseesturm.data.fcm.repository.FCMRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.fcm.repository.FCMRepository
import ch.seesturm.pfadiseesturm.domain.fcm.service.FCMSubscriptionService
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging

interface FCMModule {

    val messaging: FirebaseMessaging

    val fcmApi: FCMApi

    val fcmRepository: FCMRepository
    val fcmSubscriptionService: FCMSubscriptionService
}

class FCMModuleImpl(
    private val appContext: Context,
    private val dataStoreModule: DataStoreModule
): FCMModule {

    override val messaging: FirebaseMessaging by lazy {
        Firebase.messaging
    }

    override val fcmApi: FCMApi by lazy {
        FCMApiImpl(messaging)
    }

    override val fcmRepository: FCMRepository by lazy {
        FCMRepositoryImpl(fcmApi, dataStoreModule.dataStore)
    }
    override val fcmSubscriptionService: FCMSubscriptionService by lazy {
        FCMSubscriptionService(fcmRepository, appContext)
    }
}