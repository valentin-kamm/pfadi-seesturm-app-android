package ch.seesturm.pfadiseesturm.presentation.account.food.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.firestore.model.FoodOrder
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED

@Composable
fun EssenBestellungCell(
    order: FoodOrder,
    orders: List<FoodOrder>,
    index: Int,
    userId: String,
    onDeleteButtonClick: () -> Unit,
    onAddButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FormItem(
        items = orders,
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
                    Text(
                        text = "${order.totalCount}\u00D7",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.SEESTURM_GREEN,
                        maxLines = 1,
                        modifier = Modifier
                            .widthIn(min = 65.dp)
                            .wrapContentWidth()
                    )
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = order.itemDescription,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Text(
                            text = order.ordersString,
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(0.4f)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .widthIn(min = 67.dp)
                    ) {
                        if (order.userIds.contains(userId)) {
                            SeesturmButton(
                                type = SeesturmButtonType.IconButton(
                                    buttonColor = Color.SEESTURM_RED,
                                    contentColor = Color.White,
                                    icon = SeesturmButtonIconType.Predefined(
                                        icon = Icons.Default.Remove
                                    )
                                ),
                                title = null,
                                onClick = onDeleteButtonClick,
                                modifier = Modifier
                                    .size(25.dp)
                            )
                        }
                        SeesturmButton(
                            type = SeesturmButtonType.IconButton(
                                buttonColor = Color.SEESTURM_GREEN,
                                contentColor = Color.White,
                                icon = SeesturmButtonIconType.Predefined(
                                    icon = Icons.Default.Add
                                )
                            ),
                            title = null,
                            onClick = onAddButtonClick,
                            modifier = Modifier
                                .size(25.dp)
                        )
                    }
                }
            }
        ),
        modifier = modifier
            .fillMaxWidth()
    )
}

@Preview
@Composable
private fun EssenBestellungCellPreview() {
    EssenBestellungCell(
        order = FoodOrder(
            id = "123",
            itemDescription = "Dürüm ohne Zwieble",
            totalCount = 99,
            userIds = listOf("123", "456", "789"),
            users = listOf(null),
            ordersString = "Hallo (2x), Hoi (1x)"
        ),
        userId = "123",
        onAddButtonClick = {},
        onDeleteButtonClick = {},
        orders = listOf(
            FoodOrder(
                id = "123",
                itemDescription = "Dürüm ohne Zwieble",
                totalCount = 99,
                userIds = listOf("123", "456", "789"),
                users = listOf(null),
                ordersString = "Hallo (2x), Hoi (1x)"
            )
        ),
        index = 0
    )
}