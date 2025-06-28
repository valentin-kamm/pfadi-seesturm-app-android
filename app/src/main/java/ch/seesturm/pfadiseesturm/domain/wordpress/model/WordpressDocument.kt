package ch.seesturm.pfadiseesturm.domain.wordpress.model

data class WordpressDocument(
    val id: String,
    val thumbnailUrl: String,
    val thumbnailWidth: Int,
    val thumbnailHeight: Int,
    val title: String,
    val documentUrl: String,
    val published: String
)
