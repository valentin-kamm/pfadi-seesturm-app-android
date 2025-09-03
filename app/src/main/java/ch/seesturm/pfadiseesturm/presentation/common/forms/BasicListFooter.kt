package ch.seesturm.pfadiseesturm.presentation.common.forms

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
fun BasicListFooter(
    mode: BasicListHeaderMode,
    modifier: Modifier = Modifier,
    maxLines: Int = 1
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        when (mode) {
            BasicListHeaderMode.Loading -> {
                RedactedText(
                    numberOfLines = maxLines,
                    textStyle = MaterialTheme.typography.bodySmall,
                    lastLineFraction = 0.66f,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
            is BasicListHeaderMode.Normal -> {
                Text(
                    mode.text,
                    maxLines = maxLines,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .alpha(0.4f)
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun BasicListFooterPreview1() {
    PfadiSeesturmTheme {
        BasicListFooter(
            mode = BasicListHeaderMode.Normal("Hallo wie geht es dir?")
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun BasicListFooterPreview2() {
    PfadiSeesturmTheme {
        BasicListFooter(
            mode = BasicListHeaderMode.Loading
        )
    }
}