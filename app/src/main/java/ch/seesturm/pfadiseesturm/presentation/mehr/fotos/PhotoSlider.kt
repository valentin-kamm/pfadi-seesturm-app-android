package ch.seesturm.pfadiseesturm.presentation.mehr.fotos

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhoto
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.util.state.UiState
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import java.io.File
import java.io.FileOutputStream

@Composable
fun PhotoSlider(
    viewModel: PhotosGridViewModel,
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    PhotoSliderContentView(
        uiState = uiState,
        navController = navController,
        onRetry = {
            viewModel.fetchPhotos()
        },
        bottomNavigationInnerPadding = bottomNavigationInnerPadding,
        pageTitle = viewModel.pageTitle,
        currentImageForSharing = viewModel.currentImageForSharing,
        onSaveBitmap = { index, bitmap ->
            viewModel.saveBitmapForSharing(index, bitmap)
        },
        onPageChange = { index ->
            viewModel.setSelectedImageIndex(index)
        }
    )
}

@Composable
private fun PhotoSliderContentView(
    uiState: PhotosGridState,
    pageTitle: String,
    navController: NavController,
    bottomNavigationInnerPadding: PaddingValues,
    currentImageForSharing: Bitmap?,
    onRetry: () -> Unit,
    onSaveBitmap: (Int, Bitmap) -> Unit,
    onPageChange: (Int) -> Unit
) {

    val context = LocalContext.current
    val hideTopBar = rememberSaveable { mutableStateOf(false) }

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        hideTopBar = hideTopBar.value,
        title = pageTitle,
        backNavigationAction = {
            navController.popBackStack()
        },
        floatingActionButton = {
            if (currentImageForSharing != null) {
                FloatingActionButton(
                    onClick = {
                        shareImage(context, currentImageForSharing)
                    },
                    containerColor = Color.SEESTURM_GREEN
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        },
        modifier = Modifier
            .clickable(
                indication = null,
                onClick = {
                    hideTopBar.value = !hideTopBar.value
                },
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(bottomNavigationInnerPadding)
    ) { topBarInnerPadding ->

        Box(
            contentAlignment = if (uiState.result is UiState.Error) { Alignment.TopCenter } else { Alignment.Center },
            modifier = Modifier
                .padding(topBarInnerPadding)
                .fillMaxSize()
        ) {
            when (uiState.result) {
                is UiState.Error -> {
                    CardErrorView(
                        errorDescription = uiState.result.message,
                        retryAction = {
                            onRetry()
                        },
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }
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
                is UiState.Success -> {
                    val pagerState = rememberPagerState(
                        initialPage = uiState.selectedImageIndex,
                        pageCount = { uiState.result.data.count() }
                    )

                    HorizontalPager(
                        state = pagerState,
                        beyondViewportPageCount = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background),

                    ) { index ->

                        LaunchedEffect(pagerState) {
                            snapshotFlow { pagerState.currentPage }.collect { index ->
                                onPageChange(index)
                            }
                        }

                        val selectedPhoto = uiState.result.data.getOrNull(index)
                        if (selectedPhoto != null) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(selectedPhoto.original)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                when (val localState = painter.state) {
                                    is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(selectedPhoto.width.toFloat() / selectedPhoto.height.toFloat())
                                                .graphicsLayer()
                                                .customLoadingBlinking()
                                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                    is AsyncImagePainter.State.Error -> {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(selectedPhoto.width.toFloat() / selectedPhoto.height.toFloat())
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
                                    is AsyncImagePainter.State.Success -> {
                                        SubcomposeAsyncImageContent()
                                        val bitmap = (localState.result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                                        if (bitmap != null) {
                                            onSaveBitmap(index, bitmap)
                                        }
                                    }
                                }
                            }
                        }
                        else {
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
                    }
                }
            }
        }
    }
}

// function to share the image view standard sharing intent
private fun shareImage(context: Context, image: Bitmap) {

    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val stream = FileOutputStream(cachePath.toString() + "/image.png")
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()

        val imagePath = File(context.cacheDir, "images")
        val newFile = File(imagePath, "image.png")
        val contentUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", newFile)

        // show share sheet
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Bild teilen"))
    }
    catch (e: Exception) {
        print("X")
    }
}

@Preview
@Composable
private fun PhotoSliderPreview() {
    PfadiSeesturmTheme {
        PhotoSliderContentView(
            uiState = PhotosGridState(
                result = UiState.Loading,
                selectedImageIndex = 0
            ),
            navController = rememberNavController(),
            onRetry = {},
            bottomNavigationInnerPadding = PaddingValues(0.dp),
            pageTitle = "1 von 3",
            currentImageForSharing = null,
            onSaveBitmap = { _, _ -> },
            onPageChange = {}
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun PhotoSliderPreview() {
    PhotoSlider(
        photos = listOf(
            WordpressPhoto(
                thumbnail = "https://seesturm.ch/wp-content/gallery/40-jahre-pfadi-seesturm/thumbs/thumbs_IMG_2817-1.jpg",
                original = "https://seesturm.ch/wp-content/gallery/40-jahre-pfadi-seesturm/IMG_2817-1.jpg",
                orientation = "1",
                height = 427,
                width = 640
            ),
            WordpressPhoto(
                thumbnail = "https://seesturm.ch/wp-content/gallery/40-jahre-pfadi-seesturm/thumbs/thumbs_IMG_2833-1.jpg",
                original = "https://seesturm.ch/wp-content/gallery/40-jahre-pfadi-seesturm/IMG_2833-1.jpg",
                orientation = "1",
                height = 427,
                width = 640
            ),
            WordpressPhoto(
                thumbnail = "https://seesturm.ch/wp-content/gallery/40-jahre-pfadi-seesturm/thumbs/thumbs_IMG_2867.thumb_-1.jpg",
                original = "https://seesturm.ch/wp-content/gallery/40-jahre-pfadi-seesturm/IMG_2867.thumb_-1.jpg",
                orientation = "o",
                height = 150,
                width = 100
            )
        ),
        initialImageIndex = 0,
        modifier = Modifier
            .fillMaxSize()
    )
}

 */