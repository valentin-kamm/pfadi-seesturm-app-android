package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.GoogleCalendarEventStartEndDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toGoogleCalendarEvent
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.HtmlText
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonType
import ch.seesturm.pfadiseesturm.util.AktivitaetInteraction
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.navigation.AppDestination

@Composable
fun AktivitaetDetailCardView(
    stufe: SeesturmStufe,
    aktivitaet: GoogleCalendarEvent?,
    type: AktivitaetDetailCardViewType,
    modifier: Modifier = Modifier
) {
    CustomCardView(
        modifier = modifier
    ) {
        if (aktivitaet != null) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = aktivitaet.title,
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Start
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EventAvailable,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(
                                        with(LocalDensity.current) {
                                            MaterialTheme.typography.labelSmall.lineHeight
                                                .toPx()
                                                .toDp()
                                        }
                                    )
                                    .alpha(0.4f)
                                    .wrapContentSize()
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.4f
                                            )
                                        )
                                    ) {
                                        append("Veröffentlicht: ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.4f
                                            )
                                        )
                                    ) {
                                        append(aktivitaet.createdFormatted)
                                    }
                                },
                                maxLines = 1,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                        if (aktivitaet.showUpdated) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .size(
                                            with(LocalDensity.current) {
                                                MaterialTheme.typography.labelSmall.lineHeight
                                                    .toPx()
                                                    .toDp()
                                            }
                                        )
                                        .alpha(0.4f)
                                        .wrapContentSize()
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground.copy(
                                                    alpha = 0.4f
                                                )
                                            )
                                        ) {
                                            append("Aktualisiert: ")
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                color = MaterialTheme.colorScheme.onBackground.copy(
                                                    alpha = 0.4f
                                                )
                                            )
                                        ) {
                                            append(aktivitaet.updatedFormatted)
                                        }
                                    },
                                    maxLines = 1,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                        }
                    }
                    Image(
                        painter = painterResource(stufe.iconReference),
                        contentDescription = null,
                        modifier = Modifier
                            .wrapContentSize()
                            .size(35.dp)
                    )
                }
                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = stufe.highContrastColor(),
                        modifier = Modifier
                            .size(
                                with(LocalDensity.current) {
                                    MaterialTheme.typography.bodyMedium.lineHeight
                                        .toPx()
                                        .toDp()
                                }
                            )
                            .wrapContentSize()
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = FontWeight.Bold,
                                    color = stufe.highContrastColor()
                                )
                            ) {
                                append("Zeit: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    color = MaterialTheme.colorScheme.onBackground.copy(
                                        alpha = 0.4f
                                    )
                                )
                            ) {
                                append(aktivitaet.fullDateTimeFormatted)
                            }
                        },
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                if (aktivitaet.location != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = stufe.highContrastColor(),
                            modifier = Modifier
                                .size(
                                    with(LocalDensity.current) {
                                        MaterialTheme.typography.bodyMedium.lineHeight
                                            .toPx()
                                            .toDp()
                                    }
                                )
                                .wrapContentSize()
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = FontWeight.Bold,
                                        color = stufe.highContrastColor()
                                    )
                                ) {
                                    append("Treffpunkt: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        color = MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = 0.4f
                                        )
                                    )
                                ) {
                                    append(aktivitaet.location)
                                }
                            },
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
                if (aktivitaet.description != null) {
                    HorizontalDivider()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = stufe.highContrastColor(),
                            modifier = Modifier
                                .size(
                                    with(LocalDensity.current) {
                                        MaterialTheme.typography.bodyMedium.lineHeight
                                            .toPx()
                                            .toDp()
                                    }
                                )
                                .wrapContentSize()
                        )
                        Text(
                            text = "Infos",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Start,
                            color = stufe.highContrastColor(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                    HtmlText(
                        html = aktivitaet.description,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        fontStyle = MaterialTheme.typography.bodyMedium
                    )
                }
                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    stufe.allowedAktivitaetInteractions.forEach { interaction ->
                        SeesturmButton(
                            type = SeesturmButtonType.Primary(
                                buttonColor = interaction.color,
                                icon = SeesturmButtonIconType.Predefined(
                                    icon = interaction.icon
                                )
                            ),
                            enabled = type is AktivitaetDetailCardViewType.Normal,
                            title = interaction.verb.capitalize(Locale("de-CH")),
                            onClick = {
                                if (type is AktivitaetDetailCardViewType.Normal) {
                                    type.openSheet(interaction)
                                }
                            }
                        )
                    }
                }
            }
        }
        else {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stufe.stufenName,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Image(
                        painter = painterResource(stufe.iconReference),
                        contentDescription = null,
                        modifier = Modifier
                            .wrapContentSize()
                            .size(35.dp)
                    )
                }
                Text(
                    text = "Die nächste Aktivität ist noch in Planung.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.4f)
                        .padding(top = 16.dp)
                )
                Text(
                    text = "Aktiviere die Push-Nachrichten, um benachrichtigt zu werden, sobald die Aktivität fertig geplant ist.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.4f)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    SeesturmButton(
                        type = SeesturmButtonType.Primary(),
                        title = "Push-Nachrichten aktivieren",
                        onClick = {
                            if (type is AktivitaetDetailCardViewType.Normal) {
                                type.navController.navigate(
                                    AppDestination.MainTabView.Destinations.Home.Destinations.PushNotifications
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

sealed class AktivitaetDetailCardViewType {
    data object Preview: AktivitaetDetailCardViewType()
    data class Normal(
        val navController: NavController,
        val openSheet: (AktivitaetInteraction) -> Unit,
    ): AktivitaetDetailCardViewType()
}

@Preview
@Composable
private fun AktivitaetDetailCardViewPreview1() {
    val na = GoogleCalendarEventDto(
        id = "17v15laf167s75oq47elh17a3t",
        summary = "Biberstufen-Aktivität",
        description = "Ob uns wohl der Pfadi-Chlaus dieses Jahr wieder viele Nüssli und Schöggeli bringt? Die genauen Zeiten werden später kommuniziert.",
        location = "Geiserparkplatz",
        created = "2022-08-28T15:25:45.701Z",
        updated = "2022-08-28T15:19:45.726Z",
        start = GoogleCalendarEventStartEndDto(
            dateTime = "2022-12-10T13:00:00Z",
            date = null
        ),
        end = GoogleCalendarEventStartEndDto(
            dateTime = "2022-12-10T15:00:00Z",
            date = null
        )
    ).toGoogleCalendarEvent()
    AktivitaetDetailCardView(
        stufe = SeesturmStufe.Pio,
        aktivitaet = na,
        type = AktivitaetDetailCardViewType.Normal(
            navController = rememberNavController(),
            openSheet = {}
        ),
        modifier = Modifier
            .fillMaxWidth()
    )
}
@Preview
@Composable
private fun AktivitaetDetailCardViewPreview2() {
    AktivitaetDetailCardView(
        stufe = SeesturmStufe.Biber,
        aktivitaet = null,
        type = AktivitaetDetailCardViewType.Normal(
            navController = rememberNavController(),
            openSheet = {}
        ),
        modifier = Modifier
            .fillMaxWidth()
    )
}