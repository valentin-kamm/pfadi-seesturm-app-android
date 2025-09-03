package ch.seesturm.pfadiseesturm.presentation.common.image_cropper

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.domain.storage.model.JPGData
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.aspectRatio
import ch.seesturm.pfadiseesturm.util.imageFitSize
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.viewModelFactoryHelper
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min


@Composable
fun CircularImageCropperView(
    viewModelStoreOwner: ViewModelStoreOwner,
    viewSize: Size,
    image: JPGData,
    onCrop: (SeesturmResult<JPGData, DataError.Local>) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    maskWidthMultiplier: Float = 0.9f,
    maxMagnificationScale: Float = 5.0f
) {

    val coroutineScope = rememberCoroutineScope()

    val maskDiameter = min(
        viewSize.width * maskWidthMultiplier,
        viewSize.height * maskWidthMultiplier
    )
    val imageSizeInView = viewSize.imageFitSize(imageAspectRatio = image.originalBitmap.aspectRatio)
    val initialZoomScale = maskDiameter / min(imageSizeInView.width, imageSizeInView.height)
    val maxScale = max(maxMagnificationScale, initialZoomScale)

    val viewModel: CircularImageCropperViewModel = viewModel(
        factory = viewModelFactoryHelper {
            CircularImageCropperViewModel(
                initialScale = initialZoomScale,
                maskDiameter = maskDiameter,
                imageSizeInView = imageSizeInView,
                maxMagnificationScale = maxScale
            )
        },
        viewModelStoreOwner = viewModelStoreOwner
    )

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    CircularImageCropperContentView(
        image = image,
        maskDiameter = maskDiameter,
        uiState = uiState,
        onTransform = { pan, zoom ->
            viewModel.onTransform(pan, zoom)
        },
        onCancel = onCancel,
        onCrop = {
            coroutineScope.launch {
                val result = viewModel.cropImage(image.originalBitmap)
                onCrop(result)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun CircularImageCropperContentView(
    image: JPGData,
    maskDiameter: Float,
    uiState: ImageCropperState,
    onTransform: (Offset, Float) -> Unit,
    onCrop: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            bitmap = image.originalBitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .graphicsLayer {
                    scaleX = uiState.scale
                    scaleY = uiState.scale
                    translationX = uiState.offset.x
                    translationY = uiState.offset.y
                }
                .then(
                    if (!uiState.isCropping) {
                        Modifier
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    onTransform(pan, zoom)
                                }
                            }
                    } else {
                        Modifier
                    }
                )
        )

        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent {
                    drawContent()

                    val center = Offset(size.width / 2f, size.height / 2f)
                    drawRect(color = Color.Black.copy(alpha = 0.5f))
                    drawCircle(
                        color = Color.Transparent,
                        radius = maskDiameter / 2f,
                        center = center,
                        blendMode = BlendMode.Clear
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bewegen und skalieren",
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        buttonColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    title = "Abbrechen",
                    onClick = onCancel
                )
                SeesturmButton(
                    type = SeesturmButtonType.Primary(
                        buttonColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    isLoading = uiState.isCropping,
                    title = "Auswählen",
                    onClick = onCrop
                )
            }
        }
    }
}

@Preview
@Composable
private fun CircularImageCropperViewPreview() {
    val density = LocalDensity.current
    PfadiSeesturmTheme {
        BoxWithConstraints {
            val maskDiameter = with(density) {
                (0.9 * maxWidth).toPx()
            }
            CircularImageCropperContentView(
                image = JPGData.fromResource(R.drawable.onboarding_welcome_image),
                maskDiameter = maskDiameter,
                uiState = ImageCropperState(
                    scale = 1.0f,
                    maskSize = Size(width = maskDiameter, height = maskDiameter)
                ),
                onTransform = { _, _ -> },
                onCrop = {},
                onCancel = {},
                modifier = Modifier
            )
        }
    }
}