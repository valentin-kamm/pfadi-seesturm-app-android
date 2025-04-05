package ch.seesturm.pfadiseesturm.data.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPhotoDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPhotoGalleryDto
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.PhotosRepository

class PhotosRepositoryImpl(
    private val api: WordpressApi
): PhotosRepository {

    override suspend fun getPfadijahre(): List<WordpressPhotoGalleryDto> {
        return api.getPhotosPfadijahre()
    }
    override suspend fun getAlbums(id: String): List<WordpressPhotoGalleryDto> {
        return api.getPhotosAlbums(id)
    }
    override suspend fun getPhotos(id: String): List<WordpressPhotoDto> {
        return api.getPhotos(id)
    }
}