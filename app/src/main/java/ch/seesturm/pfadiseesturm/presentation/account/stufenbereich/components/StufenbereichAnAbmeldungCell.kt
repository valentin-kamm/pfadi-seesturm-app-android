package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetAnAbmeldung
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.domain.wordpress.model.displayTextAnAbmeldungen
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenu
import ch.seesturm.pfadiseesturm.presentation.common.ThemedDropdownMenuItem
import ch.seesturm.pfadiseesturm.presentation.common.buttons.DropdownButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

@Composable
fun StufenbereichAnAbmeldungCell(
    aktivitaet: GoogleCalendarEventWithAnAbmeldungen,
    stufe: SeesturmStufe,
    selectedAktivitaetInteraction: AktivitaetInteractionType,
    isBearbeitenButtonLoading: Boolean,
    onChangeSelectedAktivitaetInteraction: (AktivitaetInteractionType) -> Unit,
    onDeleteAnAbmeldungen: () -> Unit,
    onSendPushNotification: () -> Unit,
    onEditAktivitaet: () -> Unit,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {

    val filteredAnAbmeldungen: List<AktivitaetAnAbmeldung> =
        aktivitaet.anAbmeldungen.filter { it.type == selectedAktivitaetInteraction }

    CustomCardView(
        onClick = onClick,
        modifier = modifier
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
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = aktivitaet.event.fullDateTimeFormatted,
                        style = MaterialTheme.typography.bodySmall.copy(hyphens = Hyphens.Auto),
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
                        TextWithIcon(
                            type = TextWithIconType.Text(
                                text = aktivitaet.displayTextAnAbmeldungen(interaction),
                                textStyle = { MaterialTheme.typography.labelSmall }
                            ),
                            imageVector = interaction.icon,
                            textColor = interaction.color,
                            iconTint = interaction.color,
                            horizontalAlignment = Alignment.Start,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(8.dp)
                        )
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
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = abmeldung.displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                TextWithIcon(
                                    type = TextWithIconType.Text(
                                        text = "${abmeldung.type.taetigkeit}: ${abmeldung.createdString}",
                                        textStyle = { MaterialTheme.typography.labelSmall }
                                    ),
                                    imageVector = abmeldung.type.icon,
                                    textColor = abmeldung.type.color,
                                    iconTint = abmeldung.type.color,
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                )
                                Text(
                                    text = abmeldung.bemerkungForDisplay,
                                    style = MaterialTheme.typography.labelSmall.copy(hyphens = Hyphens.Auto),
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
                    buttonColor = stufe.highContrastColor(isDarkTheme),
                    contentColor = stufe.onHighContrastColor(),
                    dropdown = { isShown, dismiss ->
                        ThemedDropdownMenu(
                            expanded = isShown,
                            onDismissRequest = {
                                dismiss()
                            }
                        ) {
                            ThemedDropdownMenuItem(
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
                            ThemedDropdownMenuItem(
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
                            ThemedDropdownMenuItem(
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
                        }
                    }
                )
            }
        }
    }
}

@Preview("Loading")
@Composable
private fun StufenbereichAnAbmeldungCellPreview1() {
    PfadiSeesturmTheme {
        StufenbereichAnAbmeldungCell(
            aktivitaet = GoogleCalendarEventWithAnAbmeldungen(
                event = DummyData.aktivitaet1,
                anAbmeldungen = listOf(
                    DummyData.abmeldung1,
                    DummyData.abmeldung2
                )
            ),
            stufe = SeesturmStufe.Biber,
            selectedAktivitaetInteraction = AktivitaetInteractionType.ABMELDEN,
            isBearbeitenButtonLoading = true,
            onChangeSelectedAktivitaetInteraction = {},
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onClick = {},
            isDarkTheme = false
        )
    }
}
@Preview("Idle")
@Composable
private fun StufenbereichAnAbmeldungCellPreview2() {
    PfadiSeesturmTheme {
        StufenbereichAnAbmeldungCell(
            aktivitaet = GoogleCalendarEventWithAnAbmeldungen(
                event = DummyData.aktivitaet1,
                anAbmeldungen = listOf(
                    DummyData.abmeldung1,
                    DummyData.abmeldung2
                )
            ),
            stufe = SeesturmStufe.Biber,
            selectedAktivitaetInteraction = AktivitaetInteractionType.ABMELDEN,
            isBearbeitenButtonLoading = false,
            onChangeSelectedAktivitaetInteraction = {},
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onClick = {},
            isDarkTheme = false
        )
    }
}
@Preview("Empty")
@Composable
private fun StufenbereichAnAbmeldungCellPreview3() {
    PfadiSeesturmTheme {
        StufenbereichAnAbmeldungCell(
            aktivitaet = GoogleCalendarEventWithAnAbmeldungen(
                event = DummyData.aktivitaet1,
                anAbmeldungen = emptyList()
            ),
            stufe = SeesturmStufe.Biber,
            selectedAktivitaetInteraction = AktivitaetInteractionType.ABMELDEN,
            isBearbeitenButtonLoading = false,
            onChangeSelectedAktivitaetInteraction = {},
            onDeleteAnAbmeldungen = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onClick = {},
            isDarkTheme = false
        )
    }
}