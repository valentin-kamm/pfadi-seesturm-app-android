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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEvent
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.rich_text.HtmlTextView
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe


@Composable
fun AktivitaetDetailCardView(
    aktivitaet: GoogleCalendarEvent?,
    stufe: SeesturmStufe,
    mode: AktivitaetDetailViewMode,
    isDarkTheme: Boolean,
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
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = aktivitaet.title,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                            textAlign = TextAlign.Start
                        )
                        TextWithIcon(
                            type = TextWithIconType.AnnotatedString(
                                annotatedString = buildAnnotatedString {
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
                                iconSize = with(LocalDensity.current) {
                                    MaterialTheme.typography.labelSmall.lineHeight.toDp()
                                }
                            ),
                            imageVector = Icons.Outlined.EventAvailable,
                            textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            iconTint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            maxLines = 1,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        if (aktivitaet.showUpdated) {
                            TextWithIcon(
                                type = TextWithIconType.AnnotatedString(
                                    annotatedString = buildAnnotatedString {
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
                                            append(aktivitaet.modifiedFormatted)
                                        }
                                    },
                                    iconSize = with(LocalDensity.current) {
                                        MaterialTheme.typography.labelSmall.lineHeight.toDp()
                                    }
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
                        modifier = Modifier
                            .wrapContentSize()
                            .size(35.dp)
                    )
                }
                HorizontalDivider()
                TextWithIcon(
                    type = TextWithIconType.AnnotatedString(
                        annotatedString = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    fontWeight = FontWeight.Bold,
                                    color = stufe.highContrastColor(isDarkTheme)
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
                        iconSize = with(LocalDensity.current) {
                            MaterialTheme.typography.bodyMedium.lineHeight.toDp()
                        }
                    ),
                    imageVector = Icons.Outlined.AccessTime,
                    textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    iconTint = stufe.highContrastColor(isDarkTheme),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (aktivitaet.location != null) {
                    TextWithIcon(
                        type = TextWithIconType.AnnotatedString(
                            annotatedString = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = FontWeight.Bold,
                                        color = stufe.highContrastColor(isDarkTheme)
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
                            iconSize = with(LocalDensity.current) {
                                MaterialTheme.typography.bodyMedium.lineHeight.toDp()
                            }
                        ),
                        imageVector = Icons.Outlined.LocationOn,
                        textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        iconTint = stufe.highContrastColor(isDarkTheme),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                if (aktivitaet.description != null) {
                    HorizontalDivider()
                    TextWithIcon(
                        type = TextWithIconType.Text(
                            text = "Infos",
                            textStyle = { MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) }
                        ),
                        imageVector = Icons.Outlined.Info,
                        textColor = stufe.highContrastColor(isDarkTheme),
                        iconTint = stufe.highContrastColor(isDarkTheme),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    HtmlTextView(
                        html = aktivitaet.description,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    stufe.allowedAktivitaetInteractions.sortedByDescending { it.id }.forEach { interaction ->
                        SeesturmButton(
                            type = SeesturmButtonType.Secondary,
                            colors = SeesturmButtonColor.Custom(
                                buttonColor = interaction.color,
                                contentColor = Color.White
                            ),
                            icon = SeesturmButtonIconType.Predefined(
                                icon = interaction.icon
                            ),
                            title = interaction.verb.capitalize(Locale("de-CH")),
                            onClick = {
                                if (mode is AktivitaetDetailViewMode.Interactive) {
                                    mode.onOpenSheet(interaction)
                                }
                            },
                            isLoading = false,
                            enabled = mode is AktivitaetDetailViewMode.Interactive
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
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                        color = MaterialTheme.colorScheme.onBackground,
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
                        type = SeesturmButtonType.Primary,
                        title = "Push-Nachrichten aktivieren",
                        enabled = mode is AktivitaetDetailViewMode.Interactive,
                        onClick = {
                            if (mode is AktivitaetDetailViewMode.Interactive) {
                                mode.onNavigateToPushNotifications()
                            }
                        },
                        isLoading = false
                    )
                }
            }
        }
    }
}

@Preview("Noch in Planung")
@Composable
private fun AktivitaetDetailCardViewPreview1() {
    PfadiSeesturmTheme {
        AktivitaetDetailCardView(
            aktivitaet = null,
            stufe = SeesturmStufe.Pfadi,
            mode = AktivitaetDetailViewMode.Interactive(
                onNavigateToPushNotifications = {},
                onOpenSheet = { _ -> }
            ),
            modifier = Modifier
                .fillMaxWidth(),
            isDarkTheme = false
        )
    }
}
@Preview("With interaction")
@Composable
private fun AktivitaetDetailCardViewPreview2() {
    PfadiSeesturmTheme {
        AktivitaetDetailCardView(
            aktivitaet = DummyData.aktivitaet1,
            stufe = SeesturmStufe.Pfadi,
            mode = AktivitaetDetailViewMode.Interactive(
                onNavigateToPushNotifications = {},
                onOpenSheet = { _ -> }
            ),
            modifier = Modifier
                .fillMaxWidth(),
            isDarkTheme = false
        )
    }
}
@Preview("View only")
@Composable
private fun AktivitaetDetailCardViewPreview3() {
    PfadiSeesturmTheme {
        AktivitaetDetailCardView(
            aktivitaet = DummyData.aktivitaet1,
            stufe = SeesturmStufe.Biber,
            mode = AktivitaetDetailViewMode.ViewOnly,
            modifier = Modifier
                .fillMaxWidth(),
            isDarkTheme = false
        )
    }
}