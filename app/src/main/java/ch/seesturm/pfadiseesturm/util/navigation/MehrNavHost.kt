package ch.seesturm.pfadiseesturm.util.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.authModule
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.dataStoreModule
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.fcmModule
import ch.seesturm.pfadiseesturm.presentation.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.presentation.mehr.MehrView
import ch.seesturm.pfadiseesturm.presentation.mehr.documents.DokumenteView
import ch.seesturm.pfadiseesturm.presentation.mehr.documents.DokumenteViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.documents.LuuchtturmView
import ch.seesturm.pfadiseesturm.presentation.mehr.documents.LuuchtturmViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.GalleriesView
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.GalleriesViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PfadijahreView
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PfadijahreViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PhotoSlider
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PhotosGridView
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PhotosGridViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.GespeichertePersonenView
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.GespeichertePersonenViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.LeitungsteamView
import ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.LeitungsteamViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenView
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenViewModel

@Composable
fun MehrNavHost(
    bottomNavigationInnerPadding: PaddingValues,
    tabNavController: NavHostController,
    mainNavController: NavHostController,
    appStateViewModel: AppStateViewModel,
    mehrNavController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = mehrNavController,
        startDestination = AppDestination.MainTabView.Destinations.Mehr.Destinations.MehrRoot
    ) {
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.MehrRoot> {
            val pfadijahreViewModel = viewModel<PfadijahreViewModel>(
                tabNavController.getBackStackEntry<AppDestination.MainTabView.Destinations.Mehr>(), // important: Scope viewmodel to the entire tab
                factory = viewModelFactoryHelper {
                    PfadijahreViewModel(
                        service = wordpressModule.photosService
                    )
                }
            )
            MehrView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                mehrNavController = mehrNavController,
                viewModel = pfadijahreViewModel,
                appStateViewModel = appStateViewModel
            )
        }
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.Pfadijahre> {
            val pfadijahreViewModel = viewModel<PfadijahreViewModel>(
                tabNavController.getBackStackEntry<AppDestination.MainTabView.Destinations.Mehr>(), // important: Scope viewmodel to the entire tab
                factory = viewModelFactoryHelper {
                    PfadijahreViewModel(
                        service = wordpressModule.photosService
                    )
                }
            )
            PfadijahreView(
                viewModel = pfadijahreViewModel,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController
            )
        }
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.Albums> {
            val args = it.toRoute<AppDestination.MainTabView.Destinations.Mehr.Destinations.Albums>()
            GalleriesView(
                viewModel = viewModel<GalleriesViewModel>(
                    factory = viewModelFactoryHelper {
                        GalleriesViewModel(
                            service =  wordpressModule.photosService,
                            pfadijahrId = args.id
                        )
                    }
                ),
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController,
                pfadijahrTitle = args.title
            )
        }

        // sub-graph to scope view model to both screens
        navigation<AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph>(
            startDestination = AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph.Photos
        ) {
            composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph.Photos> {
                val parentEntry = remember(it) {
                    mehrNavController.getBackStackEntry<AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph>()
                }
                val args = parentEntry.toRoute<AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph>()
                val viewModel = viewModel<PhotosGridViewModel>(
                    parentEntry, // important
                    factory = viewModelFactoryHelper {
                        PhotosGridViewModel(
                            service = wordpressModule.photosService,
                            albumId = args.id
                        )
                    }
                )
                PhotosGridView(
                    viewModel = viewModel,
                    bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                    navController = mehrNavController,
                    albumTitle = args.title,
                    onNavigateToSlider = { selectedIndex ->
                        viewModel.setSelectedImageIndex(selectedIndex)
                        mehrNavController.navigate(
                            AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph.PhotosSlider
                        )
                    }
                )
            }

            composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph.PhotosSlider> {
                val parentEntry = remember(it) {
                    mehrNavController.getBackStackEntry<AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph>()
                }
                val args = parentEntry.toRoute<AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph>()
                val viewModel = viewModel<PhotosGridViewModel>(
                    parentEntry, // important
                    factory = viewModelFactoryHelper {
                        PhotosGridViewModel(
                            service = wordpressModule.photosService,
                            albumId = args.id
                        )
                    }
                )
                PhotoSlider(
                    viewModel = viewModel,
                    bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                    navController = mehrNavController
                )
            }
        }

        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.Dokumente> {
            DokumenteView(
                viewModel = viewModel<DokumenteViewModel>(
                    factory = viewModelFactoryHelper {
                        DokumenteViewModel(
                            service = wordpressModule.documentsService
                        )
                    }
                ),
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController
            )
        }
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.Luuchtturm> {
            LuuchtturmView(
                viewModel = viewModel<LuuchtturmViewModel>(
                    factory = viewModelFactoryHelper {
                        LuuchtturmViewModel(
                            service = wordpressModule.documentsService
                        )
                    }
                ),
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController
            )
        }
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.Leitungsteam> {
            LeitungsteamView(
                viewModel = viewModel<LeitungsteamViewModel>(
                    factory = viewModelFactoryHelper {
                        LeitungsteamViewModel(
                            service = wordpressModule.leitungsteamService
                        )
                    }
                ),
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController
            )
        }
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.GespeichertePersonen> {
            GespeichertePersonenView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController,
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
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.PushNotifications> {
            PushNachrichtenVerwaltenView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController,
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
