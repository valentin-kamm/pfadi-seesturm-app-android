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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ch.seesturm.pfadiseesturm.main.AllowedOrientation
import ch.seesturm.pfadiseesturm.main.AppStateViewModel
import ch.seesturm.pfadiseesturm.presentation.common.TopBarNavigationIcon
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components.ZoomableAsyncImage
import ch.seesturm.pfadiseesturm.util.types.TopBarStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSliderView(
    mode: PhotoSliderViewMode,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {

    val hideTopBar = rememberSaveable { mutableStateOf(false) }

    val imageIndex = rememberSaveable(mode) {
        mutableIntStateOf(
            when (mode) {
                is PhotoSliderViewMode.Multi -> mode.initialIndex
                is PhotoSliderViewMode.Single -> 0
            }
        )
    }

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        hideTopBar = hideTopBar.value,
        title = when (mode) {
            is PhotoSliderViewMode.Multi -> "${imageIndex.value + 1} von ${mode.images.count()}"
            is PhotoSliderViewMode.Single -> null
        },
        navigationAction = TopBarNavigationIcon.Close { onClose() },
        modifier = modifier
            .clickable(
                indication = null,
                onClick = {
                    hideTopBar.value = !hideTopBar.value
                },
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(MaterialTheme.colorScheme.background)
    ) { topBarInnerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(topBarInnerPadding)
        ) {
            when (mode) {
                is PhotoSliderViewMode.Single -> {
                    ZoomableAsyncImage(
                        photo = mode.image,
                        onTap = {
                            hideTopBar.value = !hideTopBar.value
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                is PhotoSliderViewMode.Multi -> {

                    val pagerState = rememberPagerState(
                        initialPage = imageIndex.value,
                        pageCount = { mode.images.count() }
                    )

                    LaunchedEffect(pagerState) {
                        snapshotFlow { pagerState.currentPage }.collect { i ->
                            imageIndex.value = i
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

                        val selectedPhoto = mode.images.getOrNull(index)

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

sealed interface PhotoSliderViewMode {
    data class Single(
        val image: PhotoSliderViewItem
    ): PhotoSliderViewMode
    data class Multi(
        val images: List<PhotoSliderViewItem>,
        val initialIndex: Int
    ): PhotoSliderViewMode
}

@Preview("Multi")
@Composable
private fun PhotoSliderPreview1() {
    PfadiSeesturmTheme {
        PhotoSliderView(
            mode = PhotoSliderViewMode.Multi(
                images = listOf(
                    PhotoSliderViewItem(
                        url = "https://ih1.redbubble.net/image.1742264708.3656/flat,750x1000,075,t.u1.jpg",
                        aspectRatio = 200.toFloat() / 250.toFloat()
                    ),
                    PhotoSliderViewItem(
                        url = "",
                        aspectRatio = 100.toFloat() / 600.toFloat()
                    ),
                    PhotoSliderViewItem(
                        url = "https://ih1.redbubble.net/image.1742264708.3656/flat,750x1000,075,t.u1.jpg",
                        aspectRatio = 400.toFloat() / 100.toFloat()
                    )
                ),
                initialIndex = 0
            ),
            onClose = {}
        )
    }
}

@Preview("Single")
@Composable
private fun PhotoSliderPreview2() {
    PfadiSeesturmTheme {
        PhotoSliderView(
            mode = PhotoSliderViewMode.Single(
                image = PhotoSliderViewItem(
                    url = "https://ih1.redbubble.net/image.1742264708.3656/flat,750x1000,075,t.u1.jpg",
                    aspectRatio = 200.toFloat() / 250.toFloat()
                )
            ),
            onClose = {}
        )
    }
}