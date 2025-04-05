package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
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
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetAnAbmeldung
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.domain.wordpress.model.displayTextAnAbmeldungen
import ch.seesturm.pfadiseesturm.presentation.common.components.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.components.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.components.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.util.AktivitaetInteraction
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import java.time.ZonedDateTime

@Composable
fun StufenbereichAnAbmeldungCell(
    aktivitaet: GoogleCalendarEventWithAnAbmeldungen,
    stufe: SeesturmStufe,
    selectedAktivitaetInteraction: AktivitaetInteraction,
    isBearbeitenButtonLoading: Boolean,
    onChangeSelectedAktivitaetInteraction: (AktivitaetInteraction) -> Unit,
    onDeleteAnAbmeldungen: () -> Unit,
    onSendPushNotification: () -> Unit,
    onEditAktivitaet: () -> Unit,
    modifier: Modifier = Modifier
) {

    val filteredAnAbmeldungen: List<AktivitaetAnAbmeldung> =
        aktivitaet.anAbmeldungen.filter { it.type == selectedAktivitaetInteraction }

    CustomCardView(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = aktivitaet.event.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = aktivitaet.event.fullDateTimeFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Image(
                    painter = painterResource(stufe.iconReference),
                    contentDescription = null,
                    modifier = Modifier
                        .wrapContentSize()
                        .size(35.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stufe.allowedAktivitaetInteractions.forEach { interaction ->
                    CustomCardView(
                        shadowColor = Color.Transparent,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            onChangeSelectedAktivitaetInteraction(interaction)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = interaction.icon,
                                contentDescription = null,
                                tint = interaction.color,
                                modifier = Modifier
                                    .size(
                                        with(LocalDensity.current) {
                                            MaterialTheme.typography.labelSmall.lineHeight.toPx().toDp()
                                        }
                                    )
                            )
                            Text(
                                text = aktivitaet.displayTextAnAbmeldungen(interaction),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start,
                                color = interaction.color
                            )
                        }
                    }
                }
            }
            CustomCardView(
                shadowColor = Color.Transparent,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (filteredAnAbmeldungen.isEmpty()) {
                    Text(
                        text = "Keine ${selectedAktivitaetInteraction.nomenMehrzahl}",
                        style = MaterialTheme.typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        filteredAnAbmeldungen.forEachIndexed { index, abmeldung ->
                            if (index > 0) {
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                                    modifier = Modifier
                                        .padding(0.dp)
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = abmeldung.displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Icon(
                                        abmeldung.type.icon,
                                        contentDescription = null,
                                        tint = abmeldung.type.color,
                                        modifier = Modifier
                                            .size(
                                                with(LocalDensity.current) {
                                                    MaterialTheme.typography.labelSmall.lineHeight.toPx()
                                                        .toDp()
                                                }
                                            )
                                            .wrapContentSize()
                                    )
                                    Text(
                                        text = "${abmeldung.type.taetigkeit}: ${abmeldung.createdString}",
                                        style = MaterialTheme.typography.labelSmall,
                                        overflow = TextOverflow.Ellipsis,
                                        color = abmeldung.type.color,
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                }
                                Text(
                                    text = abmeldung.bemerkungForDisplay,
                                    style = MaterialTheme.typography.labelSmall,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                DropdownButton(
                    title = "Bearbeiten",
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Default.Edit
                    ),
                    isLoading = isBearbeitenButtonLoading,
                    buttonColor = stufe.highContrastColor(),
                    contentColor = Color.White,
                    dropdown = { isShown, dismiss ->
                        DropdownMenu(
                            expanded = isShown,
                            onDismissRequest = {
                                dismiss()
                            }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("An- und Abmeldungen löschen")
                                },
                                onClick = {
                                    onDeleteAnAbmeldungen()
                                    dismiss()
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = null
                                    )
                                },
                                enabled = aktivitaet.event.hasEnded && aktivitaet.anAbmeldungen.isNotEmpty()
                            )
                            DropdownMenuItem(
                                text = {
                                    Text("Push-Nachricht senden")
                                },
                                onClick = {
                                    onSendPushNotification()
                                    dismiss()
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Notifications,
                                        contentDescription = null
                                    )
                                },
                                enabled = !aktivitaet.event.hasStarted
                            )
                            DropdownMenuItem(
                                text = {
                                    Text("Aktivität bearbeiten")
                                },
                                onClick = {
                                    onEditAktivitaet()
                                    dismiss()
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = null
                                    )
                                },
                                enabled = !aktivitaet.event.hasStarted
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun StufenbereichAnAbmeldungCellPreview() {
    StufenbereichAnAbmeldungCell(
        aktivitaet = GoogleCalendarEventWithAnAbmeldungen(
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
            anAbmeldungen = listOf(
                AktivitaetAnAbmeldung(
                    id = "123",
                    eventId = "049i70bbetjb6j9nqi9in866bl",
                    uid = "aksjdfhakjfh",
                    vorname = "Peter",
                    nachname = "Müller",
                    pfadiname = "XY",
                    bemerkung = "Hallo",
                    type = AktivitaetInteraction.ABMELDEN,
                    stufe = SeesturmStufe.Biber,
                    created = ZonedDateTime.now(),
                    modified = ZonedDateTime.now(),
                    createdString = "",
                    modifiedString = ""
                ),
                AktivitaetAnAbmeldung(
                    id = "3458o3794",
                    eventId = "049i70bbetjb6j9nqi9in866bl",
                    uid = "aksjdfhakjfh",
                    vorname = "Peter",
                    nachname = "Müller",
                    pfadiname = "XY",
                    bemerkung = "Hallo",
                    type = AktivitaetInteraction.ANMELDEN,
                    stufe = SeesturmStufe.Biber,
                    created = ZonedDateTime.now(),
                    modified = ZonedDateTime.now(),
                    createdString = "",
                    modifiedString = ""
                ),
                AktivitaetAnAbmeldung(
                    id = "ewkrunwekr",
                    eventId = "049i70bbetjb6j9nqi9in866bl",
                    uid = "aksjdfhakjfh",
                    vorname = "Franz",
                    nachname = "Müller",
                    pfadiname = "XY",
                    bemerkung = "Hallo",
                    type = AktivitaetInteraction.ABMELDEN,
                    stufe = SeesturmStufe.Biber,
                    created = ZonedDateTime.now(),
                    modified = ZonedDateTime.now(),
                    createdString = "",
                    modifiedString = ""
                )
            )
        ),
        stufe = SeesturmStufe.Biber,
        selectedAktivitaetInteraction = AktivitaetInteraction.ABMELDEN,
        isBearbeitenButtonLoading = false,
        onChangeSelectedAktivitaetInteraction = {},
        onDeleteAnAbmeldungen = {},
        onSendPushNotification = {},
        onEditAktivitaet = {},
        modifier = Modifier
            .fillMaxWidth()
    )
}