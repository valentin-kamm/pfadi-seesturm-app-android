package ch.seesturm.pfadiseesturm.domain.wordpress.model

import android.util.Patterns

data class WordpressPost(
    val id: Int,
    val publishedYear: String,
    val published: String,
    val modified: String,
    val imageUrl: String,
    val title: String,
    val titleDecoded: String,
    val content: String,
    val contentPlain: String,
    val aspectRatio: Double,
    val author: String
)

fun WordpressPost.hasValidImageUrl(): Boolean {
    return Patterns.WEB_URL.matcher(this.imageUrl).matches()
}

// computed property to group the posts by year
val List<WordpressPost>.groupedByYear: List<Pair<String, List<WordpressPost>>>
    get() = this
        .groupBy { it.publishedYear }
        .toSortedMap(compareByDescending { it })
        .map { (year, posts) -> year to posts }