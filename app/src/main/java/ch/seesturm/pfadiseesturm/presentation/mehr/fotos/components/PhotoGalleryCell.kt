package ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

@Composable
fun PhotoGalleryCell(
    size: Dp,
    thumbnailUrl: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .width(size)
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable { onClick() }
                }
                else {
                    Modifier
                }
            )
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thumbnailUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(size)
                .clip(RoundedCornerShape(3.dp))
        ) {
            when (painter.state) {
                AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> {
                    Box(
                        modifier = Modifier
                            .graphicsLayer()
                            .fillMaxSize()
                            .customLoadingBlinking()
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                is AsyncImagePainter.State.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
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
                }
            }
        }
        if (title != null) {
            Text(
                text = title,
                textAlign = TextAlign.Start,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview("Image and Text", showBackground = true)
@Composable
private fun PhotoGalleryCellPreview1() {
    PfadiSeesturmTheme {
        PhotoGalleryCell(
            size = 120.dp,
            thumbnailUrl = "https://seesturm.ch/wp-content/gallery/wofuba-17/IMG_9247.JPG",
            title = "Test"
        )
    }
}
@Preview("Text and invalid image", showBackground = true)
@Composable
private fun PhotoGalleryCellPreview2() {
    PfadiSeesturmTheme {
        PhotoGalleryCell(
            size = 120.dp,
            thumbnailUrl = "",
            title = "Test text that is relatively long and will overlap"
        )
    }
}
@Preview("Without text", showBackground = true)
@Composable
private fun PhotoGalleryCellPreview3() {
    PfadiSeesturmTheme {
        PhotoGalleryCell(
            size = 120.dp,
            thumbnailUrl = "https://seesturm.ch/wp-content/gallery/wofuba-17/IMG_9247.JPG"
        )
    }
}