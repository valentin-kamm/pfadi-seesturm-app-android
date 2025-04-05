package ch.seesturm.pfadiseesturm.domain.wordpress.model

data class Leitungsteam(
    val teamName: String,
    val teamId: Int,
    val members: List<LeitungsteamMember>
)
data class LeitungsteamMember(
    val name: String,
    val job: String,
    val contact: String,
    val photo: String
)
