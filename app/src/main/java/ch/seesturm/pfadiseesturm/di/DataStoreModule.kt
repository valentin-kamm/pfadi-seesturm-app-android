package ch.seesturm.pfadiseesturm.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import ch.seesturm.pfadiseesturm.data.data_store.DataStoreSerializer
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.data.data_store.repository.GespeichertePersonenRepositoryImpl
import ch.seesturm.pfadiseesturm.data.data_store.repository.OnboardingRepositoryImpl
import ch.seesturm.pfadiseesturm.data.data_store.repository.SelectedStufenRepositoryImpl
import ch.seesturm.pfadiseesturm.data.data_store.repository.SelectedThemeRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.data_store.repository.GespeichertePersonenRepository
import ch.seesturm.pfadiseesturm.domain.data_store.repository.OnboardingRepository
import ch.seesturm.pfadiseesturm.domain.data_store.repository.SelectedStufenRepository
import ch.seesturm.pfadiseesturm.domain.data_store.repository.SelectedThemeRepository
import ch.seesturm.pfadiseesturm.domain.data_store.service.GespeichertePersonenService
import ch.seesturm.pfadiseesturm.domain.data_store.service.OnboardingService
import ch.seesturm.pfadiseesturm.domain.data_store.service.SelectedThemeService
import ch.seesturm.pfadiseesturm.util.Constants

interface DataStoreModule {

    val dataStore: DataStore<SeesturmPreferencesDao>

    val gespeichertePersonenRepository: GespeichertePersonenRepository
    val gespeichertePersonenService: GespeichertePersonenService

    val selectedStufenRepository: SelectedStufenRepository

    val selectedThemeRepository: SelectedThemeRepository
    val selectedThemeService: SelectedThemeService

    val onboardingRepository: OnboardingRepository
    val onboardingService: OnboardingService
}

class DataStoreModuleImpl(
    private val appContext: Context
): DataStoreModule {

    override val dataStore: DataStore<SeesturmPreferencesDao> by lazy {
        DataStoreFactory.create(
            serializer = DataStoreSerializer,
            produceFile = { appContext.dataStoreFile(Constants.DATA_STORE_FILE_NAME) }
        )
    }

    override val gespeichertePersonenRepository: GespeichertePersonenRepository by lazy {
        GespeichertePersonenRepositoryImpl(dataStore)
    }
    override val gespeichertePersonenService: GespeichertePersonenService by lazy {
        GespeichertePersonenService(gespeichertePersonenRepository)
    }

    override val selectedStufenRepository: SelectedStufenRepository by lazy {
        SelectedStufenRepositoryImpl(dataStore)
    }

    override val selectedThemeRepository: SelectedThemeRepository by lazy {
        SelectedThemeRepositoryImpl(dataStore)
    }
    override val selectedThemeService: SelectedThemeService by lazy {
        SelectedThemeService(selectedThemeRepository)
    }

    override val onboardingRepository: OnboardingRepository by lazy {
        OnboardingRepositoryImpl(dataStore)
    }
    override val onboardingService: OnboardingService by lazy {
        OnboardingService(onboardingRepository)
    }
}