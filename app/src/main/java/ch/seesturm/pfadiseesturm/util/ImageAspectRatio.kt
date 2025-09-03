package ch.seesturm.pfadiseesturm.util

import androidx.compose.ui.graphics.ImageBitmap

val ImageBitmap.aspectRatio: Float
    get() = this.width.toFloat() / this.height.toFloat()