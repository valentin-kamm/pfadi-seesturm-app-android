package ch.seesturm.pfadiseesturm.presentation.mehr.fotos

import kotlinx.serialization.Serializable

@Serializable
sealed class PhotoGalleriesType {
    @Serializable
    data object Pfadijahre: PhotoGalleriesType()
    @Serializable
    data class Albums(
        val id: String,
        val name: String
    ): PhotoGalleriesType()
}