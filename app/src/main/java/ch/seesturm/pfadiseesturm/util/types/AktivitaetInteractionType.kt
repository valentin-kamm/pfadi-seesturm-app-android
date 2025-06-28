package ch.seesturm.pfadiseesturm.util.types

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_GREEN
import ch.seesturm.pfadiseesturm.presentation.common.theme.SEESTURM_RED
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError

enum class AktivitaetInteractionType(
    val id: Int,
    val nomen: String,
    val nomenMehrzahl: String,
    val verb: String,
    val taetigkeit: String,
    val icon: ImageVector,
    val color: Color
) {
    ANMELDEN(
        id = 1,
        nomen = "Anmeldung",
        nomenMehrzahl = "Anmeldungen",
        verb = "anmelden",
        taetigkeit = "Angemeldet",
        icon = Icons.Outlined.CheckCircle,
        color = Color.SEESTURM_GREEN
    ),
    ABMELDEN(
        id = 0,
        nomen = "Abmeldung",
        nomenMehrzahl = "Abmeldungen",
        verb = "abmelden",
        taetigkeit = "Abgemeldet",
        icon = Icons.Outlined.Cancel,
        color = Color.SEESTURM_RED
    );
    companion object {
        fun fromId(id: Int): AktivitaetInteractionType {
            return entries.find { it.id == id } ?: throw PfadiSeesturmAppError.UnknownAktivitaetInteraction(
                "Unbekannte An-/Abmelde-Art."
            )
        }
    }
}