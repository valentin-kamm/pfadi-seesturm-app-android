package ch.seesturm.pfadiseesturm.data.data_store.repository

import androidx.datastore.core.DataStore
import ch.seesturm.pfadiseesturm.data.data_store.dao.GespeichertePersonDao
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import ch.seesturm.pfadiseesturm.domain.data_store.repository.GespeichertePersonenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GespeichertePersonenRepositoryImpl(
    private val dataStore: DataStore<SeesturmPreferencesDao>
): GespeichertePersonenRepository {

    override fun readPersons(): Flow<List<GespeichertePersonDao>> =
        dataStore.data.map { it.savedPersons }

    override suspend fun addPerson(newPerson: GespeichertePersonDao) {
        dataStore.updateData { oldData ->
            val newPersonList = oldData.savedPersons + newPerson
            oldData.copy(
                savedPersons = newPersonList
            )
        }
    }

    override suspend fun deletePerson(id: String) {
        dataStore.updateData { oldData ->
            val newPersonList = oldData.savedPersons.filter { it.id != id }
            oldData.copy(
                savedPersons = newPersonList
            )
        }
    }
}