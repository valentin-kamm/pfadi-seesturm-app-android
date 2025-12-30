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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhoto
import ch.seesturm.pfadiseesturm.main.AllowedOrientation
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.PhotoGalleryCell
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.PhotoGalleryLoadingCell
import ch.seesturm.pfadiseesturm.util.intersectWith
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle

@Composable
fun PhotosGridView(
    viewModel: PhotosGridViewModel,
    appStateViewModel: AppStateViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    albumTitle: String
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val selectedImageIndex = rememberSaveable { mutableStateOf<Int?>(null) }

    PhotosGridContentView(
        uiState = uiState,
        albumTitle = albumTitle,
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        navController = navController,
        onRetry = {
            viewModel.fetchPhotos()
        },
        onClickImage = { index ->
            selectedImageIndex.value = index
            appStateViewModel.updateAllowedOrientation(AllowedOrientation.All)
        }
    )

    val localSelectedImageIndex = selectedImageIndex.value
    val localImageResult = uiState.result

    if (localSelectedImageIndex != null && localImageResult is UiState.Success) {
        Dialog(
            onDismissRequest = {
                selectedImageIndex.value = null
            },
            properties = DialogProperties(
                dismissOnBackPress =  true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            PhotoSliderView(
                mode = PhotoSliderViewMode.Multi(
                    images = localImageResult.data.map { PhotoSliderViewItem.fromWordpressImage(it) },
                    initialIndex = localSelectedImageIndex
                ),
                onClose = {
                    selectedImageIndex.value = null
                    appStateViewModel.updateAllowedOrientation(AllowedOrientation.PortraitOnly)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotosGridContentView(
    uiState: PhotosGridState,
    albumTitle: String,
    onRetry: () -> Unit,
    onClickImage: (Int) -> Unit,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    gridState: LazyGridState = rememberLazyGridState()
) {

    val additionalPadding = 2.dp

    val numberOfColumns = when (uiState.result) {
        is UiState.Error -> GridCells.Fixed(1)
        UiState.Loading -> GridCells.Fixed(3)
        is UiState.Success -> {
            if (uiState.result.data.isEmpty()) {
                GridCells.Fixed(1)
            }
            else {
                GridCells.Fixed(3)
            }
        }
    }

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        title = albumTitle,
        navigationAction = TopBarNavigationIcon.Back { navController.navigateUp() }
    ) { topBarInnerPadding ->

        val combinedPadding = bottomNavigationInnerPadding.intersectWith(topBarInnerPadding, LayoutDirection.Ltr)

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {

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
                when (uiState.result) {
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
                            ErrorCardView(
                                errorTitle = "Ein Fehler ist aufgetreten",
                                errorDescription = uiState.result.message,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .animateItem()
                            ) {
                                onRetry()
                            }
                        }
                    }

                    is UiState.Success -> {
                        if (uiState.result.data.isEmpty()) {
                            item(
                                key = "KeineFotosCell"
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
                            itemsIndexed(
                                items = uiState.result.data,
                                key = { index, _ ->
                                    "AlbumsPhotoCell$index"
                                }
                            ) { index, item ->
                                PhotoGalleryCell(
                                    size = columnWidth,
                                    thumbnailUrl = item.thumbnailUrl,
                                    title = null,
                                    onClick = {
                                        onClickImage(index)
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
private fun PhotosGridViewPreview1() {
    PfadiSeesturmTheme {
        PhotosGridContentView(
            uiState = PhotosGridState(
                result = UiState.Loading
            ),
            albumTitle = "Test",
            onClickImage = {},
            onRetry = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            navController = rememberNavController()
        )
    }
}
@Preview("Error")
@Composable
private fun PhotosGridViewPreview2() {
    PfadiSeesturmTheme {
        PhotosGridContentView(
            uiState = PhotosGridState(
                result = UiState.Error("Schwerer Fehler")
            ),
            albumTitle = "Test",
            onClickImage = {},
            onRetry = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            navController = rememberNavController()
        )
    }
}
@Preview("Empty")
@Composable
private fun PhotosGridViewPreview3() {
    PfadiSeesturmTheme {
        PhotosGridContentView(
            uiState = PhotosGridState(
                result = UiState.Success(emptyList())
            ),
            albumTitle = "Test",
            onClickImage = {},
            onRetry = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            navController = rememberNavController()
        )
    }
}
@Preview("Success")
@Composable
private fun PhotosGridViewPreview4() {
    PfadiSeesturmTheme {
        PhotosGridContentView(
            uiState = PhotosGridState(
                result = UiState.Success(
                    listOf(
                        WordpressPhoto(
                            thumbnailUrl = "",
                            originalUrl = "",
                            orientation = "",
                            height = 200,
                            width = 200
                        ),
                        WordpressPhoto(
                            thumbnailUrl = "",
                            originalUrl = "",
                            orientation = "",
                            height = 200,
                            width = 200
                        ),
                        WordpressPhoto(
                            thumbnailUrl = "",
                            originalUrl = "",
                            orientation = "",
                            height = 200,
                            width = 200
                        ),
                        WordpressPhoto(
                            thumbnailUrl = "",
                            originalUrl = "",
                            orientation = "",
                            height = 200,
                            width = 200
                        )
                    )
                )
            ),
            albumTitle = "Test",
            onClickImage = {},
            onRetry = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            navController = rememberNavController()
        )
    }
}