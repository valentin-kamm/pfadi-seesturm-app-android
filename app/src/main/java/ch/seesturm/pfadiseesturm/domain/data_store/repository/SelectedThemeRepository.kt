package ch.seesturm.pfadiseesturm.domain.data_store.repository

import ch.seesturm.pfadiseesturm.util.types.SeesturmAppTheme
import kotlinx.coroutines.flow.Flow

interface SelectedThemeRepository {

    fun readTheme(): Flow<SeesturmAppTheme>
    suspend fun setTheme(theme: SeesturmAppTheme)
}