package ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.components.customLoadingBlinking

@Composable
fun AnlassLoadingCardView(
    onAppear: (() -> Unit)? = null,
    modifier: Modifier
) {
    LaunchedEffect(Unit) {
        if (onAppear != null) {
            onAppear()
        }
    }
    CustomCardView(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CustomCardView(
                shadowColor = Color.Transparent,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .width(110.dp)
                    .height(85.dp)
                    .graphicsLayer()
                    .customLoadingBlinking()
            ) { }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start
            ) {
                RedactedText(
                    numberOfLines = 2,
                    textStyle = MaterialTheme.typography.titleLarge,
                    lastLineFraction = 1.0f,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                RedactedText(
                    numberOfLines = 1,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    lastLineFraction = 0.8f,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun AnlassLoadingCardViewPreview() {
    AnlassLoadingCardView(
        null,
        Modifier
    )
}