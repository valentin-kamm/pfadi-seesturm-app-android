package ch.seesturm.pfadiseesturm.util.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ch.seesturm.pfadiseesturm.presentation.aktuell.detail.AktuellDetailView
import ch.seesturm.pfadiseesturm.presentation.aktuell.detail.AktuellDetailViewModel
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.AktuellView
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.AktuellViewModel
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.fcmModule
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenView
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenViewModel
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AktuellNavHost(
    bottomNavigationInnerPadding: PaddingValues,
    aktuellNavController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = aktuellNavController,
        startDestination = AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellRoot
    ) {
        composable<AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellRoot> {
            AktuellView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                aktuellNavController = aktuellNavController,
                viewModel = viewModel<AktuellViewModel>(
                    factory = viewModelFactoryHelper {
                        AktuellViewModel(
                            service = wordpressModule.aktuellService
                        )
                    }
                )
            )
        }
        composable<AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellDetail> {
            val postId = it.toRoute<AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellDetail>().postId
            AktuellDetailView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = aktuellNavController,
                viewModel = viewModel<AktuellDetailViewModel>(
                    factory = viewModelFactoryHelper {
                        AktuellDetailViewModel(
                            postId = postId,
                            service = wordpressModule.aktuellService,
                            cacheIdentifier = MemoryCacheIdentifier.List
                        )
                    }
                ),
                onPushNotificationsNavigate = {
                    aktuellNavController.navigate(
                        AppDestination.MainTabView.Destinations.Aktuell.Destinations.PushNotifications
                    )
                }
            )
        }
        composable<AppDestination.MainTabView.Destinations.Aktuell.Destinations.PushNotifications> {
            PushNachrichtenVerwaltenView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = aktuellNavController,
                viewModel = viewModel<PushNachrichtenVerwaltenViewModel>(
                    factory = viewModelFactoryHelper {
                        PushNachrichtenVerwaltenViewModel(
                            service = fcmModule.fcmSubscriptionService
                        )
                    }
                )
            )
        }
    }
}