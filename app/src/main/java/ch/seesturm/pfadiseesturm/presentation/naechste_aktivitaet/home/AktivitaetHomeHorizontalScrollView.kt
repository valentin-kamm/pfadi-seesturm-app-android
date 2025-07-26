package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.ErrorCardView
import ch.seesturm.pfadiseesturm.presentation.common.navigation.AppDestination
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun AktivitaetHomeHorizontalScrollView(
    stufen: Set<SeesturmStufe>,
    naechsteAktivitaetState: Map<SeesturmStufe, UiState<GoogleCalendarEvent?>>,
    screenWidth: Dp,
    onRetry: (SeesturmStufe) -> Unit,
    onNavigate: (AppDestination.MainTabView.Destinations.Home.Destinations) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {

    val cardWidth = when (stufen.size) {
        1 -> screenWidth - 32.dp
        0 -> 0.dp
        else -> (screenWidth - 32.dp) * 0.85f
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        stufen.sortedBy { it.id }.forEach { stufe ->
            val stufenState = naechsteAktivitaetState[stufe]
            if (stufenState != null) {
                when (stufenState) {
                    UiState.Loading -> {
                        item(
                            key = "NaechsteAktivitaetHomeLoadingCellStufe${stufe.id}"
                        ) {
                            AktivitaetHomeLoadingView(
                                modifier = Modifier
                                    .width(cardWidth)
                                    .animateItem()
                            )
                        }
                    }
                    is UiState.Error -> {
                        item(
                            key = "NaechsteAktivitaetHomeErrorCellStufe${stufe.id}"
                        ) {
                            ErrorCardView(
                                errorTitle = "Ein Fehler ist aufgetreten",
                                errorDescription = stufenState.message,
                                retryAction = {
                                    onRetry(stufe)
                                },
                                modifier = Modifier
                                    .width(cardWidth)
                                    .animateItem()
                            )
                        }
                    }
                    is UiState.Success -> {
                        item(
                            key = "NaechsteAktivitaetHomeCellStufe${stufe.id}"
                        ) {
                            AktivitaetHomeCardView(
                                aktivitaet = stufenState.data,
                                stufe = stufe,
                                onClick = {
                                    onNavigate(
                                        AppDestination.MainTabView.Destinations.Home.Destinations.AktivitaetDetail(
                                            stufe = stufe,
                                            eventId = stufenState.data?.id
                                        )
                                    )
                                },
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier
                                    .width(cardWidth)
                                    .animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AktivitaetHomeHorizontalScrollViewPreview() {
    PfadiSeesturmTheme {
        AktivitaetHomeHorizontalScrollView(
            stufen = setOf(
                SeesturmStufe.Biber,
                SeesturmStufe.Wolf,
                SeesturmStufe.Pfadi,
                SeesturmStufe.Pio
            ),
            naechsteAktivitaetState = mapOf(
                SeesturmStufe.Biber to UiState.Loading,
                SeesturmStufe.Wolf to UiState.Error("Schwerer Fehler"),
                SeesturmStufe.Pfadi to UiState.Success(null),
                SeesturmStufe.Pio to UiState.Success(DummyData.aktivitaet2)
            ),
            screenWidth = 400.dp,
            onRetry = {},
            onNavigate = {},
            isDarkTheme = false,
            modifier = Modifier
        )
    }
}