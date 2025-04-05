package ch.seesturm.pfadiseesturm.data.firestore.dto

import ch.seesturm.pfadiseesturm.domain.auth.model.FirebaseHitobitoUser
import ch.seesturm.pfadiseesturm.util.DateTimeUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class FirebaseHitobitoUserDto(
    @DocumentId override var id: String? = null,
    @ServerTimestamp override var created: Timestamp? = null,
    @ServerTimestamp override var modified: Timestamp? = null,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var pfadiName: String? = null
): FirestoreDto {

    override fun <T : FirestoreDto> contentEquals(other: T): Boolean {
        if (other !is FirebaseHitobitoUserDto) return false
        return id == other.id &&
                email == other.email &&
                firstName == other.firstName &&
                lastName == other.lastName &&
                pfadiName == other.pfadiName
    }
}

fun FirebaseHitobitoUserDto.toFirebaseHitobitoUser(): FirebaseHitobitoUser {

    val createdDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(created)
    val modifiedDate = DateTimeUtil.shared.convertFirestoreTimestampToDate(modified)

    return FirebaseHitobitoUser(
        userId = id ?: UUID.randomUUID().toString(),
        vorname = firstName,
        nachname = lastName,
        pfadiname = pfadiName,
        email = email,
        created = createdDate,
        createdFormatted = DateTimeUtil.shared.formatDate(
            date = createdDate,
            format = "EEEE, d. MMMM yyyy",
            withRelativeDateFormatting = true,
            includeTimeInRelativeFormatting = true
        ),
        modified = modifiedDate,
        modifiedFormatted = DateTimeUtil.shared.formatDate(
            date = modifiedDate,
            format = "EEEE, d. MMMM yyyy",
            withRelativeDateFormatting = true,
            includeTimeInRelativeFormatting = true
        )
    )
}