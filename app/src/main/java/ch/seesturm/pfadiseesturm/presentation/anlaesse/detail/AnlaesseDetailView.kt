package ch.seesturm.pfadiseesturm.presentation.anlaesse.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar
import ch.seesturm.pfadiseesturm.util.Constants
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.state.UiState
import ch.seesturm.pfadiseesturm.util.TopBarStyle
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventStartEndDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.data.wordpress.repository.AnlaesseRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AnlaesseService
import ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components.CalendarSubscriptionAlert
import ch.seesturm.pfadiseesturm.presentation.common.TopBarScaffold
import ch.seesturm.pfadiseesturm.presentation.common.components.CardErrorView
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.intersectWith
import ch.seesturm.pfadiseesturm.presentation.common.viewModelFactoryHelper
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.subscribeToCalendar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun AnlaesseDetailView(
    bottomNavigationInnerPadding: PaddingValues,
    navController: NavController,
    viewModel: AnlaesseDetailViewModel,
    calendar: SeesturmCalendar,
    columnState: LazyListState = rememberLazyListState()
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CalendarSubscriptionAlert (
        isShown = uiState.showCalendarSubscriptionAlert,
        title = "Kalender kann nicht abonniert werden",
        calendar = calendar,
        onDismiss = {
            viewModel.updateAlertVisibility(false)
        }
    )

    TopBarScaffold(
        topBarStyle = TopBarStyle.Small,
        backNavigationAction = {
            navController.popBackStack()
        },
        actions = {
            IconButton(
                onClick = {
                    when (
                        subscribeToCalendar(
                            subscriptionUrl = calendar.subscriptionUrl,
                            context = context
                        )
                    ) {
                        is SeesturmResult.Error -> {
                            viewModel.updateAlertVisibility(true)
                        }
                        is SeesturmResult.Success -> {
                            // do nothing
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN
                )
            }
        }
    ) { topBarInnerPadding ->

        val combinedPadding =
            bottomNavigationInnerPadding.intersectWith(topBarInnerPadding, LayoutDirection.Ltr)

        LazyColumn(
            state = columnState,
            userScrollEnabled = !uiState.eventState.scrollingDisabled,
            contentPadding = combinedPadding,
            modifier = Modifier
                .fillMaxSize()
        ) {

            when (val localState = uiState.eventState) {
                UiState.Loading -> {
                    item(
                        key = "AnlaesseDetailLoadingView"
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            RedactedText(
                                2,
                                MaterialTheme.typography.displaySmall,
                                lastLineFraction = 0.4f
                            )
                            RedactedText(
                                10,
                                MaterialTheme.typography.bodyLarge,
                                lastLineFraction = 0.75f
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    item(
                        key = "AnlaesseDetailErrorView"
                    ) {
                        CardErrorView(
                            modifier = Modifier
                                .padding(16.dp)
                                .animateItem(),
                            errorTitle = "Ein Fehler ist aufgetreten",
                            errorDescription = localState.message
                        ) {
                            viewModel.getEvent()
                        }
                    }
                }
                is UiState.Success -> {
                    item(
                        key = "AnlaesseDetailContentView"
                    ) {
                        AnlaesseDetailContentView(
                            event = localState.data,
                            calendar = calendar
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun AnlaesseDetailContentView(
    event: GoogleCalendarEvent,
    calendar: SeesturmCalendar
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            event.title,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
        )
        if (calendar.isLeitungsteam) {
            CustomCardView(
                shadowColor = Color.Transparent,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Termin Leitungsteam",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    color = Color.SEESTURM_RED,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Icon(
                Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN,
                modifier = Modifier
                    .size(24.dp)
            )
            Text(
                text = event.fullDateTimeFormatted,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .alpha(0.4f)
                    .fillMaxWidth()
            )
        }
        if (event.location != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN,
                    modifier = Modifier
                        .size(24.dp)
                )
                Text(
                    text = event.location,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .alpha(0.4f)
                        .fillMaxWidth()
                )
            }
        }
        if (event.description != null) {
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}


@Preview
@Composable
fun AnlaesseDetailViewPreview() {
    AnlaesseDetailView(
        bottomNavigationInnerPadding = PaddingValues(0.dp),
        navController = rememberNavController(),
        calendar = SeesturmCalendar.TERMINE,
        viewModel = viewModel<AnlaesseDetailViewModel>(
            factory = viewModelFactoryHelper {
                AnlaesseDetailViewModel(
                    calendar = SeesturmCalendar.TERMINE,
                    eventId = "1p5bqoco2c1nhejhv6h0jn72mk",
                    service = AnlaesseService(
                        AnlaesseRepositoryImpl(
                            Retrofit.Builder()
                                .baseUrl(Constants.SEESTURM_API_BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                                .create(WordpressApi::class.java)
                        )
                    ),
                    cacheIdentifier = MemoryCacheIdentifier.List
                )
            }
        )
    )
}

@Preview("Eintägiger Anlass", showBackground = true)
@Composable
fun AnlaesseDetailContentViewPreview() {
    AnlaesseDetailContentView(
        event = GoogleCalendarEventDto(
            id = "049i70bbetjb6j9nqi9in866bl",
            summary = "Waldweihnachten \uD83C\uDF84",
            description = "Die traditionelle Waldweihnacht der Pfadi Seesturm kann dieses Jahr hoffentlich wieder in gewohnter Form stattfinden. Die genauen Zeiten werden später kommuniziert.",
            location = "im Wald",
            created = "2022-08-28T15:34:26.000Z",
            updated = "2022-08-28T15:34:26.247Z",
            start = GoogleCalendarEventStartEndDto(
                dateTime = "2022-12-17T15:00:00Z",
                date = null
            ),
            end = GoogleCalendarEventStartEndDto(
                dateTime = "2022-12-17T18:00:00Z",
                date = null
            )
        ).toGoogleCalendarEvent(),
        calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM
    )
}

@Preview("Mehrtägiger Anlass", showBackground = true)
@Composable
fun AnlaesseDetailContentViewPreview2() {
    AnlaesseDetailContentView(
        event = GoogleCalendarEventDto(
            id = "0nl482v21encap40tg8ecmomra",
            summary = "Wolfstufen-Weekend Wolfstufen-Weekend Wolfstufen-Weekend",
            description = "Ein erlebnisreiches Pfadiwochenende für alle Teilnehmenden der Wolfstufe",
            location = null,
            created = "2023-11-26T08:55:10.000Z",
            updated = "2023-11-26T08:55:10.887Z",
            start = GoogleCalendarEventStartEndDto(
                dateTime = "2024-02-24T10:00:00Z",
                date = null
            ),
            end = GoogleCalendarEventStartEndDto(
                dateTime = "2024-02-25T15:00:00Z",
                date = null
            )
        ).toGoogleCalendarEvent(),
        calendar = SeesturmCalendar.TERMINE
    )
}

@Preview("Eintägiger, ganztägiger Anlass", showBackground = true)
@Composable
fun AnlaesseDetailContentViewPreview3() {
    AnlaesseDetailContentView(
        event = GoogleCalendarEventDto(
            id = "4dai6m9r247vdl3t1oehi9arb0",
            summary = "Nationaler Pfadischnuppertag",
            description = null,
            location = null,
            created = "2024-11-16T14:49:15.000Z",
            updated = "2024-11-16T14:49:15.791Z",
            start = GoogleCalendarEventStartEndDto(
                dateTime = null,
                date = "2025-03-15"
            ),
            end = GoogleCalendarEventStartEndDto(
                dateTime = null,
                date = "2025-03-16"
            )
        ).toGoogleCalendarEvent(),
        calendar = SeesturmCalendar.TERMINE
    )
}

@Preview("Mehrtägiger, ganztägiger Anlass", showBackground = true)
@Composable
fun AnlaesseDetailContentViewPreview4() {
    AnlaesseDetailContentView(
        event = GoogleCalendarEventDto(
            id = "1p5bqoco2c1nhejhv6h0jn72mk",
            summary = "Sommerlager Pfadi- und Piostufe",
            description = "Das alljährliche Sommerlager der Pfadi Seesturm ist eines der grössten Pfadi-Highlights. Sei auch du dabei und verbringe 11 abenteuerliche Tage im Zelt.",
            location = null,
            created = "2022-11-20T11:16:53.000Z",
            updated = "2022-11-20T11:16:53.083Z",
            start = GoogleCalendarEventStartEndDto(
                dateTime = null,
                date = "2023-09-24"
            ),
            end = GoogleCalendarEventStartEndDto(
                dateTime = null,
                date = "2023-10-05"
            )
        ).toGoogleCalendarEvent(),
        calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM
    )
}