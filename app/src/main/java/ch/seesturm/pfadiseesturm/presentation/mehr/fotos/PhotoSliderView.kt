package ch.seesturm.pfadiseesturm.presentation.mehr.fotos


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhoto
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.ZoomableAsyncImage
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle

@Composable
fun PhotoSliderView(
    viewModel: PhotosGridViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    PhotoSliderContentView(
        uiState = uiState,
        onRetry = {
            viewModel.fetchPhotos()
        },
        pageTitle = viewModel.pageTitle,
        onPageChange = { index ->
            viewModel.setSelectedImageIndex(index)
        },
        onClose = onClose,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoSliderContentView(
    uiState: PhotosGridState,
    pageTitle: String,
    onRetry: () -> Unit,
    onClose: () -> Unit,
    onPageChange: (Int) -> Unit,
    modifier: Modifier
) {

    val hideTopBar = rememberSaveable { mutableStateOf(false) }

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        hideTopBar = hideTopBar.value,
        title = pageTitle,
        navigationAction = TopBarNavigationIcon.Close { onClose() },
        modifier = modifier
            .clickable(
                indication = null,
                onClick = {
                    hideTopBar.value = !hideTopBar.value
                },
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(MaterialTheme.colorScheme.background),
    ) { topBarInnerPadding ->
        Box(
            contentAlignment = if (uiState.result is UiState.Error) {
                Alignment.TopCenter
            }
            else {
                Alignment.Center
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(topBarInnerPadding)
        ) {
            when (uiState.result) {
                UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .graphicsLayer()
                            .customLoadingBlinking()
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                is UiState.Error -> {
                    ErrorCardView(
                        errorDescription = uiState.result.message,
                        retryAction = {
                            onRetry()
                        },
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }
                is UiState.Success -> {

                    val pagerState = rememberPagerState(
                        initialPage = uiState.selectedImageIndex,
                        pageCount = { uiState.result.data.count() }
                    )

                    LaunchedEffect(pagerState) {
                        snapshotFlow { pagerState.currentPage }.collect { i ->
                            onPageChange(i)
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        beyondViewportPageCount = 1,
                        pageSize = PageSize.Fill,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                    ) { index ->

                        val selectedPhoto = uiState.result.data.getOrNull(index)

                        if (selectedPhoto == null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.HideImage,
                                    contentDescription = null,
                                    tint = Color.SEESTURM_GREEN,
                                    modifier = Modifier
                                        .size(50.dp)
                                )
                            }
                        }
                        else {
                            ZoomableAsyncImage(
                                photo = selectedPhoto,
                                onTap = {
                                    hideTopBar.value = !hideTopBar.value
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview("Loading")
@Composable
private fun PhotoSliderPreview1() {
    PfadiSeesturmTheme {
        PhotoSliderContentView(
            uiState = PhotosGridState(
                result = UiState.Loading
            ),
            pageTitle = "Test",
            onRetry = {},
            onPageChange = {},
            onClose = {},
            modifier = Modifier
        )
    }
}
@Preview("Error")
@Composable
private fun PhotoSliderPreview2() {
    PfadiSeesturmTheme {
        PhotoSliderContentView(
            uiState = PhotosGridState(
                result = UiState.Error("Schwerer Fehler")
            ),
            pageTitle = "Test",
            onRetry = {},
            onPageChange = {},
            onClose = {},
            modifier = Modifier
        )
    }
}
@Preview("Success")
@Composable
private fun PhotoSliderPreview3() {
    PfadiSeesturmTheme {
        PhotoSliderContentView(
            uiState = PhotosGridState(
                result = UiState.Success(
                    listOf(
                        WordpressPhoto(
                            thumbnailUrl = "https://ih1.redbubble.net/image.1742264708.3656/flat,750x1000,075,t.u1.jpg",
                            originalUrl = "https://ih1.redbubble.net/image.1742264708.3656/flat,750x1000,075,t.u1.jpg",
                            orientation = "",
                            height = 250,
                            width = 200
                        ),
                        WordpressPhoto(
                            thumbnailUrl = "https://ih1.redbubble.net/image.1742264708.3656/flat,750x1000,075,t.u1.jpg",
                            originalUrl = "",
                            orientation = "",
                            height = 600,
                            width = 100
                        ),
                        WordpressPhoto(
                            thumbnailUrl = "https://ih1.redbubble.net/image.1742264708.3656/flat,750x1000,075,t.u1.jpg",
                            originalUrl = "https://ih1.redbubble.net/image.1742264708.3656/flat,750x1000,075,t.u1.jpg",
                            orientation = "",
                            height = 100,
                            width = 400
                        )
                    )
                )
            ),
            pageTitle = "Test",
            onRetry = {},
            onPageChange = {},
            onClose = {},
            modifier = Modifier
        )
    }
}