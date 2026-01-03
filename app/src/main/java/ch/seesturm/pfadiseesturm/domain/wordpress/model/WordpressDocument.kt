package ch.seesturm.pfadiseesturm.domain.wordpress.model

import java.time.ZonedDateTime

data class WordpressDocument(
    val id: String,
    val thumbnailUrl: String,
    val thumbnailWidth: Int,
    val thumbnailHeight: Int,
    val title: String,
    val documentUrl: String,
    val published: ZonedDateTime,
    val publishedFormatted: String
)
