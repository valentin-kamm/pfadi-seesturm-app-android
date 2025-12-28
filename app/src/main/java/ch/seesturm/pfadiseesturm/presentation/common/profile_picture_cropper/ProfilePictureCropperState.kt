package ch.seesturm.pfadiseesturm.presentation.common.profile_picture_cropper

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import ch.seesturm.pfadiseesturm.domain.storage.model.PickedGalleryItem
import ch.seesturm.pfadiseesturm.domain.storage.model.ProfilePicture
import ch.seesturm.pfadiseesturm.presentation.common.aspectRatio
import ch.seesturm.pfadiseesturm.presentation.common.imageFitSize
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


class ProfilePictureCropperState internal constructor (
    val maskDiameter: Float,
    private val imageSizeInView: Size,
    private val maxMagnificationScale: Float,
    private val initialScale: Float
) {

    var scale by mutableFloatStateOf(initialScale)
        private set
    var offset by mutableStateOf(Offset.Zero)
        private set
    var isCropping by mutableStateOf(false)
        private set

    fun updateTransform(pan: Offset, zoom: Float) {

        val newScale = (scale * zoom).coerceIn(initialScale, maxMagnificationScale)

        // calculate max allowed offset so the image never leaves the mask
        val xLimit = max(0f, (imageSizeInView.width * newScale - maskDiameter) / 2f)
        val yLimit = max(0f, (imageSizeInView.height * newScale - maskDiameter) / 2f)

        val newOffset = offset + pan * scale

        scale = newScale
        offset = Offset(
            x = newOffset.x.coerceIn(-xLimit, xLimit),
            y = newOffset.y.coerceIn(-yLimit, yLimit)
        )
    }

    suspend fun crop(image: PickedGalleryItem): SeesturmResult<ProfilePicture, DataError.Local> {

        isCropping = true

        return withContext(Dispatchers.Default) {
            try {

                delay(1_000)

                val factor = image.bitmap.width / imageSizeInView.width
                val cropSizeInOriginal = maskDiameter / scale * factor

                val offsetXPx = offset.x / scale * factor
                val offsetYPx = offset.y / scale * factor

                val left = (image.bitmap.width - cropSizeInOriginal) / 2f - offsetXPx
                val top = (image.bitmap.height - cropSizeInOriginal) / 2f - offsetYPx

                val bitmap = Bitmap.createBitmap(
                    image.bitmap.asAndroidBitmap(),
                    left.roundToInt().coerceAtLeast(0),
                    top.roundToInt().coerceAtLeast(0),
                    cropSizeInOriginal.roundToInt(),
                    cropSizeInOriginal.roundToInt()
                )

                val data = ProfilePicture.fromBitmap(bitmap)
                SeesturmResult.Success(data)
            }
            catch (e: Exception) {
                SeesturmResult.Error(DataError.Local.UNKNOWN)
            }
            finally {
                withContext(Dispatchers.Main) {
                    isCropping = false
                }
            }
        }
    }
}

@Composable
fun rememberProfilePictureCropperState(
    image: PickedGalleryItem,
    viewDpSize: DpSize,
    maskWidthMultiplier: Float,
    maxMagnificationScale: Float
): ProfilePictureCropperState {

    val density = LocalDensity.current

    val viewSize = remember(viewDpSize, density) {
        with(density) {
            viewDpSize.toSize()
        }
    }
    val maskDiameter = remember(viewSize, maskWidthMultiplier) {
        min(
            viewSize.width * maskWidthMultiplier,
            viewSize.height * maskWidthMultiplier
        )
    }
    val imageAspectRatio = remember(image) {
        image.bitmap.aspectRatio
    }
    val imageSizeInView = remember(viewSize, imageAspectRatio) {
        viewSize.imageFitSize(imageAspectRatio)
    }
    val initialScale = remember(maskDiameter, imageSizeInView) {
        maskDiameter / min(imageSizeInView.width, imageSizeInView.height)
    }
    val maxScale = remember(maxMagnificationScale, initialScale) {
        max(maxMagnificationScale, initialScale)
    }

    return remember(maskDiameter, imageSizeInView, maxScale, initialScale) {
        ProfilePictureCropperState(
            maskDiameter = maskDiameter,
            imageSizeInView = imageSizeInView,
            maxMagnificationScale = maxScale,
            initialScale = initialScale
        )
    }
}