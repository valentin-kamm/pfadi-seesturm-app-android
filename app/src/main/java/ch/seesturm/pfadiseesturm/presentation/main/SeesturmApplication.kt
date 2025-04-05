package ch.seesturm.pfadiseesturm.presentation.main

import android.app.Application
import ch.seesturm.pfadiseesturm.di.AuthModule
import ch.seesturm.pfadiseesturm.di.AuthModuleImpl
import ch.seesturm.pfadiseesturm.di.WordpressModule
import ch.seesturm.pfadiseesturm.di.WordpressModuleImpl
import ch.seesturm.pfadiseesturm.di.DataStoreModule
import ch.seesturm.pfadiseesturm.di.DataStoreModuleImpl
import ch.seesturm.pfadiseesturm.di.FCFModule
import ch.seesturm.pfadiseesturm.di.FCFModuleImpl
import ch.seesturm.pfadiseesturm.di.FCMModule
import ch.seesturm.pfadiseesturm.di.FCMModuleImpl
import ch.seesturm.pfadiseesturm.di.FirestoreModule
import ch.seesturm.pfadiseesturm.di.FirestoreModuleImpl
import ch.seesturm.pfadiseesturm.di.AccountModule
import ch.seesturm.pfadiseesturm.di.AccountModuleImpl

class SeesturmApplication: Application() {

    companion object {
        lateinit var fcfModule: FCFModule
        lateinit var firestoreModule: FirestoreModule
        lateinit var wordpressModule: WordpressModule
        lateinit var dataStoreModule: DataStoreModule
        lateinit var fcmModule: FCMModule
        lateinit var authModule: AuthModule
        lateinit var accountModule: AccountModule
    }

    override fun onCreate() {
        super.onCreate()
        fcfModule = FCFModuleImpl(
            appContext = this
        )
        dataStoreModule = DataStoreModuleImpl(
            appContext = this
        )
        firestoreModule = FirestoreModuleImpl(
            appContext = this
        )
        wordpressModule = WordpressModuleImpl(
            appContext = this,
            firestoreRepository = firestoreModule.firestoreRepository,
            selectedStufenRepository = dataStoreModule.selectedStufenRepository
        )
        fcmModule = FCMModuleImpl(
            appContext = this,
            dataStoreModule = dataStoreModule
        )
        authModule = AuthModuleImpl(
            appContext = this,
            cloudFunctionsRepository = fcfModule.fcfRepository,
            firestoreRepository = firestoreModule.firestoreRepository
        )
        accountModule = AccountModuleImpl(
            appContext = this,
            anlaesseRepository = wordpressModule.anlaesseRepository,
            firestoreRepository = firestoreModule.firestoreRepository,
            selectedStufenRepository = dataStoreModule.selectedStufenRepository,
            cloudFunctionsRepository = fcfModule.fcfRepository
        )
    }
}