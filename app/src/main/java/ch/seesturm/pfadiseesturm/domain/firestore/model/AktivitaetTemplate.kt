package ch.seesturm.pfadiseesturm.domain.firestore.model

import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import java.time.ZonedDateTime

data class AktivitaetTemplate(
    val id: String,
    val created: ZonedDateTime,
    val modified: ZonedDateTime,
    var stufe: SeesturmStufe,
    val description: String,
    val swipeActionsRevealed: Boolean = false
)
