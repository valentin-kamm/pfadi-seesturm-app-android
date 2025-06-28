package ch.seesturm.pfadiseesturm.domain.wordpress.model

data class WordpressPhoto(
    val thumbnailUrl: String,
    val originalUrl: String,
    val orientation: String,
    val height: Int,
    val width: Int
)
