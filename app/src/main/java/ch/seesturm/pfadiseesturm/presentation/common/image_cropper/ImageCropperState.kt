package ch.seesturm.pfadiseesturm.presentation.common.image_cropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class ImageCropperState(
    val isCropping: Boolean = false,
    val scale: Float,
    val offset: Offset = Offset.Zero,
    val maskSize: Size
) {
    companion object {
        fun create(initialScale: Float, maskDiameter: Float): ImageCropperState {
            return ImageCropperState(
                scale = initialScale,
                maskSize = Size(width = maskDiameter, height = maskDiameter)
            )
        }
    }
}