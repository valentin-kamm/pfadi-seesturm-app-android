package ch.seesturm.pfadiseesturm.util.types

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ch.seesturm.pfadiseesturm.R
import ch.seesturm.pfadiseesturm.domain.fcm.SeesturmFCMNotificationTopic
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_BLUE
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_YELLOW
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import kotlinx.serialization.Serializable

@Serializable
enum class SeesturmStufe(
    val id: Int,
    val stufenName: String,
    val aktivitaetDescription: String,
    val calendar: SeesturmCalendar,
    val iconReference: Int,
    val color: Color,
    val highContrastColor: @Composable (Boolean) -> Color,
    val onHighContrastColor: @Composable () -> Color,
    val allowedAktivitaetInteractions: List<AktivitaetInteractionType>,
    val aktivitaetNotificationTopic: SeesturmFCMNotificationTopic
) {
    Biber(
        id = 0,
        stufenName = "Biberstufe",
        aktivitaetDescription = "Biberstufen-Aktivität",
        calendar = SeesturmCalendar.AKTIVITAETEN_BIBERSTUFE,
        iconReference = R.drawable.biber,
        color = Color.SEESTURM_RED,
        highContrastColor = { Color.SEESTURM_RED },
        onHighContrastColor = { Color.White },
        allowedAktivitaetInteractions = listOf(
            AktivitaetInteractionType.ABMELDEN,
            AktivitaetInteractionType.ANMELDEN
        ),
        aktivitaetNotificationTopic = SeesturmFCMNotificationTopic.BiberAktivitaeten
    ),
    Wolf(
        id = 1,
        stufenName = "Wolfsstufe",
        aktivitaetDescription = "Wolfsstufen-Aktivität",
        calendar = SeesturmCalendar.AKTIVITAETEN_WOLFSSTUFE,
        iconReference = R.drawable.wolf,
        color = Color.SEESTURM_YELLOW,
        highContrastColor = { isDarkTheme ->
            if (isDarkTheme) {
                Color.SEESTURM_YELLOW
            }
            else {
                MaterialTheme.colorScheme.onBackground
            }
        },
        onHighContrastColor = { MaterialTheme.colorScheme.background },
        allowedAktivitaetInteractions = listOf(AktivitaetInteractionType.ABMELDEN),
        aktivitaetNotificationTopic = SeesturmFCMNotificationTopic.WolfAktivitaeten
    ),
    Pfadi(
        id = 2,
        stufenName = "Pfadistufe",
        aktivitaetDescription = "Pfadistufen-Aktivität",
        calendar = SeesturmCalendar.AKTIVITAETEN_PFADISTUFE,
        iconReference = R.drawable.pfadi,
        color = Color.SEESTURM_BLUE,
        highContrastColor = { Color.SEESTURM_BLUE },
        onHighContrastColor = { Color.White },
        allowedAktivitaetInteractions = listOf(AktivitaetInteractionType.ABMELDEN),
        aktivitaetNotificationTopic = SeesturmFCMNotificationTopic.PfadiAktivitaeten
    ),
    Pio(
        id = 3,
        stufenName = "Piostufe",
        aktivitaetDescription = "Piostufen-Aktivität",
        calendar = SeesturmCalendar.AKTIVITAETEN_PIOSTUFE,
        iconReference = R.drawable.pio,
        color = Color.SEESTURM_GREEN,
        highContrastColor = { Color.SEESTURM_GREEN },
        onHighContrastColor = { Color.White },
        allowedAktivitaetInteractions = listOf(AktivitaetInteractionType.ABMELDEN),
        aktivitaetNotificationTopic = SeesturmFCMNotificationTopic.PioAktivitaeten
    );
    companion object {
        fun fromId(id: Int): SeesturmStufe {
            return SeesturmStufe.entries.find { it.id == id } ?: throw PfadiSeesturmAppError.UnknownStufe(
                "Unbekannte Stufe."
            )
        }
        fun fromTopic(topic: SeesturmFCMNotificationTopic): SeesturmStufe? {
            return when (topic) {
                SeesturmFCMNotificationTopic.Schoepflialarm,
                SeesturmFCMNotificationTopic.SchoepflialarmReaction,
                SeesturmFCMNotificationTopic.Aktuell -> {
                    null
                }
                SeesturmFCMNotificationTopic.BiberAktivitaeten -> Biber
                SeesturmFCMNotificationTopic.WolfAktivitaeten -> Wolf
                SeesturmFCMNotificationTopic.PfadiAktivitaeten -> Pfadi
                SeesturmFCMNotificationTopic.PioAktivitaeten -> Pio
            }
        }
    }
}
val List<SeesturmStufe>.stufenDropdownText: String
    get() = when (this.size) {
        0 -> "Wählen"
        1 -> this.first().stufenName
        4 -> "Alle"
        else -> "Mehrere"
    }