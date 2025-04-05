package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.Leitungsteam
import ch.seesturm.pfadiseesturm.domain.wordpress.model.LeitungsteamMember
import com.google.gson.annotations.SerializedName

data class LeitungsteamDto(
    @SerializedName("team_name") val teamName: String,
    @SerializedName("team_id") val teamId: Int,
    val members: List<LeitungsteamMemberDto>
)
data class LeitungsteamMemberDto(
    val name: String,
    val job: String,
    val contact: String,
    val photo: String
)

fun LeitungsteamDto.toLeitungsteam(): Leitungsteam {
    return Leitungsteam(
        teamName = teamName,
        teamId = teamId,
        members = members.map { it.toLeitungsteamMember() }
    )
}
fun LeitungsteamMemberDto.toLeitungsteamMember(): LeitungsteamMember {
    return LeitungsteamMember(
        name = name,
        job = job,
        contact = contact,
        photo = photo
    )
}