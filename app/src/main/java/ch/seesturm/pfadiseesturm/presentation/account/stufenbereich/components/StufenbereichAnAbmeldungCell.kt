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
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

@Composable
fun StufenbereichAnAbmeldungCell(
    aktivitaet: GoogleCalendarEventWithAnAbmeldungen,
    stufe: SeesturmStufe,
    isBearbeitenButtonLoading: Boolean,
    onOpenSheet: (AktivitaetInteractionType) -> Unit,
    onSendPushNotification: () -> Unit,
    onDeleteAnAbmeldungen: () -> Unit,
    onEditAktivitaet: () -> Unit,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {

    val anAbmeldungenCount: Map<AktivitaetInteractionType, Int> =
        stufe.allowedAktivitaetInteractions.sortedBy { it.id }.associateWith { type ->
            aktivitaet.anAbmeldungen.count { it.type == type }
        }

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
                    TextWithIcon(
                        type = TextWithIconType.Text(
                            text = aktivitaet.event.fullDateTimeFormatted,
                            textStyle = { MaterialTheme.typography.bodySmall.copy(hyphens = Hyphens.Auto) }
                        ),
                        imageVector = Icons.Outlined.AccessTime,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        iconTint = stufe.highContrastColor(isDarkTheme),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    if (aktivitaet.event.location != null) {
                        TextWithIcon(
                            type = TextWithIconType.Text(
                                text = aktivitaet.event.location,
                                textStyle = { MaterialTheme.typography.bodySmall.copy(hyphens = Hyphens.Auto) }
                            ),
                            imageVector = Icons.Outlined.LocationOn,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            iconTint = stufe.highContrastColor(isDarkTheme),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                anAbmeldungenCount.forEach { (interaction, count) ->
                    SeesturmButton(
                        type = SeesturmButtonType.Secondary,
                        colors = SeesturmButtonColor.Custom(
                            buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = interaction.color,
                        ),
                        icon = SeesturmButtonIconType.Predefined(
                            icon = interaction.icon
                        ),
                        title = "$count ${if (count == 1) { interaction.nomen } else { interaction.nomenMehrzahl }}",
                        onClick = {
                            onOpenSheet(interaction)
                        },
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                DropdownButton(
                    type = SeesturmButtonType.Primary,
                    title = "Bearbeiten",
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Default.Edit
                    ),
                    isLoading = isBearbeitenButtonLoading,
                    colors = SeesturmButtonColor.Custom(
                        contentColor = stufe.onHighContrastColor(),
                        buttonColor = stufe.highContrastColor(isDarkTheme)
                    ),
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
            isBearbeitenButtonLoading = true,
            onOpenSheet = {},
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
            isBearbeitenButtonLoading = false,
            onOpenSheet = {},
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
            isBearbeitenButtonLoading = false,
            onDeleteAnAbmeldungen = {},
            onOpenSheet = {},
            onSendPushNotification = {},
            onEditAktivitaet = {},
            onClick = {},
            isDarkTheme = false
        )
    }
}