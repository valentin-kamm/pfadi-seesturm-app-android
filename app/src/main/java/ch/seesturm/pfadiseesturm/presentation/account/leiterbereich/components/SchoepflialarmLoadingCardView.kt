package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType

@Composable
fun SchoepflialarmLoadingCardView(
    modifier: Modifier = Modifier
) {
    CustomCardView(
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .graphicsLayer()
                        .clip(CircleShape)
                        .customLoadingBlinking()
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .wrapContentSize()
                )
                RedactedText(
                    numberOfLines = 1,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    lastLineFraction = 0.66f,
                    modifier = Modifier
                        .weight(1f)
                )
            }
            RedactedText(
                numberOfLines = 2,
                textStyle = MaterialTheme.typography.bodyMedium,
                lastLineFraction = 0.8f,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SchoepflialarmReactionType.entries.forEach { _ ->
                    CustomCardView(
                        backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shadowColor = Color.Transparent,
                        modifier = Modifier
                            .height(36.dp)
                            .weight(1f)
                            .graphicsLayer()
                            .customLoadingBlinking()
                    ) {  }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SchoepflialarmLoadingCardViewPreview() {
    PfadiSeesturmTheme {
        SchoepflialarmLoadingCardView()
    }
}