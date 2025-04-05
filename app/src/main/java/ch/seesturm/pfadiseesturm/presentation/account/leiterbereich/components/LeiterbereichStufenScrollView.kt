package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination

@Composable
fun LeiterbereichStufenScrollView(
    selectedStufen: Set<SeesturmStufe>,
    screenWidth: Dp,
    accountNavController: NavController,
    modifier: Modifier = Modifier
) {

    val cardWidth = when (selectedStufen.size) {
        0, 1 -> {
            screenWidth - 32.dp
        }
        2 -> {
            (screenWidth - 48.dp) / 2
        }
        else -> {
            0.85 * (screenWidth - 48.dp) / 2
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (selectedStufen.isEmpty()) {
            item(
                key = "LeiterbereichKeineStufenCell"
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(min = cardWidth)
                        .height(175.dp)
                        .animateItem()
                        .padding()
                ) {
                    Text(
                        text = "Keine Stufe ausgewählt",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.4f)
                    )
                }
            }
        }
        else {
            selectedStufen.sortedBy { it.id }.forEach { stufe ->
                item(
                    key = "LeiterbereichStufenCell${stufe.id}"
                ) {
                    LeiterbereichStufeCardView(
                        cardWidth = cardWidth,
                        stufe = stufe,
                        onButtonClick = {
                            accountNavController.navigate(
                                AppDestination.MainTabView.Destinations.Account.Destinations.Stufenbereich(
                                    stufe = stufe,
                                    openSheetUponNavigation = true
                                )
                            )
                        },
                        onNavigate = {
                            accountNavController.navigate(
                                AppDestination.MainTabView.Destinations.Account.Destinations.Stufenbereich(
                                    stufe = stufe,
                                    openSheetUponNavigation = false
                                )
                            )
                        },
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LeiterbereichStufenScrollViewPreview() {
    LeiterbereichStufenScrollView(
        selectedStufen = setOf(
            SeesturmStufe.Pfadi,
            //SeesturmStufe.Wolf,
            //SeesturmStufe.Pio
        ),
        screenWidth = 350.dp,
        accountNavController = rememberNavController()
    )
}