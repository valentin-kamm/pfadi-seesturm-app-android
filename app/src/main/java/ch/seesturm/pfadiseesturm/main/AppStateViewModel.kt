package ch.seesturm.pfadiseesturm.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.domain.data_store.service.OnboardingService
import ch.seesturm.pfadiseesturm.domain.data_store.service.SelectedThemeService
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.types.SeesturmAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppStateViewModel(

    private val themeService: SelectedThemeService,
    private val onboardingService: OnboardingService,
    private val wordpressApi: WordpressApi,
    private val currentAppBuild: Int?
): ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    init {
        startListeningToSelectedTheme()
        navigateToOnboardingViewIfNecessary()
        viewModelScope.launch {
            checkMinimumRequiredAppBuild()
        }
    }

    private fun navigateToOnboardingViewIfNecessary() {
        viewModelScope.launch {
            val result = onboardingService.mustShowOnboardingView()
            if (result is SeesturmResult.Success && result.data) {
                OnboardingController.showOnboardingView()
            }
        }
    }
    fun setHasSeenOnboarding() {
        viewModelScope.launch {
            onboardingService.setMustShowOnboardingView(mustShow = false)
        }
    }

    private fun startListeningToSelectedTheme() {
        themeService.readTheme().onEach { result ->
            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            theme = SeesturmAppTheme.System
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            theme = result.data
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateTheme(theme: SeesturmAppTheme) {
        viewModelScope.launch {
            when (themeService.updateTheme(theme)) {
                is SeesturmResult.Error -> {
                    SnackbarController.showSnackbar(
                        SeesturmSnackbar.Error(
                            message = "Das Erscheinungsbild konnte nicht geändert werden. Versuche es später erneut.",
                            onDismiss = {},
                            location = SeesturmSnackbarLocation.Default,
                            allowManualDismiss = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    // do nothing
                }
            }
        }
    }

    private suspend fun checkMinimumRequiredAppBuild() {

        try {
            val minimimRequiredBuild = wordpressApi.getMinimumRequiredAppBuild()

            if (currentAppBuild != null && currentAppBuild < minimimRequiredBuild.android) {
                _state.update {
                    it.copy(
                        showAppVersionCheckOverlay = true
                    )
                }
            }
        }
        catch (e: Exception) {
            println("Minimum required app build could not be checked.")
        }
    }

    fun updateAllowedOrientation(orientation: AllowedOrientation) {
        _state.update {
            it.copy(
                allowedOrientation = orientation
            )
        }
    }
}