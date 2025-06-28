package ch.seesturm.pfadiseesturm.presentation.account.stufenbereich.aktivitaet_bearbeiten

sealed class AktivitaetValidationStatus {
    data object Valid: AktivitaetValidationStatus()
    data class Warning(
        val title: String,
        val description: String
    ): AktivitaetValidationStatus()
    data class Error(
        val type: AktivitaetValidationStatusErrorType
    ): AktivitaetValidationStatus()
}