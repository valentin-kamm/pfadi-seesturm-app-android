package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.LeitungsteamMember

data class LeitungsteamMemberDto(
    val name: String,
    val job: String,
    val contact: String,
    val photo: String
)

fun LeitungsteamMemberDto.toLeitungsteamMember(): LeitungsteamMember {
    return LeitungsteamMember(
        name = name,
        job = job,
        contact = contact,
        photo = photo
    )
}