package ch.seesturm.pfadiseesturm.data.firestore.dto

import ch.seesturm.pfadiseesturm.data.auth.dto.HitobitoUserInfoDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class FirebaseHitobitoUserDto (

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

    companion object {
        fun create(dto: HitobitoUserInfoDto, role: String, fcmToken: String): FirebaseHitobitoUserDto {
            return FirebaseHitobitoUserDto(
                id = dto.sub,
                email = dto.email,
                firstname = dto.firstName,
                lastname = dto.lastName,
                pfadiname = dto.nickname,
                role = role,
                fcmToken = fcmToken
            )
        }
        fun copyAndUpdateFcmToken(oldDto: FirebaseHitobitoUserDto, newToken: String): FirebaseHitobitoUserDto {
            return FirebaseHitobitoUserDto(
                id = oldDto.id,
                created = oldDto.created,
                modified = null,
                email = oldDto.email,
                firstname = oldDto.firstname,
                lastname = oldDto.lastname,
                pfadiname = oldDto.pfadiname,
                role = oldDto.role,
                profilePictureUrl = oldDto.profilePictureUrl,
                fcmToken = newToken
            )
        }
        fun copyAndUpdateProfilePicture(oldDto: FirebaseHitobitoUserDto, newProfilePictureUrl: String): FirebaseHitobitoUserDto {
            return FirebaseHitobitoUserDto(
                id = oldDto.id,
                created = oldDto.created,
                modified = null,
                email = oldDto.email,
                firstname = oldDto.firstname,
                lastname = oldDto.lastname,
                pfadiname = oldDto.pfadiname,
                role = oldDto.role,
                profilePictureUrl = newProfilePictureUrl,
                fcmToken = oldDto.fcmToken
            )
        }
    }

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