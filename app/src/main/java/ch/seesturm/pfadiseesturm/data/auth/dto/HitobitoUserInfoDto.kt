package ch.seesturm.pfadiseesturm.data.auth.dto

import ch.seesturm.pfadiseesturm.data.firestore.dto.FirebaseHitobitoUserDto
import com.google.gson.annotations.SerializedName

data class HitobitoUserInfoDto(
    val sub: String,
    val email: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    val nickname: String?,
    val street: String?,
    val housenumber: String?,
    @SerializedName("zip_code") val zipCode: String?,
    val town: String?,
    val country: String?,
    val gender: String?,
    val birthday: String?,
    @SerializedName("primary_group_id") val primaryGroupId: Int?,
    val language: String?,
    @SerializedName("kantonalverband_id") val kantonalverbandId: Int?,
    val roles: List<HitobitoUserRoleDto?>?
)

fun HitobitoUserInfoDto.toFirebaseHitobitoUserDto(role: String): FirebaseHitobitoUserDto {
    return FirebaseHitobitoUserDto(
        id = sub,
        email = email,
        firstname = firstName,
        lastname = lastName,
        pfadiname = nickname,
        role = role
    )
}