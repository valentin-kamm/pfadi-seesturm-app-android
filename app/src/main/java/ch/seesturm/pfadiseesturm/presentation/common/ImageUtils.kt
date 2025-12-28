package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.scale
import kotlin.math.roundToInt

val ImageBitmap.aspectRatio: Float
    get() = this.width.toFloat() / this.height.toFloat()

fun ImageBitmap.shrink(largestSize: Float): ImageBitmap {

    if (this.width <= largestSize && this.height <= largestSize) {
        return  this
    }

    val aspectRatio = this.aspectRatio

    val targetSize: Size = if (aspectRatio > 1) {
        Size(width = largestSize, height = largestSize / aspectRatio)
    }
    else {
        Size(width = largestSize * aspectRatio, height = largestSize)
    }

    return this.asAndroidBitmap().scale(targetSize.width.roundToInt(), targetSize.height.roundToInt()).asImageBitmap()
}

fun Size.imageFitSize(imageAspectRatio: Float): Size {

    val viewAspectRatio = width / height

    return if (imageAspectRatio > viewAspectRatio) {
        val w = width
        val h = w / imageAspectRatio
        Size(w, h)
    }
    else {
        val h = height
        val w = h * imageAspectRatio
        Size(w, h)
    }
}