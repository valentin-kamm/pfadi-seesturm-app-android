package ch.seesturm.pfadiseesturm.presentation.common.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun RedactedText(
    numberOfLines: Int,
    textStyle: TextStyle,
    lastLineFraction: Float = 1f,
    modifier: Modifier = Modifier
) {
    val boxHeight: Dp = with(LocalDensity.current) {
        textStyle.fontSize.toPx().toDp()
    }
    val boxSpacing: Dp = with(LocalDensity.current) {
        textStyle.lineHeight.toPx().toDp()
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(boxSpacing - boxHeight),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .graphicsLayer()
            .customLoadingBlinking()
    ) {
        repeat(numberOfLines) { index ->
            Box(
                modifier = Modifier
                    .height(boxHeight)
                    .fillMaxWidth(if (index == numberOfLines - 1) lastLineFraction else 1f)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RedactedTextPreview() {
    RedactedText(3, MaterialTheme.typography.headlineMedium, 0.4f)
}

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