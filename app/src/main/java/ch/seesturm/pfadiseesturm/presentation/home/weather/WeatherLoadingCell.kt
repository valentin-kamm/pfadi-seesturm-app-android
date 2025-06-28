package ch.seesturm.pfadiseesturm.presentation.home.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme

@Composable
fun WeatherLoadingCell(
    modifier: Modifier = Modifier
) {

    CustomCardView(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            RedactedText(
                numberOfLines = 1,
                textStyle = MaterialTheme.typography.titleLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .graphicsLayer()
                        .customLoadingBlinking()
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .graphicsLayer()
                        .customLoadingBlinking()
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            CustomCardView(
                backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shadowColor = Color.Transparent,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .graphicsLayer()
                    .customLoadingBlinking()
            ) {}
        }
    }
}

@Preview
@Composable
private fun WeatherLoadingCellPreview() {
    PfadiSeesturmTheme {
        WeatherLoadingCell()
    }
}