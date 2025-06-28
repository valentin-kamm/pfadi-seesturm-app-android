package ch.seesturm.pfadiseesturm.presentation.common.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme

@Composable
fun BasicLoadingStickHeader(
    modifier: Modifier = Modifier,
    lastLineFraction: Float = 0.4f
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RedactedText(
            1,
            MaterialTheme.typography.bodyLarge,
            lastLineFraction = lastLineFraction,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
        )
    }
}

@Preview
@Composable
private fun BasicLoadingStickHeaderPreview() {
    PfadiSeesturmTheme {
        BasicLoadingStickHeader()
    }
}