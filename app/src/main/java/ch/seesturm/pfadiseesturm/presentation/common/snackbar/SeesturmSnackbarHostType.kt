package ch.seesturm.pfadiseesturm.presentation.common.snackbar

sealed interface SeesturmSnackbarHostType {
    data object Default: SeesturmSnackbarHostType
    data class StaticInfoSnackbar(
        val message: String
    ): SeesturmSnackbarHostType
}