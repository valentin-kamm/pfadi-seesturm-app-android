package ch.seesturm.pfadiseesturm.domain.auth.model

import ch.seesturm.pfadiseesturm.util.ZonedDateTimeSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class FirebaseHitobitoUser(
    val userId: String,
    val vorname: String?,
    val nachname: String?,
    val pfadiname: String?,
    val email: String?,
    @Serializable(with = ZonedDateTimeSerializer::class) val created: ZonedDateTime,
    val createdFormatted: String,
    @Serializable(with = ZonedDateTimeSerializer::class) val modified: ZonedDateTime,
    val modifiedFormatted: String
) {
    val displayNameShort: String
        get() = pfadiname ?: (vorname ?: "Unbekannter Benutzer")

    val displayNameFull: String
        get() = when (pfadiname) {
            null -> {
                if (vorname != null && nachname != null) {
                    "$vorname $nachname"
                }
                else vorname
                    ?: "Unbekannter Benutzer"
            }
            else -> {
                if (vorname != null && nachname != null) {
                    "$vorname $nachname / $pfadiname"
                }
                else if (vorname != null) {
                    "$vorname / $pfadiname"
                }
                else {
                    pfadiname
                }
            }
        }
}

fun List<FirebaseHitobitoUser>.getUserById(uid: String): FirebaseHitobitoUser? {
    return this.firstOrNull { it.userId == uid }
}
fun List<FirebaseHitobitoUser>.getUsersById(uids: List<String>): List<FirebaseHitobitoUser?> {
    return uids.map { getUserById(it) }
}