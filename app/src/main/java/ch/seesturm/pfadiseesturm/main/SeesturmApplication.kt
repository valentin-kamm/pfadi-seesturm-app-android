package ch.seesturm.pfadiseesturm.main

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.content.getSystemService
import ch.seesturm.pfadiseesturm.BuildConfig.DEBUG
import ch.seesturm.pfadiseesturm.di.AccountModule
import ch.seesturm.pfadiseesturm.di.AccountModuleImpl
import ch.seesturm.pfadiseesturm.di.AuthModule
import ch.seesturm.pfadiseesturm.di.AuthModuleImpl
import ch.seesturm.pfadiseesturm.di.DataStoreModule
import ch.seesturm.pfadiseesturm.di.DataStoreModuleImpl
import ch.seesturm.pfadiseesturm.di.FCFModule
import ch.seesturm.pfadiseesturm.di.FCFModuleImpl
import ch.seesturm.pfadiseesturm.di.FCMModule
import ch.seesturm.pfadiseesturm.di.FCMModuleImpl
import ch.seesturm.pfadiseesturm.di.FirestoreModule
import ch.seesturm.pfadiseesturm.di.FirestoreModuleImpl
import ch.seesturm.pfadiseesturm.di.StorageModule
import ch.seesturm.pfadiseesturm.di.StorageModuleImpl
import ch.seesturm.pfadiseesturm.di.WordpressModule
import ch.seesturm.pfadiseesturm.di.WordpressModuleImpl
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class SeesturmApplication: Application() {

    companion object {
        lateinit var fcfModule: FCFModule
        lateinit var firestoreModule: FirestoreModule
        lateinit var wordpressModule: WordpressModule
        lateinit var dataStoreModule: DataStoreModule
        lateinit var fcmModule: FCMModule
        lateinit var storageModule: StorageModule
        lateinit var authModule: AuthModule
        lateinit var accountModule: AccountModule
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        if (DEBUG) {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        }
        else {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }

        createNotificationChannel()

        fcfModule = FCFModuleImpl()
        dataStoreModule = DataStoreModuleImpl(
            appContext = this
        )
        firestoreModule = FirestoreModuleImpl()
        wordpressModule = WordpressModuleImpl(
            firestoreRepository = firestoreModule.firestoreRepository,
            selectedStufenRepository = dataStoreModule.selectedStufenRepository
        )
        fcmModule = FCMModuleImpl(
            appContext = this,
            dataStore = dataStoreModule.dataStore,
            firestoreRepository = firestoreModule.firestoreRepository
        )
        storageModule = StorageModuleImpl()
        authModule = AuthModuleImpl(
            appContext = this,
            cloudFunctionsRepository = fcfModule.fcfRepository,
            firestoreRepository = firestoreModule.firestoreRepository,
            fcmRepository = fcmModule.fcmRepository
        )
        accountModule = AccountModuleImpl(
            appContext = this,
            anlaesseRepository = wordpressModule.anlaesseRepository,
            firestoreRepository = firestoreModule.firestoreRepository,
            selectedStufenRepository = dataStoreModule.selectedStufenRepository,
            cloudFunctionsRepository = fcfModule.fcfRepository,
            fcmService = fcmModule.fcmService,
            storageRepository = storageModule.storageRepository
        )
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "seesturm_notification_channel",
            "Pfadi Seesturm App Notification Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Push-Nachrichten der Pfadi Seesturm App"
        }
        getSystemService<NotificationManager>()?.createNotificationChannel(channel)
    }
}