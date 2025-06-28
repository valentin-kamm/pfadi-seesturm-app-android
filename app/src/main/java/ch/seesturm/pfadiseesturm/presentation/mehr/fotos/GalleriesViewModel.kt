package ch.seesturm.pfadiseesturm.presentation.mehr.fotos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPhotoGallery
import ch.seesturm.pfadiseesturm.domain.wordpress.service.PhotosService
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GalleriesViewModel(
    private val service: PhotosService,
    private val type: PhotoGalleriesType
): ViewModel() {

    private val _state = MutableStateFlow<UiState<List<WordpressPhotoGallery>>>(
        UiState.Loading)
    val state = _state.asStateFlow()

    init {
        fetchGalleries()
    }

    fun fetchGalleries() {
        _state.update {
            UiState.Loading
        }
        viewModelScope.launch {

            val result = when (type) {
                PhotoGalleriesType.Pfadijahre -> {
                    service.getPfadijahre()
                }
                is PhotoGalleriesType.Albums -> {
                    service.getAlbums(type.id)
                }
            }

            when (result) {
                is SeesturmResult.Error -> {
                    _state.update {
                        UiState.Error("Fotos konnten nicht geladen werden. ${result.error.defaultMessage}")
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        UiState.Success(result.data)
                    }
                }
            }
        }
    }
}