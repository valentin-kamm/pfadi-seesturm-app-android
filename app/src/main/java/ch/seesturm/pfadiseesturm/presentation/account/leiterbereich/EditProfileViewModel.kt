package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.storage.model.PickedGalleryItem
import ch.seesturm.pfadiseesturm.util.state.AnonymousActionState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(): ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    private var imageFromGalleryLoadingJob: Job? = null

    fun updateSelectedImageUri(uri: Uri?, context: Context) {
        _state.update {
            it.copy(
                selectedImageUri = uri
            )
        }
        updateImageSelectionState(context)
    }

    private fun updateImageSelectionState(context: Context) {

        imageFromGalleryLoadingJob?.cancel()
        imageFromGalleryLoadingJob = null

        val uri = state.value.selectedImageUri

        if (uri == null) {
            _state.update {
                it.copy(
                    imageSelectionState = AnonymousActionState.Idle
                )
            }
            return
        }

        _state.update {
            it.copy(
                imageSelectionState = AnonymousActionState.Loading
            )
        }

        imageFromGalleryLoadingJob = viewModelScope.launch {
            try {
                val data = PickedGalleryItem.fromUri(uri, context)
                _state.update {
                    it.copy(
                        imageSelectionState = AnonymousActionState.Success(data, "Das Bild wurde erfolgreich aus der Gallerie geladen.")
                    )
                }
            }
            catch (e: CancellationException) {
                // do nothing if job is cancelled
            }
            catch (e: Exception) {
                _state.update {
                    it.copy(
                        imageSelectionState = AnonymousActionState.Error("Ein unbekannter Fehler ist aufgetreten. ${e.message ?: ""}")
                    )
                }
            }
        }
    }

    fun updateDeleteImageAlertVisibility(isVisible: Boolean) {
        _state.update {
            it.copy(
                showDeleteImageAlert = isVisible
            )
        }
    }
}