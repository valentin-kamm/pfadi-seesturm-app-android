package ch.seesturm.pfadiseesturm.data.auth.dto

import com.google.gson.annotations.SerializedName

data class HitobitoUserRoleDto(
    @SerializedName("group_id") val groupId: Int?,
    @SerializedName("group_name") val groupName: String?,
    val role: String?,
    @SerializedName("role_class") val roleClass: String?,
    @SerializedName("role_name") val roleName: String?,
    val permissions: List<String?>?
)