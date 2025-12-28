package ch.seesturm.pfadiseesturm.presentation.naechste_aktivitaet.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

@Composable
fun AktivitaetHomeCardView(
    aktivitaet: GoogleCalendarEvent?,
    stufe: SeesturmStufe,
    isDarkTheme: Boolean,
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
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        if (aktivitaet != null) {
                            TextWithIcon(
                                type = TextWithIconType.Text(
                                    text = aktivitaet.modifiedFormatted,
                                    textStyle = { MaterialTheme.typography.bodySmall }
                                ),
                                imageVector = Icons.Outlined.Refresh,
                                textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                iconTint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                maxLines = 1,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
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
                    TextWithIcon(
                        type = TextWithIconType.Text(
                            text = aktivitaet.fullDateTimeFormatted,
                            textStyle = { MaterialTheme.typography.bodyMedium }
                        ),
                        imageVector = Icons.Outlined.CalendarMonth,
                        textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        iconTint = stufe.highContrastColor(isDarkTheme),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                else {
                    Text(
                        text = "Die nächste Aktivität ist noch in Planung",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.4f)
                            .padding(bottom = 8.dp)
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

@Preview("Noch in Planung")
@Composable
private fun AktivitaetHomeCardViewPreview1() {
    PfadiSeesturmTheme {
        AktivitaetHomeCardView(
            aktivitaet = null,
            stufe = SeesturmStufe.Biber,
            onClick = {},
            modifier = Modifier,
            isDarkTheme = false
        )
    }
}
@Preview("Fertig geplant")
@Composable
private fun AktivitaetHomeCardViewPreview2() {
    PfadiSeesturmTheme {
        AktivitaetHomeCardView(
            aktivitaet = DummyData.aktivitaet1,
            stufe = SeesturmStufe.Pfadi,
            onClick = {},
            modifier = Modifier,
            isDarkTheme = false
        )
    }
}