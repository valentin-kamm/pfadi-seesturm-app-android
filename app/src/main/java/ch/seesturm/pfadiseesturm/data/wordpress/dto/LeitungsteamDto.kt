package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.Leitungsteam
import com.google.gson.annotations.SerializedName

data class LeitungsteamDto(
    @SerializedName("team_name") val teamName: String,
    @SerializedName("team_id") val teamId: Int,
    val members: List<LeitungsteamMemberDto>
)

fun LeitungsteamDto.toLeitungsteam(): Leitungsteam {
    return Leitungsteam(
        id = teamId,
        teamName = teamName,
        members = members.map { it.toLeitungsteamMember() }
    )
}
