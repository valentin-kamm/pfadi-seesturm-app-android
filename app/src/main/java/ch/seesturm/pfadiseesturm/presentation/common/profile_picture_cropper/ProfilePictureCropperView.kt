package ch.seesturm.pfadiseesturm.presentation.common.profile_picture_cropper


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.domain.storage.model.PickedGalleryItem
import ch.seesturm.pfadiseesturm.domain.storage.model.ProfilePicture
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import com.composeunstyled.Text
import kotlinx.coroutines.launch

@Composable
fun ProfilePictureCropperView(
    image: PickedGalleryItem,
    viewDpSize: DpSize,
    onCrop: (SeesturmResult<ProfilePicture, DataError.Local>) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    maskWidthMultiplier: Float = 0.9f,
    maxMagnificationScale: Float = 5f
) {

    val coroutineScope = rememberCoroutineScope()

    val state = rememberProfilePictureCropperState(
        image = image,
        viewDpSize = viewDpSize,
        maskWidthMultiplier = maskWidthMultiplier,
        maxMagnificationScale = maxMagnificationScale
    )

    ProfilePictureCropperContentView(
        bitmap = image.bitmap,
        scaleX = state.scale,
        scaleY = state.scale,
        translationX = state.offset.x,
        translationY = state.offset.y,
        onUpdateTransform = { pan, zoom ->
            state.updateTransform(pan, zoom)
        },
        onCrop = {
            coroutineScope.launch {
                onCrop(state.crop(image))
            }
        },
        onCancel = onCancel,
        modifier = modifier,
        isCropping = state.isCropping,
        maskDiameter = state.maskDiameter
    )
}

@Composable
private fun ProfilePictureCropperContentView(
    bitmap: ImageBitmap,
    scaleX: Float,
    scaleY: Float,
    translationX: Float,
    translationY: Float,
    isCropping: Boolean,
    maskDiameter: Float,
    onUpdateTransform: (Offset, Float) -> Unit,
    onCrop: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .graphicsLayer {
                    this.scaleX = scaleX
                    this.scaleY = scaleY
                    this.translationX = translationX
                    this.translationY = translationY
                    this.transformOrigin = TransformOrigin.Center
                }
                .pointerInput(isCropping) {
                    if (!isCropping) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            onUpdateTransform(pan, zoom)
                        }
                    }
                }
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    compositingStrategy = CompositingStrategy.Offscreen
                )
        ) {
            drawRect(
                color = Color.Black.copy(alpha = 0.5f)
            )
            drawCircle(
                center = size.center,
                color = Color.Transparent,
                radius = maskDiameter / 2,
                blendMode = BlendMode.Clear
            )
        }
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
                    title = "Auswählen",
                    onClick = onCrop,
                    isLoading = isCropping
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfilePictureCropperViewPreview() {

    val bitmap = ImageBitmap.imageResource(
        R.drawable.onboarding_welcome_image
    )

    PfadiSeesturmTheme {
        ProfilePictureCropperContentView(
            bitmap = bitmap,
            scaleX = 1f,
            scaleY = 1f,
            translationX = 0f,
            translationY = 0f,
            isCropping = false,
            onUpdateTransform = { _, _ -> },
            onCrop = { },
            onCancel = {},
            modifier = Modifier,
            maskDiameter = 1000f
        )
    }
}