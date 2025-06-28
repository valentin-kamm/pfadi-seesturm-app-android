package ch.seesturm.pfadiseesturm.data.data_store.repository

import androidx.datastore.core.DataStore
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.domain.data_store.repository.OnboardingRepository
import kotlinx.coroutines.flow.first

class OnboardingRepositoryImpl(
    private val dataStore: DataStore<SeesturmPreferencesDao>
): OnboardingRepository {

    override suspend fun mustShowOnboardingView(): Boolean =
        dataStore.data.first().showOnboardingView2

    override suspend fun setMustShowOnboardingView(mustShow: Boolean) {
        dataStore.updateData {
            it.copy(
                showOnboardingView2 = mustShow
            )
        }
    }
}