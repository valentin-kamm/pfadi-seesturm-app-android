package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhoto

data class WordpressPhotoDto(
    val thumbnail: String,
    val original: String,
    val orientation: String,
    val height: Int,
    val width: Int
)

fun WordpressPhotoDto.toWordpressPhoto(): WordpressPhoto {
    return WordpressPhoto(
        thumbnail = thumbnail,
        original = original,
        orientation = orientation,
        height = height,
        width = width
    )
}