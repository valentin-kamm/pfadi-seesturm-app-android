package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme

@Composable
fun RedactedText(
    numberOfLines: Int,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    lastLineFraction: Float = 1f
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
                    .fillMaxWidth(
                        if (index == numberOfLines - 1) {
                            lastLineFraction
                        }
                        else {
                            1f
                        }
                    )
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RedactedTextPreview() {
    PfadiSeesturmTheme {
        RedactedText(
            numberOfLines = 3,
            textStyle = MaterialTheme.typography.headlineMedium,
            lastLineFraction = 0.4f,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}