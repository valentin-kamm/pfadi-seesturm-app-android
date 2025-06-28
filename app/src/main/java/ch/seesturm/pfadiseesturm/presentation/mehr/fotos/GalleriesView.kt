package ch.seesturm.pfadiseesturm.presentation.mehr.fotos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhotoGallery
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.PhotoGalleryCell
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.PhotoGalleryLoadingCell
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun GalleriesView(
    viewModel: GalleriesViewModel,
    type: PhotoGalleriesType,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    GalleriesContentView(
        galleryState = uiState,
        type = type,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        navController = navController,
        onRetry = {
            viewModel.fetchGalleries()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleriesContentView(
    galleryState: UiState<List<WordpressPhotoGallery>>,
    type: PhotoGalleriesType,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    onRetry: () -> Unit,
    gridState: LazyGridState = rememberLazyGridState()
) {

    val numberOfColumns = when (galleryState) {
        is UiState.Error -> GridCells.Fixed(1)
        UiState.Loading -> GridCells.Fixed(2)
        is UiState.Success -> {
            if (galleryState.data.isEmpty()) {
                GridCells.Fixed(1)
            }
            else {
                GridCells.Fixed(2)
            }
        }
    }

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = when (type) {
            PhotoGalleriesType.Pfadijahre -> "Fotos"
            is PhotoGalleriesType.Albums -> type.name
        },
        onNavigateBack = {
            navController.navigateUp()
        }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(
            other = topBarInnerPadding,
            layoutDirection = LayoutDirection.Ltr,
            additionalTopPadding = 16.dp,
            additionalEndPadding = 16.dp,
            additionalStartPadding = 16.dp,
            additionalBottomPadding = 16.dp
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {

            val columnWidth = (maxWidth - 16.dp * (2 + 1)) / 2

            LazyVerticalGrid(
                state = gridState,
                columns = numberOfColumns,
                contentPadding = combinedPadding,
                userScrollEnabled = !galleryState.scrollingDisabled,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (galleryState) {
                    UiState.Loading -> {
                        items(
                            count = 20,
                            key = { index ->
                                "AlbumsPhotoLoadingCell$index"
                            }
                        ) { _ ->
                            PhotoGalleryLoadingCell(
                                size = columnWidth,
                                withText = true,
                                modifier = Modifier
                                    .animateItem()
                            )
                        }
                    }
                    is UiState.Error -> {
                        item(
                            key = "AlbumsPhotoErrorCell"
                        ) {
                            ErrorCardView(
                                errorTitle = "Ein Fehler ist aufgetreten",
                                errorDescription = galleryState.message,
                                modifier = Modifier
                                    .animateItem()
                            ) {
                                onRetry()
                            }
                        }
                    }
                    is UiState.Success -> {
                        if (galleryState.data.isEmpty()) {
                            item(
                                key = "KeineAlbenCell"
                            ) {
                                Text(
                                    "Keine Fotos",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 75.dp)
                                        .padding(horizontal = 16.dp)
                                        .alpha(0.4f)
                                        .animateItem()
                                )
                            }
                        }
                        else {
                            items(
                                items = galleryState.data.reversed(),
                                key = { item ->
                                    "AlbumsPhotoCell${item.id}"
                                }
                            ) { item ->
                                PhotoGalleryCell(
                                    size = columnWidth,
                                    thumbnailUrl = item.thumbnailUrl,
                                    title = item.title,
                                    onClick = {
                                        navController.navigate(
                                            when (type) {
                                                is PhotoGalleriesType.Albums -> {
                                                    AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph(
                                                        id = item.id,
                                                        title = item.title
                                                    )
                                                }
                                                PhotoGalleriesType.Pfadijahre -> {
                                                    AppDestination.MainTabView.Destinations.Mehr.Destinations.Albums(
                                                        id = item.id,
                                                        title = item.title
                                                    )
                                                }
                                            }
                                        )
                                    },
                                    modifier = Modifier
                                        .animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview("Loading")
@Composable
private fun GalleriesViewPreview1() {
    PfadiSeesturmTheme {
        GalleriesContentView(
            galleryState = UiState.Loading,
            type = PhotoGalleriesType.Pfadijahre,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            navController = rememberNavController(),
            onRetry = {}
        )
    }
}
@Preview("Error")
@Composable
private fun GalleriesViewPreview2() {
    PfadiSeesturmTheme {
        GalleriesContentView(
            galleryState = UiState.Error("Schwerer Fehler"),
            type = PhotoGalleriesType.Pfadijahre,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            navController = rememberNavController(),
            onRetry = {}
        )
    }
}

@Preview("Empty")
@Composable
private fun GalleriesViewPreview3() {
    PfadiSeesturmTheme {
        GalleriesContentView(
            galleryState = UiState.Success(emptyList()),
        type = PhotoGalleriesType.Pfadijahre,
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        navController = rememberNavController(),
        onRetry = {}
        )
    }
}
@Preview("Success")
@Composable
private fun GalleriesViewPreview4() {
    PfadiSeesturmTheme {
        GalleriesContentView(
            galleryState = UiState.Success(
                listOf(
                    WordpressPhotoGallery(
                        title = "Pfadijahr 2023",
                        id = "25",
                        thumbnailUrl = "https://seesturm.ch/wp-content/gallery/wofuba-17/IMG_9247.JPG"
                    ),
                    WordpressPhotoGallery(
                        title = "Pfadijahr 2023",
                        id = "26",
                        thumbnailUrl = "https://seesturm.ch/wp-content/gallery/wofuba-17/IMG_9247.JPG"
                    ),
                    WordpressPhotoGallery(
                        title = "Pfadijahr 2023",
                        id = "27",
                        thumbnailUrl = "https://seesturm.ch/wp-content/gallery/wofuba-17/IMG_9247.JPG"
                    )
                )
            ),
            type = PhotoGalleriesType.Pfadijahre,
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            navController = rememberNavController(),
            onRetry = {}
        )
    }
}