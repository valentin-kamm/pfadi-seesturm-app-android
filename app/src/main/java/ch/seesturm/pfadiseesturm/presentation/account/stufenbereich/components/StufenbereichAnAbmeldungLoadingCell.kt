package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.seesturm.pfadiseesturm.domain.wordpress.model.GoogleCalendarEventWithAnAbmeldungen
import ch.seesturm.pfadiseesturm.presentation.common.CustomCardView
import ch.seesturm.pfadiseesturm.presentation.common.RedactedText
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButton
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonColor
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonIconType
import ch.seesturm.pfadiseesturm.presentation.common.buttons.SeesturmButtonType
import ch.seesturm.pfadiseesturm.presentation.common.customLoadingBlinking
import ch.seesturm.pfadiseesturm.presentation.common.theme.PfadiSeesturmTheme
import ch.seesturm.pfadiseesturm.util.DummyData
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun StufenbereichAnAbmeldungLoadingCell(
    stufe: SeesturmStufe,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {

    CustomCardView(
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
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                RedactedText(
                    numberOfLines = 2,
                    textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    lastLineFraction = 0.75f,
                    modifier = Modifier
                        .weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .graphicsLayer()
                        .size(35.dp)
                        .customLoadingBlinking()
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .wrapContentSize()
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                stufe.allowedAktivitaetInteractions.sortedBy { it.id }.forEach { _ ->
                    SeesturmButton(
                        type = SeesturmButtonType.Secondary,
                        onClick = null,
                        title = "",
                        colors = SeesturmButtonColor.Custom(
                            buttonColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentColor = Color.Transparent
                        ),
                        enabled = false,
                        disabledAlpha = 1f,
                        modifier = Modifier
                            .weight(1f)
                            .customLoadingBlinking()
                    )
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SeesturmButton(
                    type = SeesturmButtonType.Primary,
                    onClick = null,
                    title = "Bearbeiten",
                    icon = SeesturmButtonIconType.Predefined(
                        icon = Icons.Default.Edit
                    ),
                    enabled = false,
                    colors = SeesturmButtonColor.Custom(
                        contentColor = stufe.onHighContrastColor(),
                        buttonColor = stufe.highContrastColor(isDarkTheme)
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun StufenbereichAnAbmeldungLoadingCellPreview() {
    PfadiSeesturmTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
                isDarkTheme = false,
                modifier = Modifier
            )
            StufenbereichAnAbmeldungLoadingCell(
                stufe = SeesturmStufe.Biber,
                isDarkTheme = false,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}