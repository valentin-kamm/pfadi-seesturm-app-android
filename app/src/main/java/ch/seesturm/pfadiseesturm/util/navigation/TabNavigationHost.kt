package ch.seesturm.pfadiseesturm.util.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.presentation.common.BottomNavigationScaffold
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabNavigationHost(
    mainNavController: NavHostController,
    appStateViewModel: AppStateViewModel,
    tabNavController: NavHostController = rememberNavController()
) {

    BottomNavigationScaffold(
        tabNavController = tabNavController,
        appStateViewModel = appStateViewModel,
        content = { innerPadding ->
            NavHost(
                navController = tabNavController,
                startDestination = AppDestination.MainTabView.Destinations.Home
            ) {
                composable<AppDestination.MainTabView.Destinations.Home> {
                    HomeNavHost(
                        bottomNavigationInnerPadding = innerPadding,
                        tabNavController = tabNavController,
                        mainNavController = mainNavController,
                        appStateViewModel = appStateViewModel
                    )
                }
                composable<AppDestination.MainTabView.Destinations.Aktuell>{
                    AktuellNavHost(innerPadding)
                }
                composable<AppDestination.MainTabView.Destinations.Anlaesse> {
                    AnlaesseNavHost(innerPadding)
                }
                composable<AppDestination.MainTabView.Destinations.Mehr> {
                    MehrNavHost(
                        bottomNavigationInnerPadding = innerPadding,
                        tabNavController = tabNavController,
                        mainNavController = mainNavController,
                        appStateViewModel = appStateViewModel
                    )
                }
                composable<AppDestination.MainTabView.Destinations.Account> {
                    AccountNavHost(
                        bottomNavigationInnerPadding = innerPadding,
                        mainNavController = mainNavController,
                        tabNavController = tabNavController,
                        appStateViewModel = appStateViewModel
                    )
                }
            }
        }
    )
}