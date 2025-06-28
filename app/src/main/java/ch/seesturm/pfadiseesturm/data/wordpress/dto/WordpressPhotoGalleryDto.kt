package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhotoGallery

data class WordpressPhotoGalleryDto(
    val title: String,
    val id: String,
    val thumbnail: String
)

fun WordpressPhotoGalleryDto.toWordpressPhotoGallery(): WordpressPhotoGallery {
    return WordpressPhotoGallery(
        title = title,
        id = id,
        thumbnailUrl = thumbnail
    )
}