package ch.seesturm.pfadiseesturm.presentation.mehr.fotos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.components.customLoadingBlinking

@Composable
fun PhotoGalleryLoadingCell(
    size: Dp,
    withText: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .width(size)
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer()
                .fillMaxWidth()
                .height(size)
                .clip(RoundedCornerShape(3.dp))
                .customLoadingBlinking()
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        )
        if (withText) {
            RedactedText(
                numberOfLines = 1,
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
fun PhotoGalleryLoadingCellPreview() {
    PhotoGalleryLoadingCell(
        size = 120.dp,
        withText = true
    )
}