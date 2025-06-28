package ch.seesturm.pfadiseesturm.presentation.common

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.navigation.GetIcon
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN

@Composable
fun MainBottomNavigationBar(
    tabNavController: NavController,
    modifier: Modifier = Modifier
) {

    NavigationBar(
        modifier = modifier,
        containerColor = if (Build.VERSION.SDK_INT >= 30) {
            Color.Transparent
        }
        else {
            MaterialTheme.colorScheme.primaryContainer
        }
    ) {

        val tabs = AppDestination.MainTabView.Destinations.allInstances()

        // get current destination from tab nav controller in order to highlight the correct tab
        val entry by tabNavController.currentBackStackEntryAsState()
        val currentDestination = entry?.destination

        tabs.forEach { tab ->

            val isItemSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(tab::class)
            } == true

            NavigationBarItem(
                selected = isItemSelected,
                onClick = {
                    tabNavController.navigate(tab) {
                        popUpTo(tabNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    tab.GetIcon(isItemSelected)
                },
                label = {
                    Text(tab.title)
                },
                colors = NavigationBarItemColors(
                    selectedIconColor = if (tab is AppDestination.MainTabView.Destinations.Home) {
                        Color.Unspecified
                    }
                    else {
                        Color.SEESTURM_GREEN
                    },
                    selectedTextColor = Color.SEESTURM_GREEN,
                    selectedIndicatorColor = if (tab is AppDestination.MainTabView.Destinations.Home) {
                        Color.Unspecified
                    }
                    else {
                        MaterialTheme.colorScheme.secondaryContainer
                    },
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledIconColor = Color.Unspecified,
                    disabledTextColor = Color.Unspecified
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainBottomNavigationBarPreview() {
    PfadiSeesturmTheme {
        MainBottomNavigationBar(
            tabNavController = rememberNavController()
        )
    }
}