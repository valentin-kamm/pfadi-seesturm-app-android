package ch.seesturm.pfadiseesturm.domain.wordpress.model

data class WordpressPost(
    val id: Int,
    val publishedYear: String,
    val publishedFormatted: String,
    val modifiedFormatted: String,
    val imageUrl: String,
    val title: String,
    val titleDecoded: String,
    val content: String,
    val contentPlain: String,
    val imageAspectRatio: Double,
    val author: String
)

val List<WordpressPost>.groupedByYear: List<Pair<String, List<WordpressPost>>>
    get() = this
        .groupBy { it.publishedYear }
        .toSortedMap(compareByDescending { it })
        .map { (year, posts) -> year to posts }