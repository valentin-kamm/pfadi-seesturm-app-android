package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme

@Composable
fun AktivitaetHomeLoadingView(
    modifier: Modifier
) {
    CustomCardView(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RedactedText(
                    numberOfLines = 1,
                    textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .graphicsLayer()
                        .size(40.dp)
                        .customLoadingBlinking()
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .wrapContentSize()
                )
            }
            RedactedText(
                numberOfLines = 2,
                textStyle = MaterialTheme.typography.bodyMedium,
                lastLineFraction = 0.75f
            )
        }
    }
}

@Preview
@Composable
private fun AktivitaetHomeLoadingViewPreview() {
    PfadiSeesturmTheme {
        AktivitaetHomeLoadingView(
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}