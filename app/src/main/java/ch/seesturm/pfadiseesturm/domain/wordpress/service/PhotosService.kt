package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWordpressPhoto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWordpressPhotoGallery
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhoto
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhotoGallery
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.PhotosRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

class PhotosService(
    private val repository: PhotosRepository
): WordpressService() {

    suspend fun getPfadijahre(): SeesturmResult<List<WordpressPhotoGallery>, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getPfadijahre() },
            transform = { list ->
                list.map { pfadijahr ->
                    pfadijahr.toWordpressPhotoGallery()
                }
            }
        )

    suspend fun getAlbums(pfadijahrId: String): SeesturmResult<List<WordpressPhotoGallery>, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getAlbums(pfadijahrId) },
            transform = { list ->
                list.map { album ->
                    album.toWordpressPhotoGallery()
                }
            }
        )

    suspend fun getPhotos(albumId: String): SeesturmResult<List<WordpressPhoto>, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getPhotos(albumId) },
            transform = { list ->
                list.map { photo ->
                    photo.toWordpressPhoto()
                }
            }
        )
}