package ch.seesturm.pfadiseesturm.presentation.common.snackbar

sealed class SeesturmSnackbarLocation {
    data object Default: SeesturmSnackbarLocation()
    data object Sheet: SeesturmSnackbarLocation()
}