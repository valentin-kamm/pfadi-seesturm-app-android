package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.random.Random

fun Modifier.customLoadingBlinking(duration: Float = 750f): Modifier {
    val min = -200f
    val max = 200f
    val randomDuration = duration + Random.nextFloat() * (max - min) + min
    return composed {
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val animatedAlpha = infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (randomDuration).toInt(),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )
        this.graphicsLayer(alpha = animatedAlpha.value)
    }
}