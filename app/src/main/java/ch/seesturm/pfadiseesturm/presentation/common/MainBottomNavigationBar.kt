package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import ch.seesturm.pfadiseesturm.util.navigation.GetIcon

@Composable
fun MainBottomNavigationBar(
    tabNavController: NavController
) {

    NavigationBar(
        containerColor = Color.Transparent
    ) {

        // list of all tabs
        val tabs = AppDestination.MainTabView.Destinations.allInstances()

        // read current destination
        val entry by tabNavController.currentBackStackEntryAsState()
        val currentDestination = entry?.destination

        tabs.forEach { tab ->
            // check if the current tab is selected
            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(tab::class)
            } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    tabNavController.navigate(tab) {
                        popUpTo(tabNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { tab.GetIcon(isSelected) },
                label = { Text(tab.title) },
                colors = NavigationBarItemColors(
                    selectedIconColor = if (tab is AppDestination.MainTabView.Destinations.Home) {
                        Color.Unspecified
                    }
                    else {
                        Color.SEESTURM_GREEN
                    },
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIndicatorColor = Color.Unspecified,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledIconColor = Color.Unspecified,
                    disabledTextColor = Color.Unspecified
                )
            )
        }
    }
}