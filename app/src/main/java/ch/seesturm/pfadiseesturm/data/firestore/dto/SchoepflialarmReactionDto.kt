package ch.seesturm.pfadiseesturm.data.firestore.dto

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.domain.auth.model.getUserById
import ch.seesturm.pfadiseesturm.domain.firestore.model.SchoepflialarmReaction
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class SchoepflialarmReactionDto(

    @DocumentId override var id: String? = null,
    @ServerTimestamp override var created: Timestamp? = null,
    @ServerTimestamp override var modified: Timestamp? = null,
    val userId: String = "",
    val reaction: String = ""
): FirestoreDto {

    override fun <T : FirestoreDto> contentEquals(other: T): Boolean {
        if (other !is SchoepflialarmReactionDto) return false
        return id == other.id &&
                userId == other.userId &&
                reaction == other.reaction
    }
}

fun SchoepflialarmReactionDto.toSchoepflialarmReaction(users: List<FirebaseHitobitoUser>): SchoepflialarmReaction {

    val createdDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(created)
    val modifiedDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(modified)

    return SchoepflialarmReaction(
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
        user = users.getUserById(userId),
        reaction = SchoepflialarmReactionType.fromString(reaction)
    )
}