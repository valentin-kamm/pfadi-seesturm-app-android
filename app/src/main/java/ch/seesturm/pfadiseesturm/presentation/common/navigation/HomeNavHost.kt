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
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.authModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.dataStoreModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.fcmModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.presentation.aktuell.detail.AktuellDetailView
import ch.seesturm.pfadiseesturm.presentation.aktuell.detail.AktuellDetailViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailViewModel
import ch.seesturm.pfadiseesturm.presentation.home.HomeView
import ch.seesturm.pfadiseesturm.presentation.home.HomeViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.GespeichertePersonenView
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.GespeichertePersonenViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenView
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenViewModel
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailView
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailViewLocation
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailViewModel
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper

@Composable
fun HomeNavHost(
    bottomNavigationInnerPadding: PaddingValues,
    tabNavController: NavHostController,
    appStateViewModel: AppStateViewModel,
    homeNavController: NavHostController = rememberNavController()
) {

    // react to programmatic navigation events
    ObserveAsEvents(
        flow = SeesturmNavigationController.homeEvents
    ) { destination ->
        homeNavController.navigate(destination) {
            popUpTo(AppDestination.MainTabView.Destinations.Home.Destinations.HomeRoot) { inclusive = false }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = homeNavController,
        startDestination = AppDestination.MainTabView.Destinations.Home.Destinations.HomeRoot,
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
        composable<AppDestination.MainTabView.Destinations.Home.Destinations.HomeRoot> {
            HomeView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                homeNavController = homeNavController,
                tabNavController = tabNavController,
                calendar = SeesturmCalendar.TERMINE,
                appStateViewModel = appStateViewModel,
                viewModel = viewModel<HomeViewModel>(
                    factory = viewModelFactoryHelper {
                        HomeViewModel(
                            aktuellService = wordpressModule.aktuellService,
                            calendar = SeesturmCalendar.TERMINE,
                            anlaesseService = wordpressModule.anlaesseService,
                            weatherService = wordpressModule.weatherService,
                            naechsteAktivitaetService = wordpressModule.naechsteAktivitaetService
                        )
                    }
                )
            )
        }
        composable<AppDestination.MainTabView.Destinations.Home.Destinations.AktuellDetail> {
            val postId = it.toRoute<AppDestination.MainTabView.Destinations.Home.Destinations.AktuellDetail>().postId
            AktuellDetailView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = homeNavController,
                viewModel = viewModel<AktuellDetailViewModel>(
                    factory = viewModelFactoryHelper {
                        AktuellDetailViewModel(
                            postId = postId,
                            service = wordpressModule.aktuellService,
                            cacheIdentifier = MemoryCacheIdentifier.TryGetFromHomeCache
                        )
                    }
                ),
                onPushNotificationsNavigate = {
                    homeNavController.navigate(
                        AppDestination.MainTabView.Destinations.Home.Destinations.PushNotifications
                    )
                }
            )
        }
        composable<AppDestination.MainTabView.Destinations.Home.Destinations.AnlaesseDetail> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Home.Destinations.AnlaesseDetail>()
            AnlaesseDetailView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = homeNavController,
                calendar = arguments.calendar,
                viewModel = viewModel<AnlaesseDetailViewModel>(
                    factory = viewModelFactoryHelper {
                        AnlaesseDetailViewModel(
                            calendar = arguments.calendar,
                            eventId = arguments.eventId,
                            service = wordpressModule.anlaesseService,
                            cacheIdentifier = MemoryCacheIdentifier.TryGetFromHomeCache
                        )
                    }
                )
            )
        }
        composable<AppDestination.MainTabView.Destinations.Home.Destinations.PushNotifications> {
            PushNachrichtenVerwaltenView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = homeNavController,
                viewModel = viewModel<PushNachrichtenVerwaltenViewModel>(
                    factory = viewModelFactoryHelper {
                        PushNachrichtenVerwaltenViewModel(
                            service = fcmModule.fcmService
                        )
                    }
                )
            )
        }
        composable<AppDestination.MainTabView.Destinations.Home.Destinations.AktivitaetDetail> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Home.Destinations.AktivitaetDetail>()
            val type = AktivitaetDetailViewLocation.Home(
                eventId = arguments.eventId,
                onNavigateToPushNotifications = {
                    homeNavController.navigate(
                        AppDestination.MainTabView.Destinations.Home.Destinations.PushNotifications
                    )
                },
                onNavigateToGespeichertePersonen = {
                    homeNavController.navigate(
                        AppDestination.MainTabView.Destinations.Home.Destinations.GespeichertePersonen
                    )
                },
                getAktivitaet = {
                    wordpressModule.naechsteAktivitaetService.getOrFetchAktivitaetById(
                        stufe = arguments.stufe,
                        eventId = arguments.eventId ?: ""
                    )
                }
            )
            val viewModel = viewModel<AktivitaetDetailViewModel>(
                factory = viewModelFactoryHelper {
                    AktivitaetDetailViewModel(
                        service = wordpressModule.naechsteAktivitaetService,
                        gespeichertePersonenService = dataStoreModule.gespeichertePersonenService,
                        stufe = arguments.stufe,
                        dismissAnAbmeldenSheet = {
                            appStateViewModel.updateSheetContent(null)
                        },
                        userId = authModule.authRepository.getCurrentUid(),
                        type = type
                    )
                }
            )
            AktivitaetDetailView(
                viewModel = viewModel,
                stufe = arguments.stufe,
                location = type,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                onNavigateBack = {
                    homeNavController.navigateUp()
                },
                appStateViewModel = appStateViewModel
            )
        }
        composable<AppDestination.MainTabView.Destinations.Home.Destinations.GespeichertePersonen> {
            GespeichertePersonenView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = homeNavController,
                viewModel = viewModel<GespeichertePersonenViewModel>(
                    factory = viewModelFactoryHelper {
                        GespeichertePersonenViewModel(
                            service = dataStoreModule.gespeichertePersonenService,
                            updateSheetContent = { content ->
                                appStateViewModel.updateSheetContent(content)
                            },
                        )
                    }
                ),
                appStateViewModel = appStateViewModel
            )
        }
    }
}