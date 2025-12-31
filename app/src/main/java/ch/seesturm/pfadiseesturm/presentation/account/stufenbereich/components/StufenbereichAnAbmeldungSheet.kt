package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetAnAbmeldung
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIcon
import ch.seesturm.pfadiseesturm.presentation.common.TextWithIconType
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItem
import ch.seesturm.pfadiseesturm.presentation.common.forms.FormItemContentType
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe

@Composable
fun StufenbereichAnAbmeldungSheet(
    initialInteraction: AktivitaetInteractionType,
    aktivitaet: GoogleCalendarEventWithAnAbmeldungen,
    stufe: SeesturmStufe,
    modifier: Modifier = Modifier,
    columnState: LazyListState = rememberLazyListState()
) {

    var interaction by retain {
        mutableStateOf(initialInteraction)
    }

    val filteredSortedAnAbmeldungen: List<AktivitaetAnAbmeldung> =
        aktivitaet.anAbmeldungen
            .filter { it.type == interaction }
            .sortedByDescending { it.created }

    LazyColumn(
        state = columnState,
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (stufe.allowedAktivitaetInteractions.count() > 1) {
            item(key = "AnAbmeldungSheetFilterHeader") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .padding(bottom = 16.dp)
                ) {
                    stufe.allowedAktivitaetInteractions.sortedBy { it.id }.forEach { i ->
                        FilterChip(
                            selected = i == interaction,
                            onClick = { interaction = i },
                            label = {
                                Text(i.nomenMehrzahl)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = i.icon,
                                    contentDescription = null,
                                    tint = i.color
                                )
                            },
                            shape = FilterChipDefaults.shape
                        )
                    }
                }
            }
        }
        if (filteredSortedAnAbmeldungen.isEmpty()) {
            item(key = "AnAbmeldungSheetEmpty") {
                FormItem(
                    (0..<1).toList(),
                    index = 0,
                    mainContent = FormItemContentType.Custom(
                        content = {
                            Text(
                                text = "Keine ${interaction.nomenMehrzahl}",
                                style = MaterialTheme.typography.bodyMedium.copy(hyphens = Hyphens.Auto),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        },
                        contentPadding = PaddingValues(32.dp)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }
        }
        else {
            filteredSortedAnAbmeldungen.forEachIndexed { index, abmeldung ->
                item(key = "AnAbmeldungSheet${abmeldung.id}") {
                    FormItem(
                        items = filteredSortedAnAbmeldungen,
                        index = index,
                        mainContent = FormItemContentType.Custom(
                            content = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = abmeldung.displayName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, hyphens = Hyphens.Auto),
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Start,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    TextWithIcon(
                                        type = TextWithIconType.Text(
                                            text = "${abmeldung.type.taetigkeit}: ${abmeldung.createdString}",
                                            textStyle = { MaterialTheme.typography.bodySmall.copy(hyphens = Hyphens.Auto) }
                                        ),
                                        imageVector = abmeldung.type.icon,
                                        textColor = abmeldung.type.color,
                                        iconTint = abmeldung.type.color,
                                        horizontalAlignment = Alignment.Start,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Text(
                                        text = abmeldung.bemerkungForDisplay,
                                        style = MaterialTheme.typography.bodySmall.copy(hyphens = Hyphens.Auto),
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Start,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                }
                            },
                            contentPadding = PaddingValues(16.dp)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                    )
                }
            }
        }
    }
}

@Preview("Biber")
@Composable
fun StufenbereichAnAbmeldungSheetPreview1() {
    PfadiSeesturmTheme {
        StufenbereichAnAbmeldungSheet(
            initialInteraction = AktivitaetInteractionType.ABMELDEN,
            aktivitaet = GoogleCalendarEventWithAnAbmeldungen(
                event = DummyData.aktivitaet1,
                anAbmeldungen = listOf(DummyData.abmeldung1, DummyData.abmeldung2, DummyData.abmeldung3)
            ),
            stufe = SeesturmStufe.Biber
        )
    }
}
@Preview("Wolf")
@Composable
fun StufenbereichAnAbmeldungSheetPreview2() {
    PfadiSeesturmTheme {
        StufenbereichAnAbmeldungSheet(
            initialInteraction = AktivitaetInteractionType.ABMELDEN,
            aktivitaet = GoogleCalendarEventWithAnAbmeldungen(
                event = DummyData.aktivitaet1,
                anAbmeldungen = listOf(DummyData.abmeldung1, DummyData.abmeldung2, DummyData.abmeldung3)
            ),
            stufe = SeesturmStufe.Wolf
        )
    }
}
@Preview("Empty")
@Composable
fun StufenbereichAnAbmeldungSheetPreview3() {
    PfadiSeesturmTheme {
        StufenbereichAnAbmeldungSheet(
            initialInteraction = AktivitaetInteractionType.ABMELDEN,
            aktivitaet = GoogleCalendarEventWithAnAbmeldungen(
                event = DummyData.aktivitaet1,
                anAbmeldungen = listOf()
            ),
            stufe = SeesturmStufe.Wolf
        )
    }
}