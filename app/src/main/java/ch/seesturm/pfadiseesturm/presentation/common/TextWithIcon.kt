package ch.seesturm.pfadiseesturm.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN

@Composable
fun TextWithIcon(
    type: TextWithIconType,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    iconTint: Color = MaterialTheme.colorScheme.onBackground,
    contentDescription: String? = null,
    maxLines: Int = Int.MAX_VALUE,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, horizontalAlignment),
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier
                .size(
                    when (type) {
                        is TextWithIconType.AnnotatedString -> {
                            type.iconSize
                        }
                        is TextWithIconType.Text -> {
                            with(LocalDensity.current) {
                                type.textStyle().lineHeight.toPx().toDp()
                            }
                        }
                    }
                )
        )
        when (type) {
            is TextWithIconType.AnnotatedString -> {
                Text(
                    text = type.annotatedString,
                    color = textColor,
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .background(Color.Transparent)
                )
            }
            is TextWithIconType.Text -> {
                Text(
                    text = type.text,
                    style = type.textStyle(),
                    color = textColor,
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .background(Color.Transparent)
                )
            }
        }
    }
}

sealed class TextWithIconType {
    data class Text(
        val text: String,
        val textStyle: @Composable () -> TextStyle = { MaterialTheme.typography.bodySmall }
    ): TextWithIconType()
    data class AnnotatedString(
        val annotatedString: androidx.compose.ui.text.AnnotatedString,
        val iconSize: Dp
    ): TextWithIconType()
}

@Preview(showBackground = true)
@Composable
private fun TextWithIconPreview1() {
    PfadiSeesturmTheme {
        TextWithIcon(
            type = TextWithIconType.Text("Test"),
            imageVector = Icons.Default.Terrain,
            iconTint = Color.SEESTURM_GREEN,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}