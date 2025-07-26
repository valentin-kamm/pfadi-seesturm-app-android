package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import androidx.compose.foundation.layout.Arrangement
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
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun LeiterbereichTopHorizontalScrollView(
    foodState: UiState<List<FoodOrder>>,
    onNavigateToFood: () -> Unit,
    modifier: Modifier = Modifier
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        contentPadding = PaddingValues(horizontal = 16.dp),
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
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = Color.SEESTURM_RED,
                        modifier = Modifier
                            .size(20.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Essen bestellen",
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
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
                                    textStyle = MaterialTheme.typography.labelSmall,
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
                                        .size(12.dp)
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
                                    style = MaterialTheme.typography.labelSmall,
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

@Preview("Loading", showBackground = true)
@Composable
private fun LeiterbereichTopHorizontalScrollViewPreview1() {
    PfadiSeesturmTheme {
        LeiterbereichTopHorizontalScrollView(
            foodState = UiState.Loading,
            onNavigateToFood = {}
        )
    }
}
@Preview("Error", showBackground = true)
@Composable
private fun LeiterbereichTopHorizontalScrollViewPreview2() {
    PfadiSeesturmTheme {
        LeiterbereichTopHorizontalScrollView(
            foodState = UiState.Error("Schwerer Fehler"),
            onNavigateToFood = {}
        )
    }
}
@Preview("Success", showBackground = true)
@Composable
private fun LeiterbereichTopHorizontalScrollViewPreview3() {
    PfadiSeesturmTheme {
        LeiterbereichTopHorizontalScrollView(
            foodState = UiState.Success(DummyData.foodOrders),
            onNavigateToFood = {}
        )
    }
}