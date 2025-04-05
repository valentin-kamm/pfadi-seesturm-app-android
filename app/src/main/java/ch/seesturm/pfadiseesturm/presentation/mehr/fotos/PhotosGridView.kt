package ch.seesturm.pfadiseesturm.presentation.mehr.fotos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.PhotosRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.auth.service.AuthService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.PhotosService
import ch.seesturm.pfadiseesturm.presentation.common.BottomSheetContent
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.PhotoGalleryCell
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.PhotoGalleryLoadingCell
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun PhotosGridView(
    viewModel: PhotosGridViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    albumTitle: String,
    onNavigateToSlider: (Int) -> Unit,
    gridState: LazyGridState = rememberLazyGridState()
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = albumTitle,
        backNavigationAction = {
            navController.popBackStack()
        }
    ) { topBarInnerPadding ->

        val additionalPadding = 2.dp

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(topBarInnerPadding, LayoutDirection.Ltr)

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val numberOfColumns = when (uiState.result) {
                is UiState.Error -> GridCells.Fixed(1)
                else -> GridCells.Fixed(3)
            }
            val columnWidth = (maxWidth - additionalPadding * (3 - 1)) / 3

            LazyVerticalGrid(
                state = gridState,
                columns = numberOfColumns,
                contentPadding = combinedPadding,
                userScrollEnabled = !uiState.result.scrollingDisabled,
                verticalArrangement = Arrangement.spacedBy(additionalPadding),
                horizontalArrangement = Arrangement.spacedBy(additionalPadding),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (val localState = uiState.result) {
                    UiState.Loading -> {
                        items(
                            count = 30,
                            key = { index ->
                                "PhotoGridLoadingCell$index"
                            }
                        ) { _ ->
                            PhotoGalleryLoadingCell(
                                size = columnWidth,
                                withText = false,
                                modifier = Modifier
                                    .animateItem()
                            )
                        }
                    }

                    is UiState.Error -> {
                        item(
                            key = "PhotoGridErrorCell"
                        ) {
                            CardErrorView(
                                errorTitle = "Ein Fehler ist aufgetreten",
                                errorDescription = localState.message,
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                viewModel.fetchPhotos()
                            }
                        }
                    }

                    is UiState.Success -> {
                        itemsIndexed(
                            items = localState.data,
                            key = { index, _ ->
                                "AlbumsPhotoCell$index"
                            }
                        ) { index, item ->
                            PhotoGalleryCell(
                                size = columnWidth,
                                thumbnailUrl = item.thumbnail,
                                title = null,
                                onClick = {
                                    onNavigateToSlider(index)
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