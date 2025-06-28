package ch.seesturm.pfadiseesturm.presentation.common.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.accountModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.dataStoreModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.fcmModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.presentation.account.auth.AccountView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichViewModel
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.food.OrdersView
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.StufenbereichView
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.StufenbereichViewModel
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.AktivitaetBearbeitenMode
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.AktivitaetBearbeitenView
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.AktivitaetBearbeitenViewModel
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates.TemplateEditListView
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten.templates.TemplateViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.AnlaesseView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.AnlaesseViewModel
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailView
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailViewLocation
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailViewModel
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper

@Composable
fun AccountNavHost(
    tabNavController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    appStateViewModel: AppStateViewModel,
    accountNavController: NavHostController = rememberNavController()
) {

    // react to programmatic navigation events
    ObserveAsEvents(
        flow = SeesturmNavigationController.accountEvents
    ) { destination ->
        accountNavController.navigate(destination) {
            popUpTo(AppDestination.MainTabView.Destinations.Account.Destinations.AccountRoot) { inclusive = false }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = accountNavController,
        startDestination = AppDestination.MainTabView.Destinations.Account.Destinations.AccountRoot,
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
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.AccountRoot> {
            AccountView(
                appStateViewModel = appStateViewModel,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                leiterbereich = { user ->
                    {
                        val leiterbereichViewModel = viewModel<LeiterbereichViewModel>(
                            tabNavController.getBackStackEntry<AppDestination.MainTabView.Destinations.Account>(),
                            factory = viewModelFactoryHelper {
                                LeiterbereichViewModel(
                                    leiterbereichService = accountModule.leiterbereichService,
                                    schoepflialarmService = accountModule.schoepflialarmService,
                                    calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
                                    userId = user.userId,
                                    userDisplayNameShort = user.displayNameShort,
                                    updateSheetContent = { content ->
                                        appStateViewModel.updateSheetContent(content)
                                    },
                                    fcmService = fcmModule.fcmService
                                )
                            }
                        )
                        LeiterbereichView(
                            user = user,
                            bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                            accountNavController = accountNavController,
                            viewModel = leiterbereichViewModel,
                            appStateViewModel = appStateViewModel
                        )
                    }
                }
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.AccountTermine> {
            AnlaesseView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                onNavigateBack = {
                    accountNavController.navigateUp()
                },
                onNavigateToDetail = { calendar, eventId ->
                    accountNavController.navigate(
                        AppDestination.MainTabView.Destinations.Account.Destinations.AccountTermineDetail(
                            cacheIdentifier = MemoryCacheIdentifier.TryGetFromListCache,
                            calendar = calendar,
                            eventId = eventId
                        )
                    )
                },
                viewModel = viewModel<AnlaesseViewModel>(
                    factory = viewModelFactoryHelper {
                        AnlaesseViewModel(
                            wordpressModule.anlaesseService,
                            calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM
                        )
                    }
                ),
                calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.AccountTermineDetail> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.AccountTermineDetail>()
            AnlaesseDetailView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = accountNavController,
                viewModel = viewModel<AnlaesseDetailViewModel>(
                    factory = viewModelFactoryHelper {
                        AnlaesseDetailViewModel(
                            calendar = arguments.calendar,
                            eventId = arguments.eventId,
                            service = wordpressModule.anlaesseService,
                            cacheIdentifier = arguments.cacheIdentifier
                        )
                    }
                ),
                calendar = arguments.calendar
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.Stufenbereich> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.Stufenbereich>()
            StufenbereichView(
                stufe = arguments.stufe,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                accountNavController = accountNavController,
                viewModel = viewModel<StufenbereichViewModel>(
                    factory = viewModelFactoryHelper {
                        StufenbereichViewModel(
                            stufe = arguments.stufe,
                            service = accountModule.stufenbereichService
                        )
                    }
                )
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.DisplayAktivitaet> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.DisplayAktivitaet>()
            val type = AktivitaetDetailViewLocation.Stufenbereich(
                eventId = arguments.id,
                getAktivitaet = {
                    accountModule.stufenbereichService.fetchEvent(
                        stufe = arguments.stufe,
                        eventId = arguments.id,
                        cacheIdentifier = MemoryCacheIdentifier.TryGetFromListCache
                    )
                }
            )
            val viewModel = viewModel<AktivitaetDetailViewModel>(
                factory = viewModelFactoryHelper {
                    AktivitaetDetailViewModel(
                        service = wordpressModule.naechsteAktivitaetService,
                        gespeichertePersonenService = dataStoreModule.gespeichertePersonenService,
                        stufe = arguments.stufe,
                        type = type,
                        dismissAnAbmeldenSheet = {},
                        userId = null
                    )
                }
            )
            AktivitaetDetailView(
                viewModel = viewModel,
                stufe = arguments.stufe,
                location = type,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                onNavigateBack = {
                    accountNavController.navigateUp()
                },
                appStateViewModel = appStateViewModel
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.Food> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.Food>()
            val viewModel = viewModel<LeiterbereichViewModel>(
                tabNavController.getBackStackEntry<AppDestination.MainTabView.Destinations.Account>(),
                factory = viewModelFactoryHelper {
                    LeiterbereichViewModel(
                        leiterbereichService = accountModule.leiterbereichService,
                        schoepflialarmService = accountModule.schoepflialarmService,
                        calendar = arguments.calendar,
                        userId = arguments.userId,
                        userDisplayNameShort = arguments.userDisplayNameShort,
                        updateSheetContent = { content ->
                            appStateViewModel.updateSheetContent(content)
                        },
                        fcmService = fcmModule.fcmService,
                    )
                }
            )
            OrdersView(
                userId = arguments.userId,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                accountNavController = accountNavController,
                viewModel = viewModel,
                appStateViewModel = appStateViewModel
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.NewAktivitaet> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.NewAktivitaet>()
            val viewModel = viewModel<AktivitaetBearbeitenViewModel>(
                factory = viewModelFactoryHelper {
                    AktivitaetBearbeitenViewModel(
                        mode = AktivitaetBearbeitenMode.Insert,
                        service = accountModule.stufenbereichService,
                        stufe = arguments.stufe
                    )
                }
            )
            AktivitaetBearbeitenView(
                viewModel = viewModel,
                appStateViewModel = appStateViewModel,
                stufe = arguments.stufe,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                mode = AktivitaetBearbeitenMode.Insert,
                accountNavController = accountNavController
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.UpdateAktivitaet> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.UpdateAktivitaet>()
            val viewModel = viewModel<AktivitaetBearbeitenViewModel>(
                factory = viewModelFactoryHelper {
                    AktivitaetBearbeitenViewModel(
                        mode = AktivitaetBearbeitenMode.Update(arguments.id),
                        service = accountModule.stufenbereichService,
                        stufe = arguments.stufe
                    )
                }
            )
            AktivitaetBearbeitenView(
                viewModel = viewModel,
                appStateViewModel = appStateViewModel,
                stufe = arguments.stufe,
                mode = AktivitaetBearbeitenMode.Update(arguments.id),
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                accountNavController = accountNavController
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.Templates> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.Templates>()
            TemplateEditListView(
                viewModel = viewModel<TemplateViewModel>(
                    factory = viewModelFactoryHelper {
                        TemplateViewModel(
                            stufe = arguments.stufe,
                            service = accountModule.stufenbereichService,
                            dismissSheet = {
                                appStateViewModel.updateSheetContent(null)
                            }
                        )
                    }
                ),
                appStateViewModel = appStateViewModel,
                stufe = arguments.stufe,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                accountNavController = accountNavController
            )
        }
    }
}