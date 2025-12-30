package ch.seesturm.pfadiseesturm.data.firestore.dto

import ch.seesturm.pfadiseesturm.data.auth.dto.HitobitoUserInfoDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class FirebaseHitobitoUserInfoDto(

    @DocumentId override var id: String? = null,
    @ServerTimestamp override var created: Timestamp? = null,
    @ServerTimestamp override var modified: Timestamp? = null,
    var email: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var pfadiname: String? = null,
    var role: String = "",
    val fcmToken: String? = null
): FirestoreDto {

    override fun <T : FirestoreDto> contentEquals(other: T): Boolean {
        if (other !is FirebaseHitobitoUserInfoDto) return false
        return id == other.id &&
                email == other.email &&
                firstname == other.firstname &&
                lastname == other.lastname &&
                pfadiname == other.pfadiname &&
                role == other.role &&
                fcmToken == other.fcmToken
    }

    companion object {
        fun from(info: HitobitoUserInfoDto, role: String, fcmToken: String): FirebaseHitobitoUserInfoDto {
            return FirebaseHitobitoUserInfoDto(
                id = info.sub,
                email = info.email,
                firstname = info.firstName,
                lastname = info.lastName,
                pfadiname = info.nickname,
                role = role,
                fcmToken = fcmToken
            )
        }
    }
}