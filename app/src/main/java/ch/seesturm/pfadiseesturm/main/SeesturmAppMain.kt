package ch.seesturm.pfadiseesturm.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.fcmModule
import ch.seesturm.pfadiseesturm.presentation.common.OnboardingView
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.navigation.TabNavigationHost
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenViewModel

@Composable
fun SeesturmAppMain(
    appStateViewModel: AppStateViewModel,
    overallNavController: NavHostController
) {
    NavHost(
        navController = overallNavController,
        startDestination = AppDestination.MainTabView
    ) {
        composable<AppDestination.MainTabView>{
            TabNavigationHost(
                appStateViewModel = appStateViewModel
            )
        }
        composable<AppDestination.Onboarding> {
            OnboardingView(
                viewModel = PushNachrichtenVerwaltenViewModel(
                    service = fcmModule.fcmService
                ),
                navController = overallNavController,
                onSetHasSeenOnboardingView = {
                    appStateViewModel.setHasSeenOnboarding()
                }
            )
        }
    }
}
