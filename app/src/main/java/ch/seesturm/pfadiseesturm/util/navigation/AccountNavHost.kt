package ch.seesturm.pfadiseesturm.util.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ch.seesturm.pfadiseesturm.presentation.account.auth.AccountView
import ch.seesturm.pfadiseesturm.presentation.account.food.EssenBestellenView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichView
import ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.LeiterbereichViewModel
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.StufenbereichView
import ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.StufenbereichViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.detail.AnlaesseDetailViewModel
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.AnlaesseView
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.AnlaesseViewModel
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.accountModule
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.authModule
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountNavHost(
    mainNavController: NavController,
    tabNavController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    appStateViewModel: AppStateViewModel,
    accountNavController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = accountNavController,
        startDestination = AppDestination.MainTabView.Destinations.Account.Destinations.AccountRoot
    ) {
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.AccountRoot> {
            AccountView(
                appStateViewModel = appStateViewModel,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                accountNavController = accountNavController,
                calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
                leiterbereich = { user ->
                    {
                        val leiterbereichViewModel = viewModel<LeiterbereichViewModel>(
                            tabNavController.getBackStackEntry<AppDestination.MainTabView.Destinations.Account>(),
                            factory = viewModelFactoryHelper {
                                LeiterbereichViewModel(
                                    service = accountModule.leiterbereichService,
                                    calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
                                    userId = user.userId,
                                    updateSheetContent = { content ->
                                        appStateViewModel.updateSheetContent(content)
                                    }
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
                onNavigateToDetail = { calendar, eventId ->
                    accountNavController.navigate(
                        AppDestination.MainTabView.Destinations.Account.Destinations.AccountTermineDetail(
                            cacheIdentifier = MemoryCacheIdentifier.List,
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
                ),
                openSheetUponNavigation = arguments.openSheetUponNavigation,
                appStateViewModel = appStateViewModel
            )
        }
        composable<AppDestination.MainTabView.Destinations.Account.Destinations.Food> {
            val arguments = it.toRoute<AppDestination.MainTabView.Destinations.Account.Destinations.Food>()
            val viewModel = viewModel<LeiterbereichViewModel>(
                tabNavController.getBackStackEntry<AppDestination.MainTabView.Destinations.Account>(),
                factory = viewModelFactoryHelper {
                    LeiterbereichViewModel(
                        service = accountModule.leiterbereichService,
                        calendar = arguments.calendar,
                        userId = arguments.userId,
                        updateSheetContent = { content ->
                            appStateViewModel.updateSheetContent(content)
                        },
                    )
                }
            )
            EssenBestellenView(
                userId = arguments.userId,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                accountNavController = accountNavController,
                viewModel = viewModel,
                appStateViewModel = appStateViewModel
            )
        }
    }
}