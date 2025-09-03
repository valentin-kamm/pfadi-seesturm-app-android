package ch.seesturm.pfadiseesturm.util

import androidx.compose.ui.geometry.Size

fun Size.imageFitSize(imageAspectRatio: Float): Size {

    val viewAspectRatio = this.width / this.height

    val imageSize = if (imageAspectRatio > viewAspectRatio) {
        // image is wider than the view
        val width = this.width
        val height = width / imageAspectRatio
        Size(width = width, height = height)
    }
    else {
        // image is taller or equal to the view
        val height = this.height
        val width = height * imageAspectRatio
        Size(width = width, height = height)
    }

    return imageSize
}