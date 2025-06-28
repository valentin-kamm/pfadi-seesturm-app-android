package ch.seesturm.pfadiseesturm.di

import android.content.Context
import ch.seesturm.pfadiseesturm.domain.account.service.LeiterbereichService
import ch.seesturm.pfadiseesturm.domain.account.service.SchoepflialarmService
import ch.seesturm.pfadiseesturm.domain.account.service.StufenbereichService
import ch.seesturm.pfadiseesturm.domain.data_store.repository.SelectedStufenRepository
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import ch.seesturm.pfadiseesturm.domain.fcm.service.FCMService
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AnlaesseRepository

interface AccountModule {

    val leiterbereichService: LeiterbereichService
    val stufenbereichService: StufenbereichService
    val schoepflialarmService: SchoepflialarmService
}

class AccountModuleImpl(
    private val appContext: Context,
    private val anlaesseRepository: AnlaesseRepository,
    private val firestoreRepository: FirestoreRepository,
    private val selectedStufenRepository: SelectedStufenRepository,
    private val cloudFunctionsRepository: CloudFunctionsRepository,
    private val fcmService: FCMService
) : AccountModule {

    override val leiterbereichService: LeiterbereichService by lazy {
        LeiterbereichService(
            termineRepository = anlaesseRepository,
            firestoreRepository = firestoreRepository,
            selectedStufenRepository = selectedStufenRepository
        )
    }
    override val stufenbereichService: StufenbereichService by lazy {
        StufenbereichService(
            anlaesseRepository = anlaesseRepository,
            firestoreRepository = firestoreRepository,
            cloudFunctionsRepository = cloudFunctionsRepository
        )
    }
    override val schoepflialarmService: SchoepflialarmService by lazy {
        SchoepflialarmService(
            firestoreRepository = firestoreRepository,
            fcmService = fcmService,
            fcfRepository = cloudFunctionsRepository,
            context = appContext
        )
    }
}