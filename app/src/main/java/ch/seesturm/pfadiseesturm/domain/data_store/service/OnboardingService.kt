package ch.seesturm.pfadiseesturm.domain.data_store.service

import ch.seesturm.pfadiseesturm.domain.data_store.repository.OnboardingRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

class OnboardingService(
    private val repository: OnboardingRepository
) {

    suspend fun mustShowOnboardingView(): SeesturmResult<Boolean, DataError.Local> {

        return try {
            val result = repository.mustShowOnboardingView()
            SeesturmResult.Success(result)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.Local.READING_ERROR)
        }
    }

    suspend fun setMustShowOnboardingView(mustShow: Boolean): SeesturmResult<Unit, DataError.Local> {
        return try {
            repository.setMustShowOnboardingView(mustShow)
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(DataError.Local.SAVING_ERROR)
        }
    }
}