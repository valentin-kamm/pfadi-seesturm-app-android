package ch.seesturm.pfadiseesturm.data.data_store.repository

import androidx.datastore.core.DataStore
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.domain.data_store.repository.SelectedThemeRepository
import ch.seesturm.pfadiseesturm.util.types.SeesturmAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SelectedThemeRepositoryImpl(
    private val dataStore: DataStore<SeesturmPreferencesDao>
): SelectedThemeRepository {

    override fun readTheme(): Flow<SeesturmAppTheme> =
        dataStore.data.map { it.selectedTheme }

    override suspend fun setTheme(theme: SeesturmAppTheme) {

        dataStore.updateData {
            it.copy(
                selectedTheme = theme
            )
        }
    }
}