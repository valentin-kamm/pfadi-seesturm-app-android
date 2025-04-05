package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
import ch.seesturm.pfadiseesturm.util.SeesturmStufe

@Composable
fun AktivitaetHomeCardView(
    aktivitaet: GoogleCalendarEvent?,
    stufe: SeesturmStufe,
    onClick: () -> Unit,
    modifier: Modifier
) {

    CustomCardView(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = aktivitaet?.title ?: stufe.stufenName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        if (aktivitaet != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Outlined.Refresh,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .size(
                                            with(LocalDensity.current) {
                                                MaterialTheme.typography.bodySmall.lineHeight.toPx().toDp()
                                            }
                                        )
                                        .alpha(0.4f)
                                        .wrapContentSize()
                                )
                                Text(
                                    text = aktivitaet.updatedFormatted,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .alpha(0.4f)
                                        .weight(1f)
                                )
                            }
                        }
                    }
                    Image(
                        painter = painterResource(stufe.iconReference),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .wrapContentSize()
                            .size(40.dp)
                    )
                }
                if (aktivitaet != null) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = stufe.highContrastColor(),
                            modifier = Modifier
                                .size(
                                    with(LocalDensity.current) {
                                        MaterialTheme.typography.bodyMedium.lineHeight.toPx().toDp()
                                    }
                                )
                        )
                        Text(
                            text = aktivitaet.fullDateTimeFormatted,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(0.4f)
                        )
                    }
                }
                else {
                    Text(
                        text = "Die nächste Aktivität ist noch in Planung",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.4f)
                            .padding(bottom = 16.dp)
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier
                    .size(14.dp)
                    .alpha(0.4f)
                    .wrapContentSize()
            )
        }
    }
}

@Preview("Nicht Fertig geplant")
@Composable
private fun AktivitaetHomeCardViewPreview1() {

    AktivitaetHomeCardView(
        aktivitaet = null,
        stufe = SeesturmStufe.Biber,
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Preview("Fertig geplant")
@Composable
private fun AktivitaetHomeCardViewPreview2() {

    val event = GoogleCalendarEventDto(
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

    AktivitaetHomeCardView(
        aktivitaet = event,
        stufe = SeesturmStufe.Biber,
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
    )
}