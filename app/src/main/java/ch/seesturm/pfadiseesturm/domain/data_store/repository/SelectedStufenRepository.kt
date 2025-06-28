package ch.seesturm.pfadiseesturm.domain.data_store.repository

import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.coroutines.flow.Flow

interface SelectedStufenRepository {

    fun readStufen(): Flow<Set<SeesturmStufe>>
    suspend fun insertStufe(newStufe: SeesturmStufe)
    suspend fun deleteStufe(stufe: SeesturmStufe)
}