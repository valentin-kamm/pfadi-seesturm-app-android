package ch.seesturm.pfadiseesturm.domain.wordpress.model

data class Leitungsteam(
    val id: Int,
    val teamName: String,
    val members: List<LeitungsteamMember>
)
