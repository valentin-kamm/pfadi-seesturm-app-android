package ch.seesturm.pfadiseesturm.presentation.mehr.documents.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme

@Composable
fun DocumentLoadingCardView(
    modifier: Modifier = Modifier
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer()
                .width(75.dp)
                .aspectRatio(212.toFloat()/300.toFloat())
                .customLoadingBlinking()
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(1f)
        ) {
            RedactedText(
                2,
                MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            RedactedText(
                1,
                MaterialTheme.typography.labelSmall,
                lastLineFraction = 0.7f
            )
        }
    }
}

@Preview
@Composable
fun DokumenteLuuchtturmLoadingCellPreview() {
    PfadiSeesturmTheme {
        DocumentLoadingCardView()
    }
}