package ch.seesturm.pfadiseesturm.domain.firestore.model

import ch.seesturm.pfadiseesturm.util.AktivitaetInteraction
import ch.seesturm.pfadiseesturm.util.SeesturmStufe
import java.time.ZonedDateTime

data class AktivitaetAnAbmeldung(
    var id: String,
    var eventId: String,
    var uid: String?,
    var vorname: String,
    var nachname: String,
    var pfadiname: String?,
    var bemerkung: String?,
    var type: AktivitaetInteraction,
    var stufe: SeesturmStufe,
    var created: ZonedDateTime,
    var modified: ZonedDateTime,
    var createdString: String,
    var modifiedString: String
) {
    val displayName: String
        get() = when (pfadiname) {
            null -> {
                "$vorname $nachname"
            }
            else -> {
                "$vorname $nachname / $pfadiname"
            }
        }
    val bemerkungForDisplay: String
        get() = when (bemerkung) {
            null -> {
                "Bemerkung: -"
            }
            else -> {
                "Bemerkung: $bemerkung"
            }
        }
}
