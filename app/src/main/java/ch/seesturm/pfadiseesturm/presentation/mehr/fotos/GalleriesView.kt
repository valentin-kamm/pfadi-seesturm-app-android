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
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.PhotosRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.wordpress.service.PhotosService
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.PhotoGalleryCell
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.PhotoGalleryLoadingCell
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleriesView(
    viewModel: GalleriesViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    pfadijahrTitle: String,
    gridState: LazyGridState = rememberLazyGridState()
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = pfadijahrTitle,
        backNavigationAction = {
            navController.popBackStack()
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
            val numberOfColumns = when (uiState) {
                is UiState.Error -> GridCells.Fixed(1)
                else -> GridCells.Fixed(2)
            }
            val columnWidth = (maxWidth - 16.dp * (2 + 1)) / 2

            LazyVerticalGrid(
                state = gridState,
                columns = numberOfColumns,
                contentPadding = combinedPadding,
                userScrollEnabled = !uiState.scrollingDisabled,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (val localState = uiState) {
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
                            CardErrorView(
                                errorTitle = "Ein Fehler ist aufgetreten",
                                errorDescription = localState.message,
                                modifier = Modifier
                            ) {
                                viewModel.fetchAlbums()
                            }
                        }
                    }
                    is UiState.Success -> {
                        if (localState.data.isEmpty()) {
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
                                items = localState.data.reversed(),
                                key = { item ->
                                    "AlbumsPhotoCell${item.id}"
                                }
                            ) { item ->
                                PhotoGalleryCell(
                                    size = columnWidth,
                                    thumbnailUrl = item.thumbnail,
                                    title = item.title,
                                    onClick = {
                                        navController.navigate(
                                            AppDestination.MainTabView.Destinations.Mehr.Destinations.PhotosGraph(
                                                id = item.id,
                                                title = item.title
                                            )
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

@Preview
@Composable
fun GalleriesViewPreview() {
    GalleriesView(
        viewModel = GalleriesViewModel(
            service = PhotosService(
                repository = PhotosRepositoryImpl(
                    api = Retrofit.Builder()
                        .baseUrl(Constants.SEESTURM_API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(WordpressApi::class.java)
                )
            ),
            pfadijahrId = "123",
        ),
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        navController = rememberNavController(),
        pfadijahrTitle = "Test"
    )
}
