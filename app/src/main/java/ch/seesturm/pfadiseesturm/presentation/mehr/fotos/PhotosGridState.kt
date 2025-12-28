package ch.seesturm.pfadiseesturm.presentation.mehr.fotos

import android.graphics.Bitmap
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhoto
import ch.seesturm.pfadiseesturm.util.state.UiState

data class PhotosGridState(
    val result: UiState<List<WordpressPhoto>> = UiState.Loading
)
