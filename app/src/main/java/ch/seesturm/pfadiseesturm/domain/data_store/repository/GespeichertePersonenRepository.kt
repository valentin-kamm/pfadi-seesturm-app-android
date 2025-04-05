package ch.seesturm.pfadiseesturm.domain.data_store.repository

import ch.seesturm.pfadiseesturm.data.data_store.dao.GespeichertePersonDao
import kotlinx.coroutines.flow.Flow

interface GespeichertePersonenRepository {
    fun readPersons(): Flow<List<GespeichertePersonDao>>
    suspend fun addPerson(newPerson: GespeichertePersonDao)
    suspend fun deletePerson(id: String)
}