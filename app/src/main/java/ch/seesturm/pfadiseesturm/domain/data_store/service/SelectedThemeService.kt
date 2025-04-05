package ch.seesturm.pfadiseesturm.domain.data_store.service

import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.domain.data_store.repository.SelectedThemeRepository
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmAppTheme
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SelectedThemeService(
    private val repository: SelectedThemeRepository
) {

    fun readTheme(): Flow<SeesturmResult<SeesturmAppTheme, DataError.Local>> {
        return repository.readTheme()
            .map<_, SeesturmResult<SeesturmAppTheme, DataError.Local>> {
                SeesturmResult.Success(it)
            }
            .catch { _ ->
                emit(SeesturmResult.Error(DataError.Local.READING_ERROR))
            }
    }
    suspend fun updateTheme(theme: SeesturmAppTheme): SeesturmResult<Unit, DataError.Local> {
        return try {
            repository.updateTheme(theme)
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.Local.SAVING_ERROR)
        }
    }
}