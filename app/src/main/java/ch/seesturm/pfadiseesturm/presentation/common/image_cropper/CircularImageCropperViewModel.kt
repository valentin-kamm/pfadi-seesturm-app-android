package ch.seesturm.pfadiseesturm.presentation.common.image_cropper

import android.graphics.Bitmap
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import ch.seesturm.pfadiseesturm.domain.storage.model.JPGData
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlin.math.max

class CircularImageCropperViewModel(
    initialScale: Float,
    private val maskDiameter: Float,
    private val imageSizeInView: Size,
    private val maxMagnificationScale: Float
): ViewModel() {

    private val _state = MutableStateFlow(
        ImageCropperState.create(
            initialScale = initialScale,
            maskDiameter = maskDiameter
        )
    )
    val state = _state.asStateFlow()

    fun onTransform(pan: Offset, zoom: Float) {

        val minScale = calculateMinScale()

        _state.update {
            val newScale = (it.scale * zoom).coerceIn(minScale, maxMagnificationScale)
            it.copy(
                scale = newScale,
                offset = constrainOffset(state.value.offset + pan * newScale)
            )
        }
    }

    private fun constrainOffset(newOffset: Offset): Offset {

        val maxOffset = calculateMaxOffset()
        return Offset(
            x = newOffset.x.coerceIn(-maxOffset.x, maxOffset.x),
            y = newOffset.y.coerceIn(-maxOffset.y, maxOffset.y)
        )
    }

    private fun calculateMinScale(): Float =
        max(
            maskDiameter / imageSizeInView.width,
            maskDiameter / imageSizeInView.height
        )

    private fun calculateMaxOffset(): Offset {

        val xLimit = max(
            0f,
            imageSizeInView.width / 2 * state.value.scale - maskDiameter / 2
        )
        val yLimit = max(
            0f,
            imageSizeInView.height / 2 * state.value.scale - maskDiameter / 2
        )
        return Offset(xLimit, yLimit)
    }

    suspend fun cropImage(image: ImageBitmap): SeesturmResult<JPGData, DataError.Local> {

        _state.update {
            it.copy(
                isCropping = true
            )
        }

        return try {
            withContext(Dispatchers.Default) {

                val cropRect = calculateCropRect(image)

                val croppedBitmap = Bitmap.createBitmap(
                    image.asAndroidBitmap(),
                    cropRect.left.toInt(),
                    cropRect.top.toInt(),
                    cropRect.width().toInt(),
                    cropRect.height().toInt()
                )

                _state.update {
                    it.copy(
                        isCropping = false
                    )
                }

                SeesturmResult.Success(JPGData.fromBitmap(croppedBitmap))
            }
        }
        catch (e: Exception) {

            _state.update {
                it.copy(
                    isCropping = false
                )
            }

            SeesturmResult.Error(DataError.Local.UNKNOWN)
        }
    }

    private fun calculateCropRect(image: ImageBitmap): RectF {

        val factor = image.width / imageSizeInView.width

        val cropSizeInOriginalImage = maskDiameter / state.value.scale * factor

        val offsetXInPixels = state.value.offset.x / state.value.scale * factor
        val offsetYInPixels = state.value.offset.y / state.value.scale * factor

        val cropRectLeft = image.width / 2f - cropSizeInOriginalImage / 2f - offsetXInPixels
        val cropRectTop = image.height / 2f - cropSizeInOriginalImage / 2f - offsetYInPixels

        return RectF(
            cropRectLeft,
            cropRectTop,
            cropRectLeft + cropSizeInOriginalImage,
            cropRectTop + cropSizeInOriginalImage
        )
    }
}