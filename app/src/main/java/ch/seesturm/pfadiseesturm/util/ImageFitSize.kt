package ch.seesturm.pfadiseesturm.util

import androidx.compose.ui.geometry.Size

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