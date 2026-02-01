package ch.seesturm.pfadiseesturm.presentation.common.event_management.types

sealed class EventValidationStatusErrorType {
    data class TitleTextField(
        val message: String
    ): EventValidationStatusErrorType()
    data class Snackbar(
        val message: String
    ): EventValidationStatusErrorType()
}