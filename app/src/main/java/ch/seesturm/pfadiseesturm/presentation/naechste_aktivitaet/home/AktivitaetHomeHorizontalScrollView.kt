package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventStartEndDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination
import ch.seesturm.pfadiseesturm.util.state.UiState

@Composable
fun AktivitaetHomeHorizontalScrollView(
    stufen: Set<SeesturmStufe>,
    naechsteAktivitaetState: Map<SeesturmStufe, UiState<GoogleCalendarEvent?>>,
    screenWidth: Dp,
    onRetry: (SeesturmStufe) -> Unit,
    homeNavController: NavController,
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
        contentPadding = PaddingValues(16.dp),
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
                            CardErrorView(
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
                                    homeNavController.navigate(
                                        AppDestination.MainTabView.Destinations.Home.Destinations.AktivitaetDetail(
                                            stufe = stufe,
                                            eventId = stufenState.data?.id
                                        )
                                    )
                                },
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
    AktivitaetHomeHorizontalScrollView(
        stufen = setOf(
            SeesturmStufe.Biber,
            SeesturmStufe.Wolf
        ),
        naechsteAktivitaetState = mapOf(
            SeesturmStufe.Biber to UiState.Success(
                data = GoogleCalendarEventDto(
                    id = "17v15laf167s75oq47elh17a3t",
                    summary = "Pfadistufenaktivität Pfadistufenaktivität",
                    description = "\n<p>Das Kantonale Pfaditreffen (KaTre) findet dieses Jahr am Wochenende vom <strong>21. und 22. September</strong> in <strong>Frauenfeld </strong>statt. Dieses Jahr steht das KaTre unter dem Motto &#171;<strong>Schräg ide Ziit</strong>&#187; und passend zum Motto werden wir nicht nur die Thurgauer Kantonshauptstadt besuchen, sondern auch eine spannende Reise in das Jahr 1999 unternehmen.</p>\n\n\n\n<p>Für die <strong>Pfadi- und Piostufe</strong> beginnt das Programm bereits am Samstagmittag und dauert bis Sonntagnachmittag, während es für die <strong>Wolfstufe</strong> und <strong>Biber</strong> am Sonntag startet. Wir würden uns sehr freuen, wenn sich möglichst viele Seestürmlerinnen und Seestürmler aller Stufen anmelden. Füllt dazu einfach das <a href=\"https: //seesturm.ch/wp-content/uploads/2024/06/KaTre1999_Anmeldetalon.pdf\">Anmeldeformular</a> aus und sendet es <strong>bis am 23. Juni</strong> an <a href=\"mailto: al@seesturm.ch\">al@seesturm.ch</a>.</p>\n",
                    location = "Pfadiheim",
                    created = "2022-08-28T15:25:45.726Z",
                    updated = "2022-08-28T15:25:45.726Z",
                    start = GoogleCalendarEventStartEndDto(
                        dateTime = "2022-08-27T06:00:00Z",
                        date = null
                    ),
                    end = GoogleCalendarEventStartEndDto(
                        dateTime = "2022-08-27T10:00:00Z",
                        date = null
                    )
                ).toGoogleCalendarEvent()
            ),
            SeesturmStufe.Wolf to UiState.Success(
                data = GoogleCalendarEventDto(
                    id = "17v15laf167s75oq47elh17a3t",
                    summary = "Wolfsstufenaktivität",
                    description = "\n<p>Das Kantonale Pfaditreffen (KaTre) findet dieses Jahr am Wochenende vom <strong>21. und 22. September</strong> in <strong>Frauenfeld </strong>statt. Dieses Jahr steht das KaTre unter dem Motto &#171;<strong>Schräg ide Ziit</strong>&#187; und passend zum Motto werden wir nicht nur die Thurgauer Kantonshauptstadt besuchen, sondern auch eine spannende Reise in das Jahr 1999 unternehmen.</p>\n\n\n\n<p>Für die <strong>Pfadi- und Piostufe</strong> beginnt das Programm bereits am Samstagmittag und dauert bis Sonntagnachmittag, während es für die <strong>Wolfstufe</strong> und <strong>Biber</strong> am Sonntag startet. Wir würden uns sehr freuen, wenn sich möglichst viele Seestürmlerinnen und Seestürmler aller Stufen anmelden. Füllt dazu einfach das <a href=\"https: //seesturm.ch/wp-content/uploads/2024/06/KaTre1999_Anmeldetalon.pdf\">Anmeldeformular</a> aus und sendet es <strong>bis am 23. Juni</strong> an <a href=\"mailto: al@seesturm.ch\">al@seesturm.ch</a>.</p>\n",
                    location = "Pfadiheim",
                    created = "2022-08-28T15:25:45.726Z",
                    updated = "2022-08-28T15:25:45.726Z",
                    start = GoogleCalendarEventStartEndDto(
                        dateTime = "2022-08-27T08:00:00Z",
                        date = null
                    ),
                    end = GoogleCalendarEventStartEndDto(
                        dateTime = "2022-08-27T12:00:00Z",
                        date = null
                    )
                ).toGoogleCalendarEvent()
            )
        ),
        screenWidth = 400.dp,
        onRetry = {},
        homeNavController = rememberNavController(),
        modifier = Modifier
            .fillMaxWidth()
    )
}