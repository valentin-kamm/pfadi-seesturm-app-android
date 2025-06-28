package ch.seesturm.pfadiseesturm.data.firestore.dto

import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetAnAbmeldung
import ch.seesturm.pfadiseesturm.util.types.AktivitaetInteractionType
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class AktivitaetAnAbmeldungDto(

    @DocumentId override var id: String? = null,
    var eventId: String = "",
    var uid: String? = null,
    var vorname: String = "",
    var nachname: String = "",
    var pfadiname: String? = null,
    var bemerkung: String? = null,
    var typeId: Int = 0,
    var stufenId: Int = 0,
    @ServerTimestamp override var created: Timestamp? = null,
    @ServerTimestamp override var modified: Timestamp? = null
): FirestoreDto {

    override fun <T : FirestoreDto> contentEquals(other: T): Boolean {
        if (other !is AktivitaetAnAbmeldungDto) return false
        return id == other.id &&
                eventId == other.eventId &&
                uid == other.uid &&
                vorname == other.vorname &&
                nachname == other.nachname &&
                pfadiname == other.pfadiname &&
                bemerkung == other.bemerkung &&
                typeId == other.typeId &&
                stufenId == other.stufenId
    }
}

fun AktivitaetAnAbmeldungDto.toAktivitaetAnAbmeldung(): AktivitaetAnAbmeldung {

    val createdDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(created)
    val modifiedDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(modified)

    return AktivitaetAnAbmeldung(
        id = id ?: UUID.randomUUID().toString(),
        eventId = eventId,
        uid = uid,
        vorname = vorname,
        nachname = nachname,
        pfadiname = pfadiname,
        bemerkung = bemerkung,
        type = AktivitaetInteractionType.fromId(typeId),
        stufe = SeesturmStufe.fromId(stufenId),
        created = createdDate,
        modified = modifiedDate,
        createdString = DateTimeUtil.shared.formatDate(
            date = createdDate,
            format = "EEEE, dd. MMMM, HH:mm 'Uhr'",
            type = DateFormattingType.Relative(true)
        ),
        modifiedString = DateTimeUtil.shared.formatDate(
            date = modifiedDate,
            format = "EEEE, dd. MMMM, HH:mm 'Uhr'",
            type = DateFormattingType.Relative(true)
        )
    )
}