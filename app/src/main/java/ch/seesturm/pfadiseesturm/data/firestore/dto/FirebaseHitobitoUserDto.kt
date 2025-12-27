package ch.seesturm.pfadiseesturm.data.firestore.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

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

    companion object {

        fun from(oldUser: FirebaseHitobitoUserDto, newProfilePictureUrl: String?): FirebaseHitobitoUserDto {
            return FirebaseHitobitoUserDto(
                id = oldUser.id,
                created = oldUser.created,
                modified = null,
                email = oldUser.email,
                firstname = oldUser.firstname,
                lastname = oldUser.lastname,
                pfadiname = oldUser.pfadiname,
                role = oldUser.role,
                profilePictureUrl = newProfilePictureUrl,
                fcmToken = oldUser.fcmToken
            )
        }
    }
}