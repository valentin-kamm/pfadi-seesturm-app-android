package ch.seesturm.pfadiseesturm.domain.auth.model

import ch.seesturm.pfadiseesturm.data.firestore.dto.FirebaseHitobitoUserDto
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.ZonedDateTimeSerializer
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.types.FirebaseHitobitoUserRole
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
import java.util.UUID

@Serializable
data class FirebaseHitobitoUser private constructor(
    val userId: String,
    val vorname: String?,
    val nachname: String?,
    val pfadiname: String?,
    val email: String?,
    val role: FirebaseHitobitoUserRole,
    val profilePictureUrl: String?,
    @Serializable(with = ZonedDateTimeSerializer::class) val created: ZonedDateTime,
    val createdFormatted: String,
    @Serializable(with = ZonedDateTimeSerializer::class) val modified: ZonedDateTime,
    val modifiedFormatted: String,
    val fcmToken: String?
) {

    companion object {

        fun from(dto: FirebaseHitobitoUserDto): FirebaseHitobitoUser {

            val createdDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(dto.created)
            val modifiedDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(dto.modified)

            return FirebaseHitobitoUser(
                userId = dto.id ?: UUID.randomUUID().toString(),
                vorname = dto.firstname,
                nachname = dto.lastname,
                pfadiname = dto.pfadiname,
                email = dto.email,
                role = FirebaseHitobitoUserRole.fromRole(dto.role),
                profilePictureUrl = dto.profilePictureUrl,
                created = createdDate,
                createdFormatted = DateTimeUtil.shared.formatDate(
                    date = createdDate,
                    format = "EEEE, d. MMMM yyyy 'Uhr'",
                    type = DateFormattingType.Relative(true)
                ),
                modified = modifiedDate,
                modifiedFormatted = DateTimeUtil.shared.formatDate(
                    date = modifiedDate,
                    format = "EEEE, d. MMMM yyyy 'Uhr'",
                    type = DateFormattingType.Relative(true)
                ),
                fcmToken = dto.fcmToken
            )
        }
        
        fun from(oldUser: FirebaseHitobitoUser, newProfilePictureUrl: String): FirebaseHitobitoUser {

            val now = ZonedDateTime.now()

            return FirebaseHitobitoUser(
                userId = oldUser.userId,
                vorname = oldUser.vorname,
                nachname = oldUser.nachname,
                pfadiname = oldUser.pfadiname,
                email = oldUser.email,
                role = oldUser.role,
                profilePictureUrl = newProfilePictureUrl,
                created = oldUser.created,
                createdFormatted = oldUser.createdFormatted,
                modified = now,
                modifiedFormatted = DateTimeUtil.shared.formatDate(
                    date = now,
                    format = "EEEE, d. MMMM yyyy 'Uhr'",
                    type = DateFormattingType.Relative(true)
                ),
                fcmToken = oldUser.fcmToken
            )
        }
    }

    val displayNameShort: String
        get() = pfadiname ?: vorname ?: "Unbekannter Benutzer"

    val profilePictureStoragePath: String
        get() = "profilePictures${userId}.jpg"
}

fun List<FirebaseHitobitoUser>.getUserById(uid: String): FirebaseHitobitoUser? {
    return this.firstOrNull { it.userId == uid }
}
fun List<FirebaseHitobitoUser>.getUsersById(uids: List<String>): List<FirebaseHitobitoUser?> {
    return uids.map { getUserById(it) }
}