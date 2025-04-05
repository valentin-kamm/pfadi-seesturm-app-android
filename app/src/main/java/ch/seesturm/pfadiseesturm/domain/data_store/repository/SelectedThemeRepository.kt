package ch.seesturm.pfadiseesturm.domain.data_store.repository

import ch.seesturm.pfadiseesturm.data.data_store.dao.GespeichertePersonDao
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmAppTheme
import kotlinx.coroutines.flow.Flow

interface SelectedThemeRepository {
    fun readTheme(): Flow<SeesturmAppTheme>
    suspend fun updateTheme(theme: SeesturmAppTheme)
}