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
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.main.AuthViewModel
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.accountModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.AnlaesseView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.AnlaesseViewModel
import ch.seesturm.pfadiseesturm.presentation.common.event_management.ManageEventView
import ch.seesturm.pfadiseesturm.presentation.common.event_management.ManageEventViewModel
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventManagementMode
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventManagementModeNavType
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventToManageType
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper
import kotlin.reflect.typeOf

@Composable
fun AnlaesseNavHost(
    appStateViewModel: AppStateViewModel,
    authViewModel: AuthViewModel,
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
        composable<AppDestination.MainTabView.Destinations.Anlaesse.Destinations.AnlaesseRoot> {
            AnlaesseView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                onNavigateToDetail = { eventId ->
                    anlaesseNavController.navigate(
                        AppDestination.MainTabView.Destinations.Anlaesse.Destinations.AnlaesseDetail(
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
                ),
                authViewModel = authViewModel,
                appStateViewModel = appStateViewModel,
                onAddEvent = {
                    anlaesseNavController.navigate(
                        AppDestination.MainTabView.Destinations.Anlaesse.Destinations.ManageTermin(
                            calendar = SeesturmCalendar.TERMINE,
                            mode = EventManagementMode.Insert
                        )
                    )
                }
            )
        }
        composable<AppDestination.MainTabView.Destinations.Anlaesse.Destinations.AnlaesseDetail> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Anlaesse.Destinations.AnlaesseDetail>()
            AnlaesseDetailView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = anlaesseNavController,
                calendar = SeesturmCalendar.TERMINE,
                viewModel = viewModel<AnlaesseDetailViewModel>(
                    factory = viewModelFactoryHelper {
                        AnlaesseDetailViewModel(
                            calendar = SeesturmCalendar.TERMINE,
                            eventId = arguments.eventId,
                            service = wordpressModule.anlaesseService,
                            cacheIdentifier = MemoryCacheIdentifier.TryGetFromListCache
                        )
                    }
                ),
                authViewModel = authViewModel,
                onEditEvent = {
                    anlaesseNavController.navigate(
                        AppDestination.MainTabView.Destinations.Anlaesse.Destinations.ManageTermin(
                            calendar = SeesturmCalendar.TERMINE,
                            mode = EventManagementMode.Update(arguments.eventId)
                        )
                    )
                }
            )
        }
        composable<AppDestination.MainTabView.Destinations.Anlaesse.Destinations.ManageTermin>(
            typeMap = mapOf(typeOf<EventManagementMode>() to EventManagementModeNavType)
        ) {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Anlaesse.Destinations.ManageTermin>()
            ManageEventView(
                viewModel = viewModel<ManageEventViewModel>(
                    factory = viewModelFactoryHelper {
                        ManageEventViewModel(
                            stufenbereichService = accountModule.stufenbereichService,
                            anlaesseService = wordpressModule.anlaesseService,
                            eventType = EventToManageType.Termin(
                                calendar = arguments.calendar,
                                mode = arguments.mode
                            )
                        )
                    }
                ),
                appStateViewModel = appStateViewModel,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = anlaesseNavController,
                onNavigateToTemplates = null
            )
        }
    }
}