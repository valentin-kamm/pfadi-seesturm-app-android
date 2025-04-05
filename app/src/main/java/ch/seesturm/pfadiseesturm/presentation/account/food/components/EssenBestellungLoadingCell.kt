package ch.seesturm.pfadiseesturm.presentation.account.food.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.components.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED

@Composable
fun EssenBestellungLoadingCell(
    items: List<Int>,
    index: Int,
    modifier: Modifier = Modifier
) {
    FormItem(
        items = items,
        index = index,
        mainContent = FormItemContentType.Custom(
            contentPadding = PaddingValues(16.dp),
            content = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    RedactedText(
                        numberOfLines = 1,
                        textStyle = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .width(65.dp)
                            .wrapContentWidth()
                    )
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        RedactedText(
                            numberOfLines = 1,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            lastLineFraction = 0.65f
                        )
                        RedactedText(
                            numberOfLines = 1,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            lastLineFraction = 0.85f
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .graphicsLayer()
                            .size(25.dp)
                            .customLoadingBlinking()
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            .wrapContentSize()
                    )
                }
            }
        ),
        modifier = modifier
            .fillMaxWidth()
    )
}

@Preview
@Composable
private fun EssenBestellungLoadingCellPreview() {
    EssenBestellungLoadingCell(
        items = (0..<3).toList(),
        index = 0
    )
}