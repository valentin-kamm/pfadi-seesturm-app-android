package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
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
import ch.seesturm.pfadiseesturm.presentation.common.lists.GroupedColumn
import ch.seesturm.pfadiseesturm.presentation.common.sheet.LocalScreenContext
import ch.seesturm.pfadiseesturm.presentation.common.sheet.ScreenContext
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

    GroupedColumn(
        state = columnState,
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (stufe.allowedAktivitaetInteractions.count() > 1) {
            section {
                customItem("AnAbmeldungSheetFilterHeader") {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        stufe.allowedAktivitaetInteractions.sortedBy { it.id }.forEachIndexed { index, i ->
                            SegmentedButton(
                                selected = i == interaction,
                                onClick = {
                                    interaction = i
                                },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = stufe.allowedAktivitaetInteractions.size
                                ),
                                colors = SegmentedButtonDefaults.colors().copy(
                                    activeContentColor = i.color
                                ),
                                icon = {
                                    AnimatedVisibility(
                                        visible = i == interaction,
                                        exit = ExitTransition.None,
                                        enter = fadeIn(
                                            animationSpec = tween(
                                                durationMillis = 150,
                                                easing = LinearOutSlowInEasing
                                            )
                                        ) + scaleIn(
                                            initialScale = 0f,
                                            transformOrigin = TransformOrigin(0f, 1f),
                                            animationSpec = tween(
                                                durationMillis = 120,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    ) {
                                        Icon(
                                            imageVector = i.icon,
                                            contentDescription = null
                                        )
                                    }
                                }
                            ) {
                                Text(
                                    i.nomenMehrzahl,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
        section {
            if (filteredSortedAnAbmeldungen.isEmpty()) {
                item(
                    key = "AnAbmeldungSheetEmpty"
                ) {
                    Text(
                        text = "Keine ${interaction.nomenMehrzahl}",
                        style = MaterialTheme.typography.bodyMedium.copy(hyphens = Hyphens.Auto),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }
            }
            else {
                items(
                    items = filteredSortedAnAbmeldungen,
                    key = { abmeldung ->
                        "AnAbmeldungSheet${abmeldung.id}"
                    }
                ) { abmeldung ->
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
                }
            }
        }
    }
}

@Preview("Biber")
@Composable
fun StufenbereichAnAbmeldungSheetPreview1() {
    PfadiSeesturmTheme {
        CompositionLocalProvider(
            LocalScreenContext provides ScreenContext.ModalBottomSheet
        ) {
            StufenbereichAnAbmeldungSheet(
                initialInteraction = AktivitaetInteractionType.ABMELDEN,
                aktivitaet = GoogleCalendarEventWithAnAbmeldungen(
                    event = DummyData.aktivitaet1,
                    anAbmeldungen = listOf(
                        DummyData.abmeldung1,
                        DummyData.abmeldung2,
                        DummyData.abmeldung3
                    )
                ),
                stufe = SeesturmStufe.Biber
            )
        }
    }
}
@Preview("Wolf")
@Composable
fun StufenbereichAnAbmeldungSheetPreview2() {
    PfadiSeesturmTheme {
        CompositionLocalProvider(
            LocalScreenContext provides ScreenContext.ModalBottomSheet
        ) {
            StufenbereichAnAbmeldungSheet(
                initialInteraction = AktivitaetInteractionType.ABMELDEN,
                aktivitaet = GoogleCalendarEventWithAnAbmeldungen(
                    event = DummyData.aktivitaet1,
                    anAbmeldungen = listOf(
                        DummyData.abmeldung1,
                        DummyData.abmeldung2,
                        DummyData.abmeldung3
                    )
                ),
                stufe = SeesturmStufe.Wolf
            )
        }
    }
}
@Preview("Empty")
@Composable
fun StufenbereichAnAbmeldungSheetPreview3() {
    PfadiSeesturmTheme {
        CompositionLocalProvider(
            LocalScreenContext provides ScreenContext.ModalBottomSheet
        ) {
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
}