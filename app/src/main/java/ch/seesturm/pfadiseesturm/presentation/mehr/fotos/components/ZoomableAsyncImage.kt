package ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhoto
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File
import java.io.FileOutputStream

@Composable
fun ZoomableAsyncImage(
    photo: WordpressPhoto,
    onTap: () -> Unit = {}
) {

    val context = LocalContext.current
    val zoomState = rememberZoomState(
        maxScale = 5.0f
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(photo.originalUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
            modifier = Modifier
                .zoomable(
                    zoomState = zoomState,
                    scrollGesturePropagation = ScrollGesturePropagation.NotZoomed,
                    onTap = {
                        onTap()
                    }
                )
        ) {

            LaunchedEffect(painter.state) {
                val localState = painter.state
                if (localState is AsyncImagePainter.State.Success) {
                    zoomState.setContentSize(localState.painter.intrinsicSize)
                }
            }

            when (painter.state) {
                AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading  -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(photo.width.toFloat() / photo.height.toFloat())
                                .fillMaxSize()
                                .graphicsLayer()
                                .customLoadingBlinking()
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        CircularProgressIndicator(
                            color = Color.SEESTURM_GREEN,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }
                is AsyncImagePainter.State.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .aspectRatio(photo.width.toFloat() / photo.height.toFloat())
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
                }
                is AsyncImagePainter.State.Success -> {
                    SubcomposeAsyncImageContent()
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