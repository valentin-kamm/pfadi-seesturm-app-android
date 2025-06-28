package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun AktivitaetDetailLoadingCardView(
    modifier: Modifier = Modifier
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
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                RedactedText(
                    numberOfLines = 1,
                    textStyle = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    lastLineFraction = 0.8f,
                    modifier = Modifier
                        .weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer()
                        .clip(CircleShape)
                        .customLoadingBlinking()
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .wrapContentSize()
                )
            }
            RedactedText(
                numberOfLines = 6,
                lastLineFraction = 0.33f,
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
private fun AktivitaetDetailLoadingCardViewPreview() {
    PfadiSeesturmTheme {
        AktivitaetDetailLoadingCardView()
    }
}