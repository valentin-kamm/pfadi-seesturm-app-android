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
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.dataStoreModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.fcmModule
import ch.seesturm.pfadiseesturm.main.SeesturmApplication.Companion.wordpressModule
import ch.seesturm.pfadiseesturm.presentation.mehr.MehrView
import ch.seesturm.pfadiseesturm.presentation.mehr.documents.DocumentsView
import ch.seesturm.pfadiseesturm.presentation.mehr.documents.DocumentsViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.GalleriesView
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.GalleriesViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PhotoGalleriesType
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PhotosGridView
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.PhotosGridViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.GespeichertePersonenView
import ch.seesturm.pfadiseesturm.presentation.mehr.gespeicherte_personen.GespeichertePersonenViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.LeitungsteamView
import ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.LeitungsteamViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenView
import ch.seesturm.pfadiseesturm.presentation.mehr.push_notifications.PushNachrichtenVerwaltenViewModel
import ch.seesturm.pfadiseesturm.util.ObserveAsEvents
import ch.seesturm.pfadiseesturm.util.types.WordpressDocumentType
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper

@Composable
fun MehrNavHost(
    bottomNavigationInnerPadding: PaddingValues,
    tabNavController: NavHostController,
    appStateViewModel: AppStateViewModel,
    mehrNavController: NavHostController = rememberNavController()
) {

    // react to programmatic navigation events
    ObserveAsEvents(
        flow = SeesturmNavigationController.mehrEvents
    ) { destination ->
        mehrNavController.navigate(destination) {
            popUpTo(AppDestination.MainTabView.Destinations.Mehr.Destinations.MehrRoot) { inclusive = false }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = mehrNavController,
        startDestination = AppDestination.MainTabView.Destinations.Mehr.Destinations.MehrRoot,
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
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.MehrRoot> {
            val viewModel = viewModel<GalleriesViewModel>(
                tabNavController.getBackStackEntry<AppDestination.MainTabView.Destinations.Mehr>(), // important: Scope viewmodel to the entire tab
                factory = viewModelFactoryHelper {
                    GalleriesViewModel(
                        service = wordpressModule.photosService,
                        type = PhotoGalleriesType.Pfadijahre

                    )
                }
            )
            MehrView(
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                mehrNavController = mehrNavController,
                viewModel = viewModel,
                appStateViewModel = appStateViewModel
            )
        }
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.Pfadijahre> {
            val viewModel = viewModel<GalleriesViewModel>(
                tabNavController.getBackStackEntry<AppDestination.MainTabView.Destinations.Mehr>(), // important: Scope viewmodel to the entire tab
                factory = viewModelFactoryHelper {
                    GalleriesViewModel(
                        service = wordpressModule.photosService,
                        type = PhotoGalleriesType.Pfadijahre
                    )
                }
            )
            GalleriesView(
                viewModel = viewModel,
                type = PhotoGalleriesType.Pfadijahre,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController
            )
        }
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.Albums> {
            val args = it.toRoute<AppDestination.MainTabView.Destinations.Mehr.Destinations.Albums>()
            val viewModel = viewModel<GalleriesViewModel>(
                factory = viewModelFactoryHelper {
                    GalleriesViewModel(
                        service = wordpressModule.photosService,
                        type = PhotoGalleriesType.Albums(
                            id = args.id,
                            name = args.title
                        )
                    )
                }
            )
            GalleriesView(
                viewModel = viewModel,
                type = PhotoGalleriesType.Albums(
                    id = args.id,
                    name = args.title
                ),
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController
            )
        }

        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.Photos> {

            val args = it.toRoute<AppDestination.MainTabView.Destinations.Mehr.Destinations.Photos>()

            PhotosGridView(
                viewModel = viewModel<PhotosGridViewModel>(
                    factory = viewModelFactoryHelper {
                        PhotosGridViewModel(
                            service = wordpressModule.photosService,
                            albumId = args.id
                        )
                    }
                ),
                appStateViewModel = appStateViewModel,
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController,
                albumTitle = args.title
            )
        }

        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.Dokumente> {
            DocumentsView(
                viewModel = viewModel<DocumentsViewModel>(
                    factory = viewModelFactoryHelper {
                        DocumentsViewModel(
                            service = wordpressModule.documentsService,
                            documentType = WordpressDocumentType.Documents
                        )
                    }
                ),
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController,
                documentType = WordpressDocumentType.Documents
            )
        }
        composable<AppDestination.MainTabView.Destinations.Mehr.Destinations.Luuchtturm> {
            DocumentsView(
                viewModel = viewModel<DocumentsViewModel>(
                    factory = viewModelFactoryHelper {
                        DocumentsViewModel(
                            service = wordpressModule.documentsService,
                            documentType = WordpressDocumentType.Luuchtturm
                        )
                    }
                ),
                bottomNavigationInnerPadding = bottomNavigationInnerPadding,
                navController = mehrNavController,
                documentType = WordpressDocumentType.Luuchtturm
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
                            service = fcmModule.fcmService
                        )
                    }
                )
            )
        }
    }
}
