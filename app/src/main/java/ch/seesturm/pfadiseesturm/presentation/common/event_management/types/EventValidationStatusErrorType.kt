package ch.seesturm.pfadiseesturm.presentation.common.event_management.types

sealed interface EventValidationStatusErrorType {
    data class TitleTextField(
        val message: String
    ): EventValidationStatusErrorType
    data class Snackbar(
        val message: String
    ): EventValidationStatusErrorType
}