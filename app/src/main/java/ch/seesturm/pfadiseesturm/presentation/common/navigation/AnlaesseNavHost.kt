package ch.seesturm.pfadiseesturm.presentation.common.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.AnlaesseView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.AnlaesseViewModel
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper

@Composable
fun AnlaesseNavHost(
    bottomNavigationInnerPadding: PaddingValues,
    anlaesseNavController: NavHostController = rememberNavController()
) {

    // react to programmatic navigation events
    ObserveAsEvents(
        flow = SeesturmNavigationController.anlaesseEvents
    ) { destination ->
        anlaesseNavController.navigate(destination) {
            popUpTo(AppDestination.MainTabView.Destinations.Anlaesse.Destinations.AnlaesseRoot) { inclusive = false }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = anlaesseNavController,
        startDestination = AppDestination.MainTabView.Destinations.Anlaesse.Destinations.AnlaesseRoot,
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            fadeIn()
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }
    ) {
        composable<AppDestination.MainTabView.Destinations.Anlaesse.Destinations.AnlaesseRoot> {
            AnlaesseView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                onNavigateToDetail = { calendar, eventId ->
                    anlaesseNavController.navigate(
                        AppDestination.MainTabView.Destinations.Anlaesse.Destinations.AnlaesseDetail(
                            calendar = calendar,
                            eventId = eventId
                        )
                    )
                },
                calendar = SeesturmCalendar.TERMINE,
                viewModel = viewModel<AnlaesseViewModel>(
                    factory = viewModelFactoryHelper {
                        AnlaesseViewModel(
                            wordpressModule.anlaesseService, SeesturmCalendar.TERMINE
                        )
                    }
                )
            )
        }
        composable<AppDestination.MainTabView.Destinations.Anlaesse.Destinations.AnlaesseDetail> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Anlaesse.Destinations.AnlaesseDetail>()
            AnlaesseDetailView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = anlaesseNavController,
                calendar = arguments.calendar,
                viewModel = viewModel<AnlaesseDetailViewModel>(
                    factory = viewModelFactoryHelper {
                        AnlaesseDetailViewModel(
                            calendar = arguments.calendar,
                            eventId = arguments.eventId,
                            service = wordpressModule.anlaesseService,
                            cacheIdentifier = MemoryCacheIdentifier.TryGetFromListCache
                        )
                    }
                )
            )
        }
    }
}