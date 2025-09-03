package ch.seesturm.pfadiseesturm.presentation.account.leiterbereich

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.seesturm.pfadiseesturm.domain.account.service.LeiterbereichService
import ch.seesturm.pfadiseesturm.domain.storage.model.JPGData
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbar
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SeesturmSnackbarLocation
import ch.seesturm.pfadiseesturm.presentation.common.snackbar.SnackbarController
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.ActionState
import ch.seesturm.pfadiseesturm.util.state.AnonymousActionState
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userId: String,
    private val leiterbereichService: LeiterbereichService
): ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    private var imageFromGalleryLoadingJob: Job? = null

    val isCircularImageViewLoading: Boolean
        get() {
            if (state.value.imageUploadState is ActionState.Loading) {
                return true
            }
            if (state.value.imageDeleteState is ActionState.Loading) {
                return true
            }
            if (state.value.imageSelectionState is AnonymousActionState.Loading) {
                return true
            }
            return false
        }

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
                val jpgData = JPGData.fromUri(uri = uri, context = context)
                _state.update {
                    it.copy(
                        imageSelectionState = AnonymousActionState.Success(jpgData, "Das Bild wurde erfolgreich von der Gallerie geladen.")
                    )
                }
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

    fun uploadProfilePicture(croppingResult: SeesturmResult<JPGData, DataError.Local>) {

        when (croppingResult) {
            is SeesturmResult.Error -> {

                val message = "Das Bild konnte nicht zugeschnitten werden. Versuche es erneut."

                _state.update {
                    it.copy(
                        imageUploadState = ActionState.Error(Unit, message)
                    )
                }
                viewModelScope.launch {
                    SnackbarController.sendSnackbar(
                        SeesturmSnackbar.Error(
                            message = message,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        imageUploadState = ActionState.Idle
                                    )
                                }
                            },
                            location = SeesturmSnackbarLocation.Sheet,
                            allowManualDismiss = true
                        )
                    )
                }
            }
            is SeesturmResult.Success -> {

                _state.update {
                    it.copy(
                        imageUploadState = ActionState.Loading(Unit)
                    )
                }

                viewModelScope.launch {

                    val result = leiterbereichService.uploadProfilePicture(
                        data = croppingResult.data,
                        userId = userId
                    )

                    when (result) {
                        is SeesturmResult.Error -> {

                            _state.update {
                                it.copy(
                                    imageUploadState = ActionState.Error(Unit, result.error.defaultMessage)
                                )
                            }
                            SnackbarController.sendSnackbar(
                                SeesturmSnackbar.Error(
                                    message = result.error.defaultMessage,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                imageUploadState = ActionState.Idle
                                            )
                                        }
                                    },
                                    location = SeesturmSnackbarLocation.Sheet,
                                    allowManualDismiss = true
                                )
                            )
                        }
                        is SeesturmResult.Success -> {

                            val message = "Das Profilbild wurde erfolgreich gespeichert."

                            _state.update {
                                it.copy(
                                    imageUploadState = ActionState.Success(Unit, message)
                                )
                            }
                            SnackbarController.sendSnackbar(
                                SeesturmSnackbar.Success(
                                    message = message,
                                    onDismiss = {
                                        _state.update {
                                            it.copy(
                                                imageUploadState = ActionState.Idle
                                            )
                                        }
                                    },
                                    location = SeesturmSnackbarLocation.Sheet,
                                    allowManualDismiss = true
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun deleteProfilePicture() {

        _state.update {
            it.copy(
                imageDeleteState = ActionState.Loading(Unit)
            )
        }

        viewModelScope.launch {

            when (val result = leiterbereichService.deleteProfilePicture(userId)) {
                is SeesturmResult.Error -> {
                    _state.update {
                        it.copy(
                            imageDeleteState = ActionState.Error(Unit, result.error.defaultMessage)
                        )
                    }
                    SnackbarController.sendSnackbar(
                        SeesturmSnackbar.Error(
                            message = result.error.defaultMessage,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        imageDeleteState = ActionState.Idle
                                    )
                                }
                            },
                            location = SeesturmSnackbarLocation.Sheet,
                            allowManualDismiss = true
                        )
                    )
                }
                is SeesturmResult.Success -> {
                    val message = "Das Profilbild wurde erfolgreich gelöscht."
                    _state.update {
                        it.copy(
                            imageDeleteState = ActionState.Success(Unit, message)
                        )
                    }
                    SnackbarController.sendSnackbar(
                        SeesturmSnackbar.Success(
                            message = message,
                            onDismiss = {
                                _state.update {
                                    it.copy(
                                        imageDeleteState = ActionState.Idle
                                    )
                                }
                            },
                            location = SeesturmSnackbarLocation.Sheet,
                            allowManualDismiss = true
                        )
                    )
                }
            }
        }
    }

    fun updateDeleteImageAlertVisibility(show: Boolean) {
        _state.update {
            it.copy(
                showDeleteImageAlert = show
            )
        }
    }
}