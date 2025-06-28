package ch.seesturm.pfadiseesturm.domain.firestore.model

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.util.types.SchoepflialarmReactionType
import java.time.ZonedDateTime

data class Schoepflialarm(
    val id: String,
    val created: ZonedDateTime,
    val modified: ZonedDateTime,
    val createdFormatted: String,
    val modifiedFormatted: String,
    val message: String,
    val user: FirebaseHitobitoUser?,
    val reactions: List<SchoepflialarmReaction>
) {
    fun reactionCount(reaction: SchoepflialarmReactionType): Int {
        return this.reactions.count { it.reaction == reaction }
    }
    fun reactions(reaction: SchoepflialarmReactionType): List<SchoepflialarmReaction> {
        return this.reactions.filter { it.reaction == reaction }.sortedByDescending { it.created }
    }
}