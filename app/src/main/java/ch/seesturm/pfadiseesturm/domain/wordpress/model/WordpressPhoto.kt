package ch.seesturm.pfadiseesturm.domain.wordpress.model

data class WordpressPhoto(
    val thumbnail: String,
    val original: String,
    val orientation: String,
    val height: Int,
    val width: Int
)
