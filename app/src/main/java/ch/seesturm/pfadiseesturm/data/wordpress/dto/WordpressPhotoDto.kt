package ch.seesturm.pfadiseesturm.data.wordpress.dto

import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhoto

data class WordpressPhotoDto(
    val thumbnail: String,
    val original: String,
    val orientation: String,
    val height: Int?,
    val width: Int?
)

fun List<WordpressPhotoDto>.toWordpressPhotos(): List<WordpressPhoto> {
    return this.mapNotNull { photo ->
        if (photo.height != null && photo.width != null) {
            WordpressPhoto(
                thumbnailUrl = photo.thumbnail,
                originalUrl = photo.original,
                orientation = photo.orientation,
                height = photo.height,
                width = photo.width
            )
        }
        else {
            null
        }
    }
}

/*
fun WordpressPhotoDto.toWordpressPhoto(): WordpressPhoto {
    return WordpressPhoto(
        thumbnailUrl = thumbnail,
        originalUrl = original,
        orientation = orientation,
        height = height ?: 9,
        width = width ?: 16
    )
}

 */