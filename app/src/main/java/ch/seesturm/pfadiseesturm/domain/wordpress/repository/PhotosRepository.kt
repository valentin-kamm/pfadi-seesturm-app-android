package ch.seesturm.pfadiseesturm.domain.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPhotoDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPhotoGalleryDto

interface PhotosRepository {

    suspend fun getPfadijahre(): List<WordpressPhotoGalleryDto>
    suspend fun getAlbums(pfadijahrId: String): List<WordpressPhotoGalleryDto>
    suspend fun getPhotos(albumId: String): List<WordpressPhotoDto>
}