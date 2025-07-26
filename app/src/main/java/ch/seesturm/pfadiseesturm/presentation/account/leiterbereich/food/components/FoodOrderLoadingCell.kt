package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.food.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme

@Composable
fun FoodOrderLoadingCell(
    items: List<Int>,
    index: Int,
    modifier: Modifier = Modifier
) {
    FormItem(
        items = items,
        index = index,
        mainContent = FormItemContentType.Custom(
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
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
private fun FoodOrderLoadingCellPreview() {
    PfadiSeesturmTheme {
        FoodOrderLoadingCell(
            items = (0..<3).toList(),
            index = 0
        )
    }
}