package ch.seesturm.pfadiseesturm.presentation.mehr.fotos

import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhoto

data class PhotoSliderViewItem(
    val url: String,
    val aspectRatio: Float
) {

    companion object {
        fun fromWordpressImage(wordpressPhoto: WordpressPhoto): PhotoSliderViewItem {
            return PhotoSliderViewItem(
                url = wordpressPhoto.originalUrl,
                aspectRatio = wordpressPhoto.width.toFloat() / wordpressPhoto.height.toFloat()
            )
        }
    }
}