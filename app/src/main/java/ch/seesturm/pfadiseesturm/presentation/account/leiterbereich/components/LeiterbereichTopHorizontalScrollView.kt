package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Icon
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
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun LeiterbereichTopHorizontalScrollView(
    onNavigateToFood: () -> Unit,
    foodState: UiState<List<FoodOrder>>,
    modifier: Modifier = Modifier
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        item(
            key = "LeiterbereichTopHorizontalScrollViewEssenBestellen"
        ) {
            CustomCardView(
                onClick = onNavigateToFood
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = Color.SEESTURM_RED
                    )
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Essen bestellen",
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        when (foodState) {
                            UiState.Loading -> {
                                RedactedText(
                                    numberOfLines = 1,
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .width(75.dp)
                                )
                            }

                            is UiState.Error -> {
                                Icon(
                                    imageVector = Icons.Outlined.Error,
                                    contentDescription = null,
                                    tint = Color.SEESTURM_RED,
                                    modifier = Modifier
                                        .size(15.dp)
                                )
                            }

                            is UiState.Success -> {
                                val sum = foodState.data.sumOf { it.totalCount }
                                val orderString = when (sum) {
                                    1 -> { "$sum Bestellung"}
                                    else -> { "$sum Bestellungen"}
                                }
                                Text(
                                    text = orderString,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodySmall,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .wrapContentWidth()
                            .alpha(0.4f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LeiterbereichTopHorizontalScrollViewPreview() {
    LeiterbereichTopHorizontalScrollView(
        onNavigateToFood = {},
        foodState = UiState.Success(
            data = listOf(
                FoodOrder(
                    id = "123123",
                    itemDescription = "Dürüm",
                    totalCount = 0,
                    userIds = listOf(""),
                    users = listOf(null),
                    ordersString = ""
                )
            )
        )
    )
}