package ch.seesturm.pfadiseesturm.presentation.common.snackbar

sealed interface SeesturmSnackbarLocation {

    data object Default: SeesturmSnackbarLocation
    data object Sheet: SeesturmSnackbarLocation
}