package ch.seesturm.pfadiseesturm.presentation.aktuell.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.components.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.theme.PfadiSeesturmTheme

@Composable
fun AktuellLoadingCell(
    onAppear: (() -> Unit)? = null,
    modifier: Modifier
) {
    LaunchedEffect(Unit) {
        if (onAppear != null) {
            onAppear()
        }
    }
    val cardAspectRatio: Float = 16.0f/10.0f
    CustomCardView(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer()
                    .aspectRatio(cardAspectRatio)
                    .customLoadingBlinking()
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                RedactedText(
                    3,
                    MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AktuellLoadingCellPreview() {
    PfadiSeesturmTheme {
        AktuellLoadingCell(
            modifier = Modifier
        )
    }
}