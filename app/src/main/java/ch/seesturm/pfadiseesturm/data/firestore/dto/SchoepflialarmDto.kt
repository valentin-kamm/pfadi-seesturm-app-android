package ch.seesturm.pfadiseesturm.data.firestore.dto

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.model.getUserById
import ch.seesturm.pfadiseesturm.domain.firestore.model.Schoepflialarm
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class SchoepflialarmDto(

    @DocumentId override var id: String? = null,
    @ServerTimestamp override var created: Timestamp? = null,
    @ServerTimestamp override var modified: Timestamp? = null,
    val message: String = "",
    val userId: String = ""
): FirestoreDto {

    override fun <T : FirestoreDto> contentEquals(other: T): Boolean {
        if (other !is SchoepflialarmDto) return false
        return id == other.id &&
                message == other.message &&
                userId == other.userId
    }
}

fun SchoepflialarmDto.toSchoepflialarm(users: List<FirebaseHitobitoUser>, reactions: List<SchoepflialarmReactionDto>): Schoepflialarm {

    val createdDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(created)
    val modifiedDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(modified)

    return Schoepflialarm(
        id = id ?: UUID.randomUUID().toString(),
        created = createdDate,
        modified = modifiedDate,
        createdFormatted = DateTimeUtil.shared.formatDate(
            date = createdDate,
            format = "dd. MMM, HH:mm 'Uhr'",
            type = DateFormattingType.Relative(true)
        ),
        modifiedFormatted = DateTimeUtil.shared.formatDate(
            date = modifiedDate,
            format = "dd. MMM, HH:mm 'Uhr'",
            type = DateFormattingType.Relative(true)
        ),
        message = message,
        user = users.getUserById(userId),
        reactions = reactions.map { it.toSchoepflialarmReaction(users) }
    )
}