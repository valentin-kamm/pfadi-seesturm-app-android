package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import kotlinx.serialization.Serializable

@Serializable
sealed class AktivitaetBearbeitenMode {

    @Serializable
    data object Insert: AktivitaetBearbeitenMode()

    @Serializable
    data class Update(
        val id: String
    ): AktivitaetBearbeitenMode()

    val verb: String
        get() = when (this) {
            Insert -> "veröffentlichen"
            is Update -> "aktualisieren"
        }
    val verbPassiv: String
        get() = when (this) {
            Insert -> "veröffentlicht"
            is Update -> "aktualisiert"
        }
    val buttonTitle: String
        get() = when (this) {
            Insert -> "Veröffentlichen"
            is Update -> "Aktualisieren"
        }
    fun topBarTitle(stufe: SeesturmStufe): String =
        when (this) {
            Insert -> "Neue ${stufe.aktivitaetDescription}"
            is Update -> "${stufe.aktivitaetDescription} bearbeiten"
        }
}