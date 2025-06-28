package ch.seesturm.pfadiseesturm.domain.data_store.repository



interface OnboardingRepository {

    suspend fun mustShowOnboardingView(): Boolean
    suspend fun setMustShowOnboardingView(mustShow: Boolean)
}