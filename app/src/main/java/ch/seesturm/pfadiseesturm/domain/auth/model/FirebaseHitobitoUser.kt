package ch.seesturm.pfadiseesturm.domain.auth.model

import ch.seesturm.pfadiseesturm.data.firestore.dto.FirebaseHitobitoUserDto
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmAppError
import ch.seesturm.pfadiseesturm.util.ZonedDateTimeSerializer
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.types.FirebaseHitobitoUserRole
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
class FirebaseHitobitoUser private constructor(
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
        fun create(dto: FirebaseHitobitoUserDto): FirebaseHitobitoUser {

            val createdDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(dto.created)
            val modifiedDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(dto.modified)
            val userId = dto.id ?: throw PfadiSeesturmAppError.AuthError("Die User ID ist ungültig.")

            return FirebaseHitobitoUser(
                userId = userId,
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
    }

    val displayNameShort: String
        get() = pfadiname ?: vorname ?: "Unbekannter Benutzer"
}

fun List<FirebaseHitobitoUser>.getUserById(uid: String): FirebaseHitobitoUser? {
    return this.firstOrNull { it.userId == uid }
}
fun List<FirebaseHitobitoUser>.getUsersById(uids: List<String>): List<FirebaseHitobitoUser?> {
    return uids.map { getUserById(it) }
}