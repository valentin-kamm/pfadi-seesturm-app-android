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
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailViewModel
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.home.HomeView
import ch.seesturm.pfadiseesturm.presentation.home.HomeViewModel
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.authModule
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.dataStoreModule
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.fcmModule
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.GespeichertePersonenView
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.GespeichertePersonenViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenView
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenViewModel
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailView
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailViewModel
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavHost(
    bottomNavigationInnerPadding: PaddingValues,
    tabNavController: NavHostController,
    mainNavController: NavHostController,
    appStateViewModel: AppStateViewModel,
    homeNavController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = homeNavController,
        startDestination = AppDestination.MainTabView.Destinations.Home.Destinations.HomeRoot
    ) {
        composable<AppDestination.MainTabView.Destinations.Home.Destinations.HomeRoot> {
            HomeView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                homeNavController = homeNavController,
                tabNavController = tabNavController,
                calendar = SeesturmCalendar.TERMINE,
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
                            cacheIdentifier = MemoryCacheIdentifier.Home
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
                            cacheIdentifier = MemoryCacheIdentifier.Home
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
                            service = fcmModule.fcmSubscriptionService
                        )
                    }
                )
            )
        }
        composable<AppDestination.MainTabView.Destinations.Home.Destinations.AktivitaetDetail> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Home.Destinations.AktivitaetDetail>()
            AktivitaetDetailView(
                stufe = arguments.stufe,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                homeNavController = homeNavController,
                viewModel = viewModel<AktivitaetDetailViewModel>(
                    factory = viewModelFactoryHelper {
                        AktivitaetDetailViewModel(
                            service = wordpressModule.naechsteAktivitaetService,
                            gespeichertePersonenService = dataStoreModule.gespeichertePersonenService,
                            stufe = arguments.stufe,
                            eventId = arguments.eventId,
                            dismiss = {
                                appStateViewModel.updateSheetContent(null)
                            },
                            userId = authModule.authService.getCurrentUid()
                        )
                    }
                ),
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