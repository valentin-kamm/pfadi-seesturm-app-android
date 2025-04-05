package ch.seesturm.pfadiseesturm.domain.data_store.repository

import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import kotlinx.coroutines.flow.Flow

interface SelectedStufenRepository {

    fun readSelectedStufen(): Flow<Set<SeesturmStufe>>
    suspend fun addStufe(newStufe: SeesturmStufe)
    suspend fun deleteStufe(stufe: SeesturmStufe)
}