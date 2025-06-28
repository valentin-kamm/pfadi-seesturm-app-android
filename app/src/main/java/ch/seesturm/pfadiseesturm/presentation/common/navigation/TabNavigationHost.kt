package ch.seesturm.pfadiseesturm.presentation.common.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.presentation.common.BottomNavigationScaffold
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.main.AppStateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabNavigationHost(
    appStateViewModel: AppStateViewModel,
    tabNavController: NavHostController = rememberNavController()
) {

    // react to change tab events
    ObserveAsEvents(
        flow = SeesturmNavigationController.tabEvents
    ) { tabChangeEvent ->
        tabNavController.navigate(tabChangeEvent) {
            popUpTo(tabNavController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

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
                        appStateViewModel = appStateViewModel
                    )
                }
                composable<AppDestination.MainTabView.Destinations.Aktuell>{
                    AktuellNavHost(
                        bottomNavigationInnerPadding = innerPadding
                    )
                }
                composable<AppDestination.MainTabView.Destinations.Anlaesse> {
                    AnlaesseNavHost(
                        bottomNavigationInnerPadding = innerPadding
                    )
                }
                composable<AppDestination.MainTabView.Destinations.Mehr> {
                    MehrNavHost(
                        bottomNavigationInnerPadding = innerPadding,
                        tabNavController = tabNavController,
                        appStateViewModel = appStateViewModel
                    )
                }
                composable<AppDestination.MainTabView.Destinations.Account> {
                    AccountNavHost(
                        bottomNavigationInnerPadding = innerPadding,
                        tabNavController = tabNavController,
                        appStateViewModel = appStateViewModel
                    )
                }
            }
        }
    )
}