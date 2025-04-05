package ch.seesturm.pfadiseesturm.presentation.mehr.leitungsteam.components

import androidx.compose.foundation.background
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.components.customLoadingBlinking
import ch.seesturm.pfadiseesturm.util.Constants

@Composable
fun LeitungsteamLoadingCell(
    modifier: Modifier = Modifier
) {
    CustomCardView(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer()
                    .size(130.dp)
                    .customLoadingBlinking()
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .padding(vertical = 16.dp)
            ) {
                RedactedText(
                    numberOfLines = 1,
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                RedactedText(
                    numberOfLines = 2,
                    textStyle = MaterialTheme.typography.labelMedium,
                    lastLineFraction = 0.4f
                )
            }
        }
    }
}

@Preview
@Composable
fun LeitungsteamLoadingCellPreview() {
    LeitungsteamLoadingCell()
}