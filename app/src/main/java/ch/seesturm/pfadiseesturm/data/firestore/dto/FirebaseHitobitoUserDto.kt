package ch.seesturm.pfadiseesturm.data.firestore.dto

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.util.types.DateFormattingType
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import ch.seesturm.pfadiseesturm.util.types.FirebaseHitobitoUserRole
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class FirebaseHitobitoUserDto(

    @DocumentId override var id: String? = null,
    @ServerTimestamp override var created: Timestamp? = null,
    @ServerTimestamp override var modified: Timestamp? = null,
    var email: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var pfadiname: String? = null,
    var role: String = "",
    val profilePictureUrl: String? = null,
    val fcmToken: String? = null
): FirestoreDto {

    override fun <T : FirestoreDto> contentEquals(other: T): Boolean {
        if (other !is FirebaseHitobitoUserDto) return false
        return id == other.id &&
                email == other.email &&
                firstname == other.firstname &&
                lastname == other.lastname &&
                pfadiname == other.pfadiname &&
                role == other.role &&
                profilePictureUrl == other.profilePictureUrl &&
                fcmToken == other.fcmToken
    }
}

fun FirebaseHitobitoUserDto.toFirebaseHitobitoUser(): FirebaseHitobitoUser {

    val createdDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(created)
    val modifiedDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(modified)

    return FirebaseHitobitoUser(
        userId = id ?: UUID.randomUUID().toString(),
        vorname = firstname,
        nachname = lastname,
        pfadiname = pfadiname,
        email = email,
        role = FirebaseHitobitoUserRole.fromRole(role),
        profilePictureUrl = profilePictureUrl,
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
        fcmToken = fcmToken
    )
}