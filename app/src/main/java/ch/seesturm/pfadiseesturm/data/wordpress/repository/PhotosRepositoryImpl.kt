package ch.seesturm.pfadiseesturm.data.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPhotoDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPhotoGalleryDto
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.PhotosRepository

class PhotosRepositoryImpl(
    private val api: WordpressApi
): PhotosRepository {

    override suspend fun getPfadijahre(): List<WordpressPhotoGalleryDto> =
        api.getPhotosPfadijahre()

    override suspend fun getAlbums(pfadijahrId: String): List<WordpressPhotoGalleryDto> =
        api.getPhotosAlbums(pfadijahrId)

    override suspend fun getPhotos(albumId: String): List<WordpressPhotoDto> =
        api.getPhotos(albumId)
}