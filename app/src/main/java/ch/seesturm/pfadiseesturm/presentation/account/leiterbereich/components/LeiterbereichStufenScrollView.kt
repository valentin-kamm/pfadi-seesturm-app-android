package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

@Composable
fun LeiterbereichStufenScrollView(
    selectedStufen: Set<SeesturmStufe>,
    screenWidth: Dp,
    accountNavController: NavController,
    isDarkTheme: Boolean,
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
        contentPadding = PaddingValues(horizontal = 16.dp),
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
                                AppDestination.MainTabView.Destinations.Account.Destinations.NewAktivitaet(
                                    stufe = stufe
                                )
                            )
                        },
                        onNavigate = {
                            accountNavController.navigate(
                                AppDestination.MainTabView.Destinations.Account.Destinations.Stufenbereich(
                                    stufe = stufe
                                )
                            )
                        },
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeiterbereichStufenScrollViewPreview() {
    PfadiSeesturmTheme {
        BoxWithConstraints {
            val width = this.maxWidth
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LeiterbereichStufenScrollView(
                    selectedStufen = emptySet(),
                    screenWidth = width,
                    accountNavController = rememberNavController(),
                    isDarkTheme = false
                )
                LeiterbereichStufenScrollView(
                    selectedStufen = setOf(SeesturmStufe.Biber),
                    screenWidth = width,
                    accountNavController = rememberNavController(),
                    isDarkTheme = false
                )
                LeiterbereichStufenScrollView(
                    selectedStufen = setOf(SeesturmStufe.Biber, SeesturmStufe.Wolf),
                    screenWidth = width,
                    accountNavController = rememberNavController(),
                    isDarkTheme = false
                )
                LeiterbereichStufenScrollView(
                    selectedStufen = setOf(SeesturmStufe.Biber, SeesturmStufe.Wolf, SeesturmStufe.Pfadi, SeesturmStufe.Pio),
                    screenWidth = width,
                    accountNavController = rememberNavController(),
                    isDarkTheme = false
                )
            }
        }
    }
}
