package ch.seesturm.pfadiseesturm.domain.firestore.model

import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import java.time.ZonedDateTime

data class AktivitaetAnAbmeldung(
    var id: String,
    var eventId: String,
    var uid: String?,
    var vorname: String,
    var nachname: String,
    var pfadiname: String?,
    var bemerkung: String?,
    var type: AktivitaetInteractionType,
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
        get() = if (bemerkung != null && bemerkung?.isNotEmpty() == true) {
            "Bemerkung: $bemerkung"
        }
        else {
            "Bemerkung: -"
        }
}
