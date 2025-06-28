package ch.seesturm.pfadiseesturm.data.data_store.repository

import androidx.datastore.core.DataStore
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.domain.data_store.repository.SelectedStufenRepository
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SelectedStufenRepositoryImpl(
    private val dataStore: DataStore<SeesturmPreferencesDao>
): SelectedStufenRepository {

    override fun readStufen(): Flow<Set<SeesturmStufe>> =
        dataStore.data.map { it.selectedStufen }

    override suspend fun insertStufe(newStufe: SeesturmStufe) {

        dataStore.updateData { oldData ->
            val newStufenList = oldData.selectedStufen + newStufe
            oldData.copy(
                selectedStufen = newStufenList
            )
        }
    }

    override suspend fun deleteStufe(stufe: SeesturmStufe) {

        dataStore.updateData { oldData ->
            val newStufenList = oldData.selectedStufen.toMutableSet()
            newStufenList.remove(stufe)
            oldData.copy(
                selectedStufen = newStufenList
            )
        }
    }
}