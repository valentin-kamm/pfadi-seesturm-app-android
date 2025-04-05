package ch.seesturm.pfadiseesturm.presentation.anlaesse.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventStartEndDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.SeesturmCalendar

@Composable
fun AnlassCardView(
    event: GoogleCalendarEvent,
    calendar: SeesturmCalendar,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    CustomCardView(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CustomCardView(
                shadowColor = Color.Transparent,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .width(110.dp)
                    .height(85.dp)
                    .wrapContentWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight()
                ) {
                    val firstLine = if (event.endDateFormatted == null) {
                        event.startDayFormatted
                    }
                    else {
                        event.startDayFormatted + " " + event.startMonthFormatted
                    }
                    val secondLine = if (event.endDateFormatted == null) {
                        event.startMonthFormatted
                    }
                    else {
                        "bis " + event.endDateFormatted
                    }
                    Text(
                        text = firstLine,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    )
                    Text(
                        text = secondLine,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN,
                        modifier = Modifier
                            .size(
                                with(LocalDensity.current) {
                                    MaterialTheme.typography.bodyMedium.lineHeight.toPx().toDp()
                                }
                            )
                    )
                    Text(
                        text = event.timeFormatted,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .alpha(0.4f)
                    )
                }
                if (event.location != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = if (calendar.isLeitungsteam) Color.SEESTURM_RED else Color.SEESTURM_GREEN,
                            modifier = Modifier
                                .size(
                                    with(LocalDensity.current) {
                                        MaterialTheme.typography.bodyMedium.lineHeight.toPx().toDp()
                                    }
                                )
                        )
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                                .alpha(0.4f)
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

@Preview("Eintägiger Anlass")
@Composable
fun AnlassCardViewPreview() {
    AnlassCardView(
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
        calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM,
        onClick = {}
    )
}

@Preview("Mehrtägiger Anlass")
@Composable
fun AnlassCardViewPreview2() {
    AnlassCardView(
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
        onClick = {},
        calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM
    )
}

@Preview("Eintägiger, ganztägiger Anlass")
@Composable
fun AnlassCardViewPreview3() {
    AnlassCardView(
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
        onClick = {},
        calendar = SeesturmCalendar.TERMINE
    )
}

@Preview("Mehrtägiger, ganztägiger Anlass")
@Composable
fun AnlassCardViewPreview4() {
    AnlassCardView(
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
        onClick = {},
        calendar = SeesturmCalendar.TERMINE_LEITUNGSTEAM
    )
}
