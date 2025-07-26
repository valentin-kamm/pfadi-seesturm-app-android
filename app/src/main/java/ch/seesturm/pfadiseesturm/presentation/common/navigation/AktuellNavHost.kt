package ch.seesturm.pfadiseesturm.presentation.common.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.fcmModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.presentation.aktuell.detail.AktuellDetailView
import ch.seesturm.pfadiseesturm.presentation.aktuell.detail.AktuellDetailViewModel
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.AktuellView
import ch.seesturm.pfadiseesturm.presentation.aktuell.list.AktuellViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenView
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenViewModel
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper

@Composable
fun AktuellNavHost(
    bottomNavigationInnerPadding: PaddingValues,
    aktuellNavController: NavHostController = rememberNavController()
) {

    // react to programmatic navigation events
    ObserveAsEvents(
        flow = SeesturmNavigationController.aktuellEvents
    ) { destination ->
        println("Intent: navigated")
        aktuellNavController.navigate(destination) {
            popUpTo(AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellRoot) { inclusive = false }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = aktuellNavController,
        startDestination = AppDestination.MainTabView.Destinations.Aktuell.Destinations.AktuellRoot,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }
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
                            cacheIdentifier = MemoryCacheIdentifier.TryGetFromListCache
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
                            service = fcmModule.fcmService
                        )
                    }
                )
            )
        }
    }
}