package ch.seesturm.pfadiseesturm.domain.firestore.model

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import java.time.ZonedDateTime

data class SchoepflialarmReaction(
    val id: String,
    val created: ZonedDateTime,
    val modified: ZonedDateTime,
    val createdFormatted: String,
    val modifiedFormatted: String,
    val user: FirebaseHitobitoUser?,
    val reaction: SchoepflialarmReactionType
)
