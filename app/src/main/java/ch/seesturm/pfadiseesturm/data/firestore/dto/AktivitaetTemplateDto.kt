package ch.seesturm.pfadiseesturm.data.firestore.dto

import ch.seesturm.pfadiseesturm.domain.firestore.model.AktivitaetTemplate
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.SeesturmStufe
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class AktivitaetTemplateDto(

    @DocumentId override var id: String? = null,
    @ServerTimestamp override var created: Timestamp? = null,
    @ServerTimestamp override var modified: Timestamp? = null,
    val stufenId: Int = -1,
    val description: String = ""
): FirestoreDto {

    override fun <T : FirestoreDto> contentEquals(other: T): Boolean {
        if (other !is AktivitaetTemplateDto) return false
        return id == other.id &&
                stufenId == other.stufenId &&
                description == other.description
    }
}

fun AktivitaetTemplateDto.toAktivitaetTemplate(): AktivitaetTemplate {
    return AktivitaetTemplate(
        id = id ?: UUID.randomUUID().toString(),
        created = DateTimeUtil.shared.convertFirestoreTimestampToDate(created),
        modified = DateTimeUtil.shared.convertFirestoreTimestampToDate(modified),
        stufe = SeesturmStufe.fromId(stufenId),
        description = description
    )
}