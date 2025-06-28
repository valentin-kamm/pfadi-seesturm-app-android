package ch.seesturm.pfadiseesturm.domain.wordpress.model


data class WordpressPosts(
    val postCount: Int,
    val posts: List<WordpressPost>
)
