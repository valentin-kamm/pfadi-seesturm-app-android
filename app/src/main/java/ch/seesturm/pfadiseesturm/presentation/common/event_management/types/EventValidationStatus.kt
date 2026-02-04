package ch.seesturm.pfadiseesturm.presentation.common.event_management.types

sealed interface EventValidationStatus {
    data object Valid: EventValidationStatus
    data class Warning(
        val title: String,
        val description: String
    ): EventValidationStatus
    data class Error(
        val type: EventValidationStatusErrorType
    ): EventValidationStatus
}