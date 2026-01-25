package ch.seesturm.pfadiseesturm.presentation.common.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme

@Composable
fun BasicListHeader(
    mode: BasicListHeaderMode,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        when (mode) {
            BasicListHeaderMode.Loading -> {
                RedactedText(
                    numberOfLines = 1,
                    textStyle = MaterialTheme.typography.bodySmall,
                    lastLineFraction = 0.45f,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
            is BasicListHeaderMode.Normal -> {
                Text(
                    mode.text.uppercase(),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .alpha(0.4f)
                )
            }
        }
    }
}

sealed class BasicListHeaderMode {
    data class Normal(
        val text: String
    ): BasicListHeaderMode()
    data object Loading: BasicListHeaderMode()
}

@Preview(showBackground = true)
@Composable
private fun BasicListHeaderPreview1() {
    PfadiSeesturmTheme {
        BasicListHeader(mode = BasicListHeaderMode.Normal("Pfadijahr 2025"))
    }
}
@Preview(showBackground = true)
@Composable
private fun BasicListHeaderPreview2() {
    PfadiSeesturmTheme {
        BasicListHeader(BasicListHeaderMode.Loading)
    }
}