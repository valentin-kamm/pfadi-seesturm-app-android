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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
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
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonStyle
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventManagementMode
import ch.seesturm.pfadiseesturm.presentation.common.event_management.types.EventToManageType
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

@Composable
fun LeiterbereichStufenScrollView(
    stufen: Set<SeesturmStufe>,
    totalContentWidth: Dp,
    accountNavController: NavController,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    
    val scrollViewItemWidth = when (stufen.size) {
        0, 1 -> totalContentWidth - 32.dp
        2 -> (totalContentWidth - 48.dp) / 2
        else -> 0.9 * (totalContentWidth - 48.dp) / 2
    }

    CustomCardView(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top,
                contentPadding = PaddingValues(16.dp),
                userScrollEnabled = stufen.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (stufen.isEmpty()) {
                    item(
                        key = "LeiterbereichKeineStufenCell"
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(min = scrollViewItemWidth)
                                .height(155.dp)
                                .animateItem()
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
                    stufen.sortedBy { it.id }.forEach { stufe ->
                        item(
                            key = "LeiterbereichStufenCell${stufe.id}"
                        ) {
                            LeiterbereichStufeCardView(
                                cardWidth = scrollViewItemWidth,
                                stufe = stufe,
                                onButtonClick = {
                                    accountNavController.navigate(
                                        AppDestination.MainTabView.Destinations.Account.Destinations.ManageEvent(
                                            type = EventToManageType.Aktivitaet(
                                                stufe = stufe,
                                                mode = EventManagementMode.Insert
                                            )
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
            SeesturmButton(
                type = SeesturmButtonType.Secondary,
                onClick = {
                    accountNavController.navigate(
                        AppDestination.MainTabView.Destinations.Account.Destinations.ManageEvent(
                            type = EventToManageType.MultipleAktivitaeten
                        )
                    )
                },
                title = "Aktivität für mehrere Stufen",
                icon = SeesturmButtonIconType.Predefined(
                    icon = Icons.Default.Add
                ),
                colors = SeesturmButtonColor.Custom(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    buttonColor = MaterialTheme.colorScheme.onBackground
                ),
                style = SeesturmButtonStyle.Outlined,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeiterbereichStufenScrollViewPreview() {
    PfadiSeesturmTheme {
        BoxWithConstraints {
            val width = this.maxWidth - 32.dp
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    LeiterbereichStufenScrollView(
                        stufen = emptySet(),
                        totalContentWidth = width,
                        accountNavController = rememberNavController(),
                        isDarkTheme = false
                    )
                }
                item {
                    LeiterbereichStufenScrollView(
                        stufen = setOf(SeesturmStufe.Biber),
                        totalContentWidth = width,
                        accountNavController = rememberNavController(),
                        isDarkTheme = false
                    )
                }
                item {
                    LeiterbereichStufenScrollView(
                        stufen = setOf(SeesturmStufe.Biber, SeesturmStufe.Wolf),
                        totalContentWidth = width,
                        accountNavController = rememberNavController(),
                        isDarkTheme = false
                    )
                }
                item {
                    LeiterbereichStufenScrollView(
                        stufen = setOf(
                            SeesturmStufe.Biber,
                            SeesturmStufe.Wolf,
                            SeesturmStufe.Pfadi,
                            SeesturmStufe.Pio
                        ),
                        totalContentWidth = width,
                        accountNavController = rememberNavController(),
                        isDarkTheme = false
                    )
                }
            }
        }
    }
}
