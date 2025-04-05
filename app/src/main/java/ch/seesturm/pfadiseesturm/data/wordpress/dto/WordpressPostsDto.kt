package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPosts
import com.google.gson.annotations.SerializedName

data class WordpressPostsDto(
    @SerializedName("total_posts") val totalPosts: Int,
    val posts: List<WordpressPostDto>
)

fun WordpressPostsDto.toWordpressPosts(): WordpressPosts {
    return WordpressPosts(
        totalPosts = totalPosts,
        posts = posts.map { it.toWordpressPost() }
    )
}