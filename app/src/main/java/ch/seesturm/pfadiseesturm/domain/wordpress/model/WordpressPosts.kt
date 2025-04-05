package ch.seesturm.pfadiseesturm.domain.wordpress.model


data class WordpressPosts(
    val totalPosts: Int,
    val posts: List<WordpressPost>
)
