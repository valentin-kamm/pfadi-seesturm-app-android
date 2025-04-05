package ch.seesturm.pfadiseesturm.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.authModule
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.dataStoreModule
import ch.seesturm.pfadiseesturm.presentation.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import ch.seesturm.pfadiseesturm.util.navigation.TabNavigationHost


// entry point for my app
class MainActivity : ComponentActivity() {

    private val appStateViewModel: AppStateViewModel by viewModels {
        viewModelFactoryHelper {
            AppStateViewModel(
                authService = authModule.authService,
                themeService = dataStoreModule.selectedThemeService
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val overallNavController = rememberNavController()
            val appState by appStateViewModel.state.collectAsStateWithLifecycle()
            PfadiSeesturmTheme(
                darkTheme = when (appState.theme) {
                    SeesturmAppTheme.Dark -> true
                    SeesturmAppTheme.Light -> false
                    SeesturmAppTheme.System -> isSystemInDarkTheme()
                }
            ) {
                SeesturmAppMain(
                    overallNavController = overallNavController,
                    appStateViewModel = appStateViewModel
                )
            }
        }
    }
}

@Composable
fun SeesturmAppMain(
    overallNavController: NavHostController,
    appStateViewModel: AppStateViewModel
) {
    NavHost(
        navController = overallNavController,
        startDestination = AppDestination.MainTabView
    ) {
        composable<AppDestination.MainTabView>{
            TabNavigationHost(
                mainNavController = overallNavController,
                appStateViewModel = appStateViewModel
            )
        }
        composable<AppDestination.WelcomeScreen> {
            Text("Not yet implemented (Welcome)")
        }
        composable<AppDestination.VersionCheckScreen> {
            Text("Not yet implemented (Version)")
        }
    }
}
