package ch.seesturm.pfadiseesturm.presentation.mehr.fotos

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.wordpress.service.PhotosService
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.util.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotosGridViewModel(
    private val service: PhotosService,
    private val albumId: String
): ViewModel() {

    private val _state = MutableStateFlow(PhotosGridState())
    val state = _state.asStateFlow()

    init {
        fetchPhotos()
    }

    val pageTitle: String
        get() = when (val localState = state.value.result) {
            is UiState.Success -> "${state.value.selectedImageIndex + 1} von ${localState.data.count()}"
                else -> ""
        }
    val currentImageForSharing: Bitmap?
        get() = state.value.imagesForSharing[state.value.selectedImageIndex]

    fun fetchPhotos() {

        _state.update {
            it.copy(
                result = UiState.Loading
            )
        }
        viewModelScope.launch {
            when (val result = service.getPhotos(albumId)) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            result = UiState.Error("Fotos konnten nicht geladen werden. ${result.error.defaultMessage}")
                        )
                    }
                }
                is SeesturmResult.Success -> {
                    _state.update {
                        it.copy(
                            result = UiState.Success(result.data)
                        )
                    }
                }
            }
        }
    }

    fun setSelectedImageIndex(index: Int) {
        _state.update {
            it.copy(
                selectedImageIndex = index
            )
        }
    }
    fun saveBitmapForSharing(index: Int, bitmap: Bitmap) {
        _state.update {
            val newMap = it.imagesForSharing.toMutableMap()
            newMap[index] = bitmap
            it.copy(
                imagesForSharing = newMap
            )
        }
    }
}
