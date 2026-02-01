package ch.seesturm.pfadiseesturm.presentation.common.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
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
import ch.seesturm.pfadiseesturm.main.AuthViewModel
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
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.templates.TemplateEditListView
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.templates.TemplateViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.AnlaesseView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.AnlaesseViewModel
import ch.seesturm.pfadiseesturm.presentation.common.event_management.ManageEventView
import ch.seesturm.pfadiseesturm.presentation.common.event_management.ManageEventViewModel
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventManagementMode
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventToManageNavType
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventToManageType
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailView
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailViewLocation
import ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail.AktivitaetDetailViewModel
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.types.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper
import kotlin.reflect.typeOf

@Composable
fun AccountNavHost(
    tabNavController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    appStateViewModel: AppStateViewModel,
    authViewModel: AuthViewModel,
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
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.AccountRoot> {
            AccountView(
                authViewModel = authViewModel,
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
                                    fcmService = fcmModule.fcmService
                                )
                            }
                        )
                        LeiterbereichView(
                            user = user,
                            bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                            accountNavController = accountNavController,
                            leiterbereichViewModel = leiterbereichViewModel,
                            appStateViewModel = appStateViewModel,
                            authViewModel = authViewModel
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
                onAddEvent = {
                    accountNavController.navigate(
                        AppDestination.MainTabView.Destinations.Account.Destinations.ManageEvent(
                            type = EventToManageType.Termin(
                                calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
                                mode = EventManagementMode.Insert
                            )
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
                calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
                authViewModel = authViewModel,
                appStateViewModel = appStateViewModel
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
                calendar = arguments.calendar,
                authViewModel = authViewModel,
                onEditEvent = {
                    accountNavController.navigate(
                        AppDestination.MainTabView.Destinations.Account.Destinations.ManageEvent(
                            type = EventToManageType.Termin(
                                calendar = arguments.calendar,
                                mode = EventManagementMode.Update(arguments.eventId)
                            )
                        )
                    )
                }
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.Stufenbereich> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.Stufenbereich>()
            StufenbereichView(
                stufe = arguments.stufe,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                accountNavController = accountNavController,
                appStateViewModel = appStateViewModel,
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
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.ManageEvent>(
            typeMap = mapOf(typeOf<EventToManageType>() to EventToManageNavType)
        ) {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.ManageEvent>()
            ManageEventView(
                viewModel = viewModel<ManageEventViewModel>(
                    factory = viewModelFactoryHelper {
                        ManageEventViewModel(
                            stufenbereichService = accountModule.stufenbereichService,
                            anlaesseService = wordpressModule.anlaesseService,
                            eventType = arguments.type
                        )
                    }
                ),
                appStateViewModel = appStateViewModel,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = accountNavController,
                onNavigateToTemplates = { stufe ->
                    accountNavController.navigate(
                        AppDestination.MainTabView.Destinations.Account.Destinations.Templates(
                            stufe = stufe
                        )
                    )
                }
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.Templates> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.Templates>()
            TemplateEditListView(
                viewModel = viewModel<TemplateViewModel>(
                    factory = viewModelFactoryHelper {
                        TemplateViewModel(
                            stufe = arguments.stufe,
                            service = accountModule.stufenbereichService
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