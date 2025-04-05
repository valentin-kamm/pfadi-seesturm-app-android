package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.components.customLoadingBlinking

@Composable
fun StufenbereichAnAbmeldungLoadingCell(
    modifier: Modifier = Modifier
) {

    CustomCardView(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                RedactedText(
                    numberOfLines = 2,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    lastLineFraction = 0.75f,
                    modifier = Modifier
                        .weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .graphicsLayer()
                        .size(35.dp)
                        .customLoadingBlinking()
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .wrapContentSize()
                )
            }
            CustomCardView(
                backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shadowColor = Color.Transparent,
                modifier = Modifier
                    .height(30.dp)
                    .width(120.dp)
                    .graphicsLayer()
                    .customLoadingBlinking()
            ) {  }
            CustomCardView(
                backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shadowColor = Color.Transparent,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .graphicsLayer()
                    .customLoadingBlinking()
            ) {  }
        }
    }
}

@Preview
@Composable
private fun StufenbereichAnAbmeldungLoadingCellPreview() {
    StufenbereichAnAbmeldungLoadingCell(
        modifier = Modifier
            .fillMaxWidth()
    )
}